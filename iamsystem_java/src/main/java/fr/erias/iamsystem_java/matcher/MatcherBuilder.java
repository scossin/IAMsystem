package fr.erias.iamsystem_java.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

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
import fr.erias.iamsystem_java.fuzzy.encoder.StringEncoderSyn;
import fr.erias.iamsystem_java.fuzzy.levenshtein.Levenshtein;
import fr.erias.iamsystem_java.fuzzy.normfun.WordNormalizer;
import fr.erias.iamsystem_java.fuzzy.troncation.PrefixTrie;
import fr.erias.iamsystem_java.fuzzy.troncation.Troncation;
import fr.erias.iamsystem_java.keywords.IKeyword;
import fr.erias.iamsystem_java.keywords.Keyword;
import fr.erias.iamsystem_java.matcher.strategy.EMatchingStrategy;
import fr.erias.iamsystem_java.matcher.strategy.IMatchingStrategy;
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.stopwords.IWord2keepF;
import fr.erias.iamsystem_java.stopwords.NegativeStopwords;
import fr.erias.iamsystem_java.stopwords.NoStopwords;
import fr.erias.iamsystem_java.stopwords.Stopwords;
import fr.erias.iamsystem_java.stopwords.Word2KeepFuzzy;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.INormalizeF;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.OrderTokensTokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;

/**
 * Main public API to build a matcher that will annotate a document with
 * IAMsystem algorithm.
 *
 *
 * @author Sebastien Cossin
 *
 */
public class MatcherBuilder
{

	private ITokenizer tokenizer;
	private boolean orderTokens = false;
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
	private boolean setNegativeStopwords = false;
	private int minPrefixLengthClosest = -1;
	private int maxDistanceClosest = -1;
	private int minNbCharTroncation;
	private int maxDistanceTroncation;
	private List<WordNormalizer> wordNormalizers = new ArrayList<WordNormalizer>();
	private List<FuzzyRegex> fuzzyRegex = new ArrayList<FuzzyRegex>();
	private List<StringEncoderSyn> stringEncoders = new ArrayList<StringEncoderSyn>();
	private IMatchingStrategy strategy = EMatchingStrategy.WindowStrategy.getInstance();

	/**
	 * Start building an IAMsystem matcher to annotate documents.
	 */
	public MatcherBuilder()
	{
	}

	/**
	 * Add an abbreviation.
	 *
	 * @param shortForm an abbreviation short form.
	 * @param longForm  an abbreviation long form.
	 * @return the builder instance.
	 */
	public MatcherBuilder abbreviations(String shortForm, String longForm)
	{
		this.shortForms.add(shortForm);
		this.longForms.add(longForm);
		return this;
	}

	/**
	 * Call this function to construct a new {@link Matcher} instance.
	 *
	 * @return a Matcher to annotate a document.
	 */
	public Matcher build()
	{
		ITokenizer tokenizer = (this.tokenizer != null) ? this.tokenizer
				: (ITokenizer) TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		if (orderTokens)
		{
			tokenizer = new OrderTokensTokenizer(tokenizer);
		}
		IStopwords stopwords = (this.stopwords != null) ? this.stopwords : (IStopwords) new NoStopwords();
		Matcher matcher = new Matcher(tokenizer, stopwords);
		matcher.setW(this.w);
		matcher.setRemoveNestedAnnot(this.removeNestedAnnot);
		matcher.addKeyword(this.keywords);
		matcher.setStrategy(this.strategy);
		if (this.setNegativeStopwords)
		{
			NegativeStopwords negativeStopwords = new NegativeStopwords();
			negativeStopwords.add(matcher.getUnigrams());
			matcher.setStopwords(negativeStopwords);
		}
		CacheFuzzyAlgos cache = new CacheFuzzyAlgos();
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

		if (this.minNbCharTroncation != -1)
		{
			PrefixTrie trie = new PrefixTrie(minPrefixLengthClosest);
			trie.addToken(matcher.getUnigrams());
			Troncation troncation = new Troncation("troncation", trie, this.maxDistanceTroncation);
			cache.addFuzzyAlgo(troncation);
		}

		for (StringEncoderSyn stringEncoder : this.stringEncoders)
		{
			cache.addFuzzyAlgo(stringEncoder);
			try
			{
				stringEncoder.add(matcher.getUnigrams());
			} catch (EncoderException e)
			{
				e.printStackTrace();
			}
		}

		for (WordNormalizer normalizer : wordNormalizers)
		{
			cache.addFuzzyAlgo(normalizer);
			normalizer.add(matcher.getUnigrams());
		}

		for (FuzzyRegex fuzzyRegex : fuzzyRegex)
		{
			matcher.addFuzzyAlgo(fuzzyRegex);

		}

		if (matcher.getStopwords() instanceof NegativeStopwords)
		{
			NegativeStopwords negativeStop = (NegativeStopwords) matcher.getStopwords();
			IWord2keepF words2keepFuzzy = new Word2KeepFuzzy(stopwords, matcher.getFuzzyAlgos());
			negativeStop.add(words2keepFuzzy);
		}
		return matcher;
	}

