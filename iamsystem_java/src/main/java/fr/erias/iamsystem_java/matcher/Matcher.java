package fr.erias.iamsystem_java.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import fr.erias.iamsystem_java.fuzzy.ExactMatch;
import fr.erias.iamsystem_java.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.base.ISynsProvider;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgos;
import fr.erias.iamsystem_java.fuzzy.base.SynsProvider;
import fr.erias.iamsystem_java.keywords.IKeyword;
import fr.erias.iamsystem_java.keywords.IStoreKeywords;
import fr.erias.iamsystem_java.keywords.Keyword;
import fr.erias.iamsystem_java.keywords.Terminology;
import fr.erias.iamsystem_java.matcher.strategy.IMatchingStrategy;
import fr.erias.iamsystem_java.matcher.strategy.StrategyUtils;
import fr.erias.iamsystem_java.matcher.strategy.WindowMatching;
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.ITokenizerStopwords;
import fr.erias.iamsystem_java.tree.Trie;

public class Matcher implements IBaseMatcher, IStoreKeywords, ITokenizerStopwords, ISynsProvider
{

	private ITokenizer tokenizer;
	private IStopwords stopwords;
	private IMatchingStrategy strategy;
	private int w = 1;

	private final Trie trie = new Trie();
	private Terminology termino = new Terminology();
	private boolean removeNestedAnnot = true;
	private List<FuzzyAlgo> fuzzyAlgos = new ArrayList<FuzzyAlgo>();
	private SynsProvider synsProvider;

	/**
	 * Create a Matcher. I recommend you rather use {@link MatcherBuilder} class to
	 * build a matcher since the API is easier to use.
	 *
	 * @param tokenizer the {@link ITokenizer} used to tokenize and normalize
	 *                  documents and keywords.
	 * @param stopwords the {@link IStopwords} instance used to remove stopwords
	 *                  from keywords and ignore them in documents.
	 */
	public Matcher(ITokenizer tokenizer, IStopwords stopwords)
	{
		this.setTokenizer(tokenizer);
		this.setStopwords(stopwords);
		this.strategy = new WindowMatching(); // LargeWindowStrategy();
		fuzzyAlgos.add(new ExactMatch());
		this.synsProvider = new SynsProvider(fuzzyAlgos);
	}

	/**
	 * Add a fuzzy algorithm to allow fuzzy matching.
	 *
	 * @param fuzzyAlgo a class that extends {@link FuzzyAlgo}.
	 */
	public void addFuzzyAlgo(FuzzyAlgo fuzzyAlgo)
	{
		this.fuzzyAlgos.add(fuzzyAlgo);
	}

	@Override
	public void addKeyword(IKeyword keyword)
	{
		termino.addKeyword(keyword);
		trie.addIKeyword(keyword, this);
	}

	/**
	 * Add multiple keywords.
	 *
	 * @param keywords a class that implements {@link IKeyword} interface.
	 */
	public void addKeyword(Iterable<? extends IKeyword> keywords)
	{
		for (IKeyword kw : keywords)
		{
			this.addKeyword(kw);
		}
	}

	/**
	 * Add keywords by providing an iterable of labels.
	 *
	 * @param keywords A collection of keywords to add.
	 */
	public void addKeyword(String... labels)
	{
		for (String kw : labels)
		{
			Keyword k = new Keyword(kw);
			this.addKeyword(k);
		}
	}

	@Override
	public List<IAnnotation> annot(List<IToken> tokens)
	{
		List<IAnnotation> annots = strategy.detect(tokens, w, trie.getInitialState(), this, stopwords);
		if (this.removeNestedAnnot)
		{
			annots = StrategyUtils.rmNestedAnnots(annots, false);
		}
		return annots;
	}

	@Override
	public List<IAnnotation> annot(String text)
	{
		List<IToken> tokens = tokenize(text);
		List<IAnnotation> annots = this.annot(tokens);
		annots.forEach(annot -> annot.setText(text));
		return annots;
	}

	/**
	 * Get the list of fuzzy algorithms.
	 * 
	 * @return {@link FuzzyAlgo} algorithms.
	 */
	public List<FuzzyAlgo> getFuzzyAlgos()
	{
		return (this.fuzzyAlgos);
	}

	@Override
	public Collection<IKeyword> getKeywords()
	{
		return termino.getKeywords();
	}

	/**
	 * Retrieve the {@link IStopwords} instance.
	 *
	 * @return the {@link IStopwords} instance.
	 */
	public IStopwords getStopwords()
	{
		return stopwords;
	}

	@Override
	public Collection<SynAlgos> getSynonyms(List<IToken> tokens, IToken token, Iterable<StateTransition> transitions)
	{
		return synsProvider.getSynonyms(tokens, token, transitions);
	}

	/**
	 * Retrieve the {@link ITokenizer} instance.
	 *
	 * @return the {@link ITokenizer} instance.
	 */
	public ITokenizer getTokenizer()
	{
		return tokenizer;
	}

	/**
	 * Get all the unigrams (single words excluding stopwords) in keywords. This
	 * function is often called by fuzzy algorithms to know which unigram are
	 * presents in the keywords.
	 *
	 * @return a set of unigrams.
	 */
	public Set<String> getUnigrams()
	{
		return IStoreKeywords.getUnigrams(this.getKeywords(), this);
	}

	/**
	 * Return the window parameter of this matcher.
	 *
	 * @return an integer, default to 1.
	 */
	public int getW()
	{
		return w;
	}

	@Override
	public boolean isTokenAStopword(IToken token)
	{
		return stopwords.isTokenAStopword(token);
	}

	/**
	 * Whether to remove nested annotations.
	 *
	 * @return Default to True.
	 */
	public boolean removeNestedAnnot()
	{
		return removeNestedAnnot;
	}

	/**
	 * Set removeNestedAnnot attribute.
	 *
	 * @param removeNestedAnnot False to not removed nested annotations.
	 */
	public void setRemoveNestedAnnot(boolean removeNestedAnnot)
	{
		this.removeNestedAnnot = removeNestedAnnot;
	}

	/**
	 * Change the stopwords instance. Note that keywords already added will not be
	 * modified.
	 *
	 * @param stopwords another {@link IStopwords} instance.
	 */
	public void setStopwords(IStopwords stopwords)
	{
		this.stopwords = stopwords;
	}

	public void setStrategy(IMatchingStrategy strategy)
	{
		this.strategy = strategy;
	}

	/**
	 * Change the tokenizer. Note that keywords already added will not be modified.
	 *
	 * @param tokenizer another {@link ITokenizer} instance.
	 */
	public void setTokenizer(ITokenizer tokenizer)
	{
		this.tokenizer = tokenizer;
	}

	/**
	 * Set the window parameter of this matcher.
	 *
	 * @param w Default to 1.
	 */
	public void setW(int w)
	{
		this.w = w;
	}

	@Override
	public List<IToken> tokenize(String text)
	{
		return tokenizer.tokenize(text);
	}
}
