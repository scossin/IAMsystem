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
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.ITokenizerStopwords;
import fr.erias.iamsystem_java.tree.Trie;

public class Matcher implements IMatcher, IStoreKeywords, ITokenizerStopwords, ISynsProvider
{

	private ITokenizer tokenizer;
	private IStopwords stopwords;
	private final Detector detector;
	private int w = 1;
	private final Trie trie = new Trie();
	private Terminology termino = new Terminology();
	private boolean removeNestedAnnot = true;
	private List<FuzzyAlgo> fuzzyAlgos = new ArrayList<FuzzyAlgo>();
	private SynsProvider synsProvider;

	public Matcher(ITokenizer tokenizer, IStopwords stopwords)
	{
		this.setTokenizer(tokenizer);
		this.setStopwords(stopwords);
		this.detector = new Detector();
		fuzzyAlgos.add(new ExactMatch());
		this.synsProvider = new SynsProvider(fuzzyAlgos);
	}

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

	public void addKeyword(Iterable<? extends IKeyword> keywords)
	{
		for (IKeyword kw : keywords)
		{
			this.addKeyword(kw);
		}
	}

	/**
	 * Add keywords by providing a String iterable.
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

	public List<IAnnotation> annot(List<IToken> tokens)
	{
		List<IAnnotation> annots = detector.detect(tokens, w, trie.getInitialState(), this, stopwords);
		if (this.removeNestedAnnot)
		{
			annots = this.detector.rmNestedAnnots(annots, false);
		}
		return annots;
	}

	public List<IAnnotation> annot(String text)
	{
		List<IToken> tokens = tokenize(text);
		List<IAnnotation> annots = this.annot(tokens);
		return annots;
	}

	@Override
	public Collection<IKeyword> getKeywords()
	{
		return termino.getKeywords();
	}

	public IStopwords getStopwords()
	{
		return stopwords;
	}

	@Override
	public Collection<SynAlgos> getSynonyms(List<IToken> tokens, IToken token, Set<LinkedState> states)
	{
		return synsProvider.getSynonyms(tokens, token, states);
	}

	public ITokenizer getTokenizer()
	{
		return tokenizer;
	}

	public Set<String> getUnigrams()
	{
		return IStoreKeywords.getUnigrams(this.getKeywords(), this);
	}

	public int getW()
	{
		return w;
	}

	@Override
	public boolean isTokenAStopword(IToken token)
	{
		return stopwords.isTokenAStopword(token);
	}

	public boolean removeNestedAnnot()
	{
		return removeNestedAnnot;
	}

	public void setRemoveNestedAnnot(boolean removeNestedAnnot)
	{
		this.removeNestedAnnot = removeNestedAnnot;
	}

	public void setStopwords(IStopwords stopwords)
	{
		this.stopwords = stopwords;
	}

	public void setTokenizer(ITokenizer tokenizer)
	{
		this.tokenizer = tokenizer;
	}

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
