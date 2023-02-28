package fr.erias.iamsystem_java.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.liblevenshtein.transducer.Algorithm;
import com.github.liblevenshtein.transducer.Candidate;
import com.github.liblevenshtein.transducer.ITransducer;

import fr.erias.iamsystem_java.fuzzy.CacheFuzzyAlgos;
import fr.erias.iamsystem_java.fuzzy.FuzzyRegex;
import fr.erias.iamsystem_java.fuzzy.abbreviations.Abbreviations;
import fr.erias.iamsystem_java.fuzzy.base.IWord2ignore;
import fr.erias.iamsystem_java.fuzzy.base.NoWord2ignore;
import fr.erias.iamsystem_java.fuzzy.base.SimpleWords2ignore;
import fr.erias.iamsystem_java.fuzzy.closestSubString.ClosestSubString;
import fr.erias.iamsystem_java.fuzzy.levenshtein.Levenshtein;
import fr.erias.iamsystem_java.fuzzy.normfun.WordNormalizer;
import fr.erias.iamsystem_java.fuzzy.troncation.PrefixTrie;
import fr.erias.iamsystem_java.fuzzy.troncation.Troncation;
import fr.erias.iamsystem_java.keywords.IKeyword;
import fr.erias.iamsystem_java.keywords.Keyword;
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.stopwords.NegativeStopwords;
import fr.erias.iamsystem_java.stopwords.NoStopwords;
import fr.erias.iamsystem_java.stopwords.Stopwords;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.INormalizeF;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;

public class MatcherBuilder
{

	private ITokenizer tokenizer;
	private IStopwords stopwords;
	private Collection<IKeyword> keywords = new ArrayList<IKeyword>();
	private int w = 1;
	private boolean removeNestedAnnot = true;
	private IWord2ignore word2ignore = new NoWord2ignore();
	private List<String> shortForms = new ArrayList<String>();
	private List<String> longForms = new ArrayList<String>();
	private int minNbcharLeven;
	private int maxDistanceLeven;
	private Algorithm algorithmLeven;
	private boolean negativeStopwords = false;
	private int minPrefixLengthClosest = -1;
	private int maxDistanceClosest = -1;
	private int minPrefixLengthTroncation;
	private int maxDistanceTroncation;
	private List<WordNormalizer> wordNormalizers = new ArrayList<WordNormalizer>();
	private List<FuzzyRegex> fuzzyRegex = new ArrayList<FuzzyRegex>();

	public MatcherBuilder()
	{
	}

	public MatcherBuilder abbreviations(String shortForm, String longForm)
	{
		this.shortForms.add(shortForm);
		this.longForms.add(longForm);
		return this;
	}

	public Matcher build()
	{
		ITokenizer tokenizer = (this.tokenizer != null) ? this.tokenizer
				: (ITokenizer) TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		IStopwords stopwords = (this.stopwords != null) ? this.stopwords : (IStopwords) new NoStopwords();
		Matcher matcher = new Matcher(tokenizer, stopwords);
		matcher.setW(this.w);
		matcher.setRemoveNestedAnnot(this.removeNestedAnnot);
		matcher.addKeyword(this.keywords);
		if (this.negativeStopwords)
		{
			matcher.setStopwords(new NegativeStopwords());
		}
		CacheFuzzyAlgos cache = new CacheFuzzyAlgos("cache");
		matcher.addFuzzyAlgo(cache);
		if (this.algorithmLeven != null)
		{
			ITransducer<Candidate> transducer = Levenshtein.buildTransuder(this.maxDistanceLeven, matcher,
					this.algorithmLeven);
			Levenshtein leven = new Levenshtein("levenshtein", this.minNbcharLeven, word2ignore, transducer);
			cache.addFuzzyAlgo(leven);
		}
		if (this.shortForms.size() != 0)
		{

			Abbreviations abbs = new Abbreviations("abbs");
			matcher.addFuzzyAlgo(abbs);
			for (int i = 0; i < this.shortForms.size(); i++)
			{
				String shortForm = this.shortForms.get(i);
				String longForm = this.longForms.get(i);
				abbs.add(shortForm, longForm, matcher);
			}
		}
		if (this.minPrefixLengthClosest != -1)
		{
			PrefixTrie trie = new PrefixTrie(minPrefixLengthClosest);
			trie.addToken(matcher.getUnigrams());
			ClosestSubString closest = new ClosestSubString("closest", trie, this.maxDistanceClosest);
			cache.addFuzzyAlgo(closest);
		}

		if (this.minPrefixLengthTroncation != -1)
		{
			PrefixTrie trie = new PrefixTrie(minPrefixLengthClosest);
			trie.addToken(matcher.getUnigrams());
			Troncation troncation = new Troncation("troncation", trie, this.maxDistanceTroncation);
			cache.addFuzzyAlgo(troncation);
		}


		for(WordNormalizer normalizer : wordNormalizers) {
			matcher.addFuzzyAlgo(normalizer);
			normalizer.add(matcher.getUnigrams());
		}
		
		for (FuzzyRegex fuzzyRegex : fuzzyRegex) {
			matcher.addFuzzyAlgo(fuzzyRegex);
		}


		return matcher;
	}

