package fr.erias.iamsystem_java.fuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.tokenize.IToken;

class CacheFuzzyAlgosTest
{

	private FakeFuzzyAlgo fakeFuzzy;
	private CacheFuzzyAlgos<IToken> cache;

	@BeforeEach
	void setUp() throws Exception
	{
		this.fakeFuzzy = new FakeFuzzyAlgo();
		this.cache = new CacheFuzzyAlgos<IToken>();
		this.cache.addFuzzyAlgo(fakeFuzzy);
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
