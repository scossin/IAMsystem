package fr.erias.iamsystem_java.brat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.annotation.IAnnotation;
import fr.erias.iamsystem_java.tokenize.IOffsets;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.Offsets;

public class BratFormatters
{

	public static IBratFormatterF contSeqFormatter = (annot) ->
	{
		List<List<IToken>> sequences = groupContinuousSeq(annot.tokens());
		return getBratForm(annot, sequences);
	};

	public static IBratFormatterF defaultFormatter = contSeqFormatter;

	public static IBratFormatterF contSeqStopFormatter = (annot) ->
	{
		List<IToken> allTokens = new ArrayList<>();
		allTokens.addAll(annot.tokens());
		allTokens.addAll(annot.stopTokens());
		allTokens.sort(Comparator.naturalOrder());
		List<List<IToken>> sequences = groupContinuousSeq(allTokens);
		Set<Integer> stopIndices = annot.stopTokens().stream().map(token -> token.i()).collect(Collectors.toSet());
		sequences = removeTrailingStopwords(sequences, stopIndices);
		return getBratForm(annot, sequences);
	};

	public static IBratFormatterF tokenFormatter = (annot) ->
	{
		String tokensLabel = IToken.ConcatLabel(annot.tokens());
		String offsets = getBratFormat(annot.tokens());
		BratFormat bratForm = new BratFormat(tokensLabel, offsets);
		return bratForm;
	};

	public static IBratFormatterF spanFormatter = (annot) ->
	{
		String tokensLabel = getTextSpan(annot.getText(), annot);
		String offsets = getBratFormat(annot);
		BratFormat bratForm = new BratFormat(tokensLabel, offsets);
		return bratForm;
	};

	protected static BratFormat getBratForm(IAnnotation annot, List<List<IToken>> sequences)
	{
		List<IOffsets> offsets = multipleSeqToOffsets(sequences);
		String seq_offsets = getBratFormat(offsets);
		String seqLabel = offsets.stream().map(o -> getTextSpan(annot.getText(), o)).collect(Collectors.joining(" "));
		BratFormat bratForm = new BratFormat(seqLabel, seq_offsets);
		return bratForm;
	}

	protected static String getBratFormat(IOffsets offsets)
	{
		return String.format("%d %d", offsets.start(), offsets.end());
	}

	protected static String getBratFormat(List<? extends IOffsets> offsets)
	{
		return offsets.stream().map(offset -> getBratFormat(offset)).collect(Collectors.joining(";"));
	}

	protected static String getTextSpan(String text, IOffsets offsets)
	{
		return text.substring(offsets.start(), offsets.end());
	}

	/**
	 * Group continuous sequences. From a sequence of tokens, group tokens that
	 * follow each other by their indice. Ex: [1,2,3,5,6] => [[1,2,3], [5,6]]
	 *
	 * @return
	 */
	protected static List<List<IToken>> groupContinuousSeq(List<? extends IToken> tokens)
	{
		if (tokens.size() == 0)
			return new ArrayList<List<IToken>>();
		tokens.sort(Comparator.naturalOrder());
		List<IToken> seq = new ArrayList<IToken>();
		seq.add(tokens.get(0));
		List<List<IToken>> sequences = new ArrayList<List<IToken>>();
		sequences.add(seq);
		for (int i = 1; i < tokens.size(); i++)
		{
			IToken token = tokens.get(i);
			IToken lastToken = seq.get(seq.size() - 1);
			if ((lastToken.i() + 1) == token.i())
			{ // continuous sequence
				seq.add(token);
			} else
			{ // discontinuous sequence
				seq = new ArrayList<IToken>();
				seq.add(token);
				sequences.add(seq);
			}
		}
		return sequences;
	}

	/**
	 * Create an Offsets for each continuous sequence, start being the start offset
	 * of the first token in the sequence and end being the end offset of the last
	 * token in the sequence.
	 *
	 * @param sequences multiple continuous sequences
	 * @return a list of {@link Offsets}.
	 */
	protected static List<IOffsets> multipleSeqToOffsets(List<List<IToken>> sequences)
	{
		return sequences.stream().map(t ->
		{
			int start = t.get(0).start();
			int end = t.get(t.size() - 1).end();
			IOffsets offsets = new Offsets(start, end);
			return offsets;
		}).collect(Collectors.toList());
	}

	protected static List<List<IToken>> removeTrailingStopwords(List<List<IToken>> sequences, Set<Integer> stopIndices)
	{
		List<List<IToken>> outSeq = new ArrayList<List<IToken>>();
		for (List<IToken> seq : sequences)
		{
			List<IToken> seqWithoutStop = seq.stream()
					.filter(token -> !stopIndices.contains(token.i()))
					.collect(Collectors.toList());
			if (seqWithoutStop.size() == 0)
				continue;
			int lastIndice = seqWithoutStop.get(seqWithoutStop.size() - 1).i();
			List<IToken> seqWithoutTrailingStop = seq.stream()
					.filter(token -> token.i() <= lastIndice)
					.collect(Collectors.toList());
			outSeq.add(seqWithoutTrailingStop);
		}
		return outSeq;
	}
}