	public Matcher build(ITokenizer tokenizer)
	{
		this.tokenizer = tokenizer;
		return build();
	}

	public MatcherBuilder closestSubString(int minPrefixLength, int maxDistance)
	{
		this.minPrefixLengthClosest = minPrefixLength;
		this.maxDistanceClosest = maxDistance;
		return this;
	}

	public MatcherBuilder keywords(Collection<IKeyword> keywords)
	{
		keywords.addAll(keywords);
		return this;
	}

	public MatcherBuilder keywords(String... labels)
	{
		for (String label : labels)
		{
			IKeyword kw = new Keyword(label);
			this.keywords.add(kw);
		}
		return this;
	}

	public MatcherBuilder levenshtein(int minNbChar, int maxDistance, Algorithm algorithm)
	{
		this.minNbcharLeven = minNbChar;
		this.maxDistanceLeven = maxDistance;
		this.algorithmLeven = algorithm;
		return this;
	}

	public MatcherBuilder negativeStopwords(boolean negativeStopwords)
	{
		this.negativeStopwords = negativeStopwords;
		return this;
	}

	public MatcherBuilder wordNormalizer(String name, INormalizeF normfun)
	{
		WordNormalizer normalizer = new WordNormalizer(name, normfun);
		this.wordNormalizers .add(normalizer);
		return this;
	}

	public MatcherBuilder stopwords(Collection<String> stopwords)
	{
		this.stopwords = new Stopwords(stopwords);
		return this;
	}

	public MatcherBuilder stopwords(IStopwords stopwords)
	{
		this.stopwords = stopwords;
		return this;
	}

	public MatcherBuilder stopwords(String... words)
	{
		Stopwords stopwords = new Stopwords();
		for (String stopword : words)
		{
			stopwords.add(stopword);
		}
		this.stopwords = stopwords;
		return this;
	}

	public MatcherBuilder stringDistanceWords2ignore(Collection<String> words2ignore)
	{
		this.word2ignore = new SimpleWords2ignore(words2ignore);
		return this;
	}

	public MatcherBuilder tokenizer(ITokenizer tokenizer)
	{
		this.tokenizer = tokenizer;
		return this;
	}

	public MatcherBuilder troncation(int minPrefixLength, int maxDistance)
	{
		this.minPrefixLengthTroncation = minPrefixLength;
		this.maxDistanceTroncation = maxDistance;
		return this;
	}


	public MatcherBuilder fuzzyRegex(String name, String pattern, String patternName)
	{
		FuzzyRegex fuzzy = new FuzzyRegex(name, pattern, patternName);
		this.fuzzyRegex.add(fuzzy);
		return this;
	}

	public MatcherBuilder w(boolean removeNestedAnnot)
	{
		this.removeNestedAnnot = removeNestedAnnot;
		return this;
	}

	public MatcherBuilder w(int w)
	{
		this.w = w;
		return this;
	}
}
