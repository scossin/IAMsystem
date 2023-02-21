package fr.erias.iamsystem_java.fuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Caverphone1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.stopwords.NoStopwords;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import fr.erias.iamsystem_java.utils.MockData;

class CacheFuzzyAlgosTest
{

	private FakeFuzzyAlgo fakeFuzzy;
	private CacheFuzzyAlgos<IToken> cache;
	private ITokenizer<IToken> tokenizer;
	private Matcher<IToken> matcher;

	@BeforeEach
	void setUp() throws Exception
	{
		this.fakeFuzzy = new FakeFuzzyAlgo();
		this.cache = new CacheFuzzyAlgos<IToken>();
		this.cache.addFuzzyAlgo(fakeFuzzy);
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		this.matcher = new Matcher<IToken>(this.tokenizer, new NoStopwords());
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
		stringEncoder.addTerminology(this.matcher.getKeywords(), this.matcher.getTokStop());
		this.cache.addFuzzyAlgo(stringEncoder);
		this.matcher.setRemoveNestedAnnot(false);
		List<IAnnotation<IToken>> anns = this.matcher.annot("insuffizzzance cardiaqu gauch");
		assertEquals(anns.size(), 2);
	}

}

class FakeFuzzyAlgo extends NormLabelAlgo<IToken>
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
