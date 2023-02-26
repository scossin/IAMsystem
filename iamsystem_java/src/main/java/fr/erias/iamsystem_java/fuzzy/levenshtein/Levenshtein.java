package fr.erias.iamsystem_java.fuzzy.levenshtein;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.liblevenshtein.transducer.Algorithm;
import com.github.liblevenshtein.transducer.Candidate;
import com.github.liblevenshtein.transducer.ITransducer;
import com.github.liblevenshtein.transducer.factory.TransducerBuilder;

import fr.erias.iamsystem_java.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.base.IWord2ignore;
import fr.erias.iamsystem_java.fuzzy.base.NoWord2ignore;
import fr.erias.iamsystem_java.fuzzy.base.StringDistance;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.tokenize.IToken;

public class Levenshtein<T extends IToken> extends StringDistance<T>
{

	public static ITransducer<Candidate> buildTransuder(int maxDistance, Collection<String> unigrams,
			Algorithm algorithm)
	{
		ITransducer<Candidate> transducer = new TransducerBuilder().algorithm(algorithm).defaultMaxDistance(maxDistance)
				.dictionary(unigrams, true).build();
		return transducer;
	}

	public static ITransducer<Candidate> buildTransuder(int maxDistance, Matcher<?> matcher, Algorithm algorithm)
	{
		return buildTransuder(maxDistance, matcher.getUnigrams(), algorithm);
	}

	private final ITransducer<Candidate> transducer;

	public Levenshtein(String name, int minNbChar, ITransducer<Candidate> transducer)
	{
		this(name, minNbChar, new NoWord2ignore(), transducer);
	}

	public Levenshtein(String name, int minNbChar, IWord2ignore word2ignore, ITransducer<Candidate> transducer)
	{
		super(name, minNbChar, word2ignore);
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
