package fr.erias.iamsystem.fuzzy.levenshtein;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.liblevenshtein.transducer.Algorithm;
import com.github.liblevenshtein.transducer.Candidate;
import com.github.liblevenshtein.transducer.ITransducer;
import com.github.liblevenshtein.transducer.factory.TransducerBuilder;

import fr.erias.iamsystem.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem.fuzzy.base.StringDistance;
import fr.erias.iamsystem.fuzzy.base.SynAlgo;
import fr.erias.iamsystem.matcher.Matcher;

/**
 * A fuzzy algorithm based on the Levenshtein distance.
 *
 * @author Sebastien Cossin
 *
 */
public class Levenshtein extends StringDistance
{

	/**
	 * Create a {@link ITransducer} to compute the Levenshtein distanc.
	 * 
	 * @param maxDistance Levenshtein distance.
	 * @param unigrams    a collection of unigrams (in general from the dictionary
	 *                    of keywords).
	 * @param algorithm   one of the avaiable Levenshtein {@link Algorithm}.
	 * @return a {@link ITransducer}
	 */
	public static ITransducer<Candidate> buildTransuder(int maxDistance, Collection<String> unigrams,
			Algorithm algorithm)
	{
		// unigrams must be ordered.
		List<String> unigramsOrdered = new ArrayList<String>(unigrams);
		unigramsOrdered.sort(Comparator.naturalOrder());
		ITransducer<Candidate> transducer = new TransducerBuilder().algorithm(algorithm)
				.defaultMaxDistance(maxDistance)
				.dictionary(unigramsOrdered, true)
				.build();
		return transducer;
	}

	/**
	 * Create a {@link ITransducer} to compute the Levenshtein distance.
	 * 
	 * @param maxDistance Levenshtein distance.
	 * @param matcher     the IAMsystem {@link Matcher}.
	 * @param algorithm   one of the avaiable Levenshtein {@link Algorithm}.
	 * @return a {@link ITransducer}
	 */
	public static ITransducer<Candidate> buildTransuder(int maxDistance, Matcher matcher, Algorithm algorithm)
	{
		return buildTransuder(maxDistance, matcher.getUnigrams(), algorithm);
	}

	private final ITransducer<Candidate> transducer;

	/**
	 * Create a fuzzy algorithm based on the Levenshtein distance.
	 * 
	 * @param name       A name given to this algorithm.
	 * @param minNbChar  the minimum number of characters a word must have in order
	 *                   not to be ignored.
	 * @param transducer a {@link ITransducer} to compute the Levenshtein distance.
	 */
	public Levenshtein(String name, int minNbChar, ITransducer<Candidate> transducer)
	{
		super(name, minNbChar);
		this.transducer = transducer;
	}

	@Override
	public List<SynAlgo> getSynsOfWord(String normLabel)
	{
		if (this.isAword2ignore(normLabel))
			return FuzzyAlgo.NO_SYN;
		Set<String> similarWords = new HashSet<String>();
		for (final Candidate candidate : transducer.transduce(normLabel))
		{
			similarWords.add(candidate.term());
		}
		return this.words2syn(similarWords);
	}
}