	/**
	 * Add a {@link ClosestSubString} fuzzy algorithm. For example, if the document
	 * contains 'pressureee', the keyword token 'pressure' will be returned by this
	 * algorithm if 'maxDistance' is equal or greater to 2.
	 *
	 * @param minNbChar   Ignore words that are less than this length.
	 * @param maxDistance Maximum number of characters between a document token and
	 *                    a keyword substring token.
	 * @return the builder instance.
	 */
	public MatcherBuilder closestSubString(int minNbChar, int maxDistance)
	{
		this.minPrefixLengthClosest = minNbChar;
		this.maxDistanceClosest = maxDistance;
		return this;
	}

	/**
	 * Add a {@link FuzzyRegex} fuzzy algorithm.
	 *
	 * @param name        a name given to this algorithm.
	 * @param pattern     a regular expression.
	 * @param patternName a name given to this pattern (ex: 'numval').
	 * @return the builder instance.
	 */
	public MatcherBuilder fuzzyRegex(String name, String pattern, String patternName)
	{
		FuzzyRegex fuzzy = new FuzzyRegex(name, pattern, patternName);
		this.fuzzyRegex.add(fuzzy);
		return this;
	}

	/**
	 * Add {@link IKeyword} you want to detect in a document.
	 *
	 * @param keywords a collection of {@link IKeyword}.
	 * @return the builder instance.
	 */
	public MatcherBuilder keywords(IKeyword... keywords)
	{
		for (IKeyword kw : keywords)
		{
			this.keywords.add(kw);
		}
		return this;
	}

	/**
	 * Add {@link IKeyword} you want to detect in a document.
	 *
	 * @param keywords a collection of {@link IKeyword}.
	 * @return the builder instance.
	 */
	public MatcherBuilder keywords(Iterable<IKeyword> keywords)
	{
		for (IKeyword kw : keywords)
		{
			this.keywords.add(kw);
		}
		return this;
	}

	/**
	 * Add {@link IKeyword} labels you want to detect in a document.
	 *
	 * @param keywords a collection of {@link IKeyword}.
	 * @return the builder instance.
	 */
	public MatcherBuilder keywords(String... labels)
	{
		for (String label : labels)
		{
			IKeyword kw = new Keyword(label);
			this.keywords.add(kw);
		}
		return this;
	}

	/**
	 * Add a Levenshtein distance to detect typos in a document.
	 *
	 * @param minNbChar   Ignore all words that have a length less than this number.
	 * @param maxDistance Levenshtein distance.
	 * @param algorithm   See {@link com.github.liblevenshtein.transducer.Algorithm}
	 * @return the builder instance.
	 */
	public MatcherBuilder levenshtein(int minNbChar, int maxDistance, Algorithm algorithm)
	{
		this.minNbcharLeven = minNbChar;
		this.maxDistanceLeven = maxDistance;
		this.algorithmLeven = algorithm;
		return this;
	}

	/**
	 * Every unigram not in the keywords is a stopword. If stopwords are also
	 * passed, they will be removed from keywords' tokens and so still be stopwords.
	 *
	 * @param negativeStopwords True to add negative stopwords. Default to False.
	 * @return the builder instance.
	 */
	public MatcherBuilder negative(boolean negativeStopwords)
	{
		this.setNegativeStopwords = negativeStopwords;
		return this;
	}

