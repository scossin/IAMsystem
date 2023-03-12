package fr.erias.iamsystem.fuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Caverphone1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem.annotation.IAnnotation;
import fr.erias.iamsystem.fuzzy.CacheFuzzyAlgos;
import fr.erias.iamsystem.fuzzy.base.NormLabelAlgo;
import fr.erias.iamsystem.fuzzy.base.SynAlgo;
import fr.erias.iamsystem.fuzzy.encoder.StringEncoderSyn;
import fr.erias.iamsystem.keywords.IStoreKeywords;
import fr.erias.iamsystem.matcher.Matcher;
import fr.erias.iamsystem.stopwords.NoStopwords;
import fr.erias.iamsystem.tokenize.ETokenizer;
import fr.erias.iamsystem.tokenize.ITokenizer;
import fr.erias.iamsystem.tokenize.TokenizerFactory;
import fr.erias.iamsystem.utils.MockData;

class CacheFuzzyAlgosTest
{

	private FakeFuzzyAlgo fakeFuzzy;
	private CacheFuzzyAlgos cache;
	private ITokenizer tokenizer;
	private Matcher matcher;

	@BeforeEach
	void setUp() throws Exception
	{
		this.fakeFuzzy = new FakeFuzzyAlgo();
		this.cache = new CacheFuzzyAlgos();
		this.cache.addFuzzyAlgo(fakeFuzzy);
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		this.matcher = new Matcher(this.tokenizer, new NoStopwords());
		this.matcher.addFuzzyAlgo(cache);
		this.matcher.addKeyword(MockData.getICG());
	}

	@Test
	void testCache()
	{
		assertEquals(0, this.fakeFuzzy.count);
		this.cache.getSynsOfWord("word");
		assertEquals(1, this.fakeFuzzy.count);
		this.cache.getSynsOfWord("word");
		assertEquals(1, this.fakeFuzzy.count);
	}

	@Test
	void testMatcher() throws EncoderException
	{
		StringEncoderSyn stringEncoder = new StringEncoderSyn(new Caverphone1(), 5);
		Set<String> unigrams = IStoreKeywords.getUnigrams(this.matcher.getKeywords(), this.matcher);
		stringEncoder.add(unigrams);
		this.cache.addFuzzyAlgo(stringEncoder);
		this.matcher.setRemoveNestedAnnot(false);
		List<IAnnotation> anns = this.matcher.annot("insuffizzzance cardiaqu gauch");
		assertEquals(anns.size(), 2);
	}

}

class FakeFuzzyAlgo extends NormLabelAlgo
{

	public int count;

	public FakeFuzzyAlgo()
	{
		super("Fake");
		this.count = 0;
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<SynAlgo> getSynsOfWord(String word)
	{
		this.count++;
		return this.word2syn(word);
	}

}
