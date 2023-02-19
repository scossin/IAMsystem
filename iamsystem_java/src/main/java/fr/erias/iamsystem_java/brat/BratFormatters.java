package fr.erias.iamsystem_java.brat;

import fr.erias.iamsystem_java.tokenize.IOffsets;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.Offsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BratFormatters {

  public static IBratFormatterF tokenFormatter =
      (annot) -> {
        List<List<IToken>> sequences = groupContinuousSeq(annot.tokens());
        String tokensLabel = IToken.ConcatLabel(annot.tokens());
        List<IOffsets> offsets_seq = multipleSeqToOffsets(sequences);
        String offsets = getBratFormat(offsets_seq);
        BratFormat bratForm = new BratFormat(tokensLabel, offsets);
        return bratForm;
      };

  /**
   * Group continuous sequences. From a sequence of tokens, group tokens that follow each other by
   * their indice. Ex: [1,2,3,5,6] => [[1,2,3], [5,6]]
   *
   * @return
   */
  protected static List<List<IToken>> groupContinuousSeq(List<? extends IToken> tokens) {
    if (tokens.size() == 0) return new ArrayList<List<IToken>>();
    tokens.sort(Comparator.naturalOrder());
    List<IToken> seq = new ArrayList<IToken>();
    seq.add(tokens.get(0));
    List<List<IToken>> sequences = new ArrayList<List<IToken>>();
    sequences.add(seq);
    for (int i = 1; i < tokens.size(); i++) {
      IToken token = tokens.get(i);
      IToken lastToken = seq.get(seq.size() - 1);
      if ((lastToken.i() + 1) == token.i()) { // continuous sequence
        seq.add(token);
      } else { // discontinuous sequence
        seq = new ArrayList<IToken>();
        seq.add(token);
        sequences.add(seq);
      }
    }
    return sequences;
  }

  /**
   * Create an Offsets for each continuous sequence, start being the start offset of the first token
   * in the sequence and end being the end offset of the last token in the sequence.
   *
   * @param sequences multiple continuous sequences
   * @return a list of {@link Offsets}.
   */
  protected static List<IOffsets> multipleSeqToOffsets(List<List<IToken>> sequences) {
    return sequences.stream()
        .map(
            t -> {
              int start = t.get(0).start();
              int end = t.get(t.size() - 1).end();
              IOffsets offsets = new Offsets(start, end);
              return offsets;
            })
        .collect(Collectors.toList());
  }

  protected static String getBratFormat(IOffsets offsets) {
    return String.format("%d %d", offsets.start(), offsets.end());
  }

  protected static String getBratFormat(List<IOffsets> offsets) {
    return offsets.stream().map(offset -> getBratFormat(offset)).collect(Collectors.joining(";"));
  }
}