	/**
	 * Order tokens alphabetically if order doesn't matter in the matching strategy.
	 *
	 * @param orderTokens True to order tokens.
	 * @return the builder instance.
	 */
	public MatcherBuilder orderTokens(boolean orderTokens)
	{
		this.orderTokens = orderTokens;
		return this;
	}

	/**
	 * If two annotations overlap, remove the shorter one.
	 *
	 * @param removeNestedAnnot Default to True.
	 * @return the builder instance.
	 */
	public MatcherBuilder removeNestedAnnot(boolean removeNestedAnnot)
	{
		this.removeNestedAnnot = removeNestedAnnot;
		return this;
	}

	/**
	 * Stopwords to remove from keywords and to ignore in documents.
	 *
	 * @param stopwords A collection of words to ignore.
	 * @return the builder instance.
	 */
	public MatcherBuilder stopwords(Collection<String> stopwords)
	{
		this.stopwords = new Stopwords(stopwords);
		return this;
	}

	/**
	 * Stopwords to remove from keywords and to ignore in documents.
	 *
	 * @param stopwords A {@link IStopwords} instance to detect stopwords.
	 * @return the builder instance.
	 */
	public MatcherBuilder stopwords(IStopwords stopwords)
	{
		this.stopwords = stopwords;
		return this;
	}

	/**
	 * Stopwords to remove from keywords and to ignore in documents.
	 *
	 * @param words A collection of words to ignore.
	 * @return the builder instance.
	 */
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

	/**
	 * Set the strategy of the matching algorithm.
	 *
	 * @param strategy one of the {@link EMatchingStrategy} enumerated values.
	 * @return the builder instance.
	 */
	public MatcherBuilder strategy(EMatchingStrategy strategy)
	{
		this.strategy = strategy.getInstance();
		return this;
	}

	/**
	 * Set the strategy of the matching algorithm.
	 *
	 * @param strategy a {@link IMatchingStrategy} implementation.
	 * @return the builder instance.
	 */
	public MatcherBuilder strategy(IMatchingStrategy strategy)
	{
		this.strategy = strategy;
		return this;
	}

	/**
	 * Words ignored by string distance algorithms to avoid false positives matched.
	 *
	 * @param words2ignore A collection of words to ignore.
	 * @return the builder instance.
	 */
	public MatcherBuilder stringDistanceWords2ignore(Collection<String> words2ignore)
	{
		this.word2ignore = new SimpleWords2ignore(words2ignore);
		return this;
	}

	/**
	 * Add an Apache string encoder like Soundex.
	 *
	 * @param stringEncoder an apache {@link StringEncoder}. For example
	 *                      {@link org.apache.commons.codec.language.Soundex}.
	 * @param minNbChar     ignore tokens less than this length.
	 * @return the builder instance.
	 * @see https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/class-use/StringEncoder.html#org.apache.commons.codec.language
	 */
	public MatcherBuilder stringEncoder(StringEncoder stringEncoder, int minNbChar)
	{
		StringEncoderSyn encoder = new StringEncoderSyn(stringEncoder, minNbChar);
		this.stringEncoders.add(encoder);
		return this;
	}

	/**
	 * Set the tokenizer.
	 *
	 * @param tokenizer A {@link ITokenizer} instance. Default to French
	 *                  {@link ETokenizer}.
	 * @return the builder instance.
	 */
	public MatcherBuilder tokenizer(ITokenizer tokenizer)
	{
		this.tokenizer = tokenizer;
		return this;
	}

	/**
	 * Add a troncation fuzzy algorithm.
	 *
	 * @param minNbChar   ignore tokens less than this length.
	 * @param maxDistance
	 * @return the builder instance.
	 */
	public MatcherBuilder troncation(int minNbChar, int maxDistance)
	{
		this.minNbCharTroncation = minNbChar;
		this.maxDistanceTroncation = maxDistance;
		return this;
	}

	public MatcherBuilder w(int w)
	{
		this.w = w;
		return this;
	}

	public MatcherBuilder wordNormalizer(String name, INormalizeF normfun)
	{
		WordNormalizer normalizer = new WordNormalizer(name, normfun);
		this.wordNormalizers.add(normalizer);
		return this;
	}
}
