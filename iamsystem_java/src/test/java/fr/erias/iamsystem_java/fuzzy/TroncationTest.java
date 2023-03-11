package fr.erias.iamsystem_java.fuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.annotation.IAnnotation;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.fuzzy.troncation.PrefixTrie;
import fr.erias.iamsystem_java.fuzzy.troncation.Troncation;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.stopwords.NoStopwords;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import fr.erias.iamsystem_java.utils.MockData;

class TroncationTest
{

	Troncation getTroncation(int minPrefixLength, int maxDistance)
	{
		PrefixTrie trie = new PrefixTrie(minPrefixLength);
		Matcher matcher = new Matcher(TokenizerFactory.getTokenizer(ETokenizer.FRENCH), new NoStopwords());
		matcher.addKeyword(MockData.getICG());
		trie.addToken(matcher.getUnigrams());
		Troncation troncation = new Troncation("troncation", trie, maxDistance);
		return troncation;
	}

	@BeforeEach
	void setUp() throws Exception
	{
	}

	@Test
	void test()
	{
		Troncation troncation = getTroncation(5, 9);
		List<SynAlgo> syns = troncation.getSynsOfWord("insuffis");
		assertEquals(1, syns.size());
	}

	@Test
	void testMatcher()
	{
		PrefixTrie trie = new PrefixTrie(4);
		Matcher matcher = new Matcher(TokenizerFactory.getTokenizer(ETokenizer.FRENCH), new NoStopwords());
		matcher.addKeyword(MockData.getICG());
		trie.addToken(matcher.getUnigrams());
		Troncation troncation = new Troncation("troncation", trie, 2);
		matcher.addFuzzyAlgo(troncation);
		matcher.setRemoveNestedAnnot(false);
		List<IAnnotation> annots = matcher.annot("insuffisan cardiaq gauc");
		assertEquals(2, annots.size());
	}

	@Test
	void testMaxDistance()
	{
		// insuffisance has 12 characters. More than 12, this word is ignored.
		Troncation troncation = getTroncation(5, 2);
		List<SynAlgo> syns = troncation.getSynsOfWord("insuffisan");
		assertEquals(syns.size(), 1);
		troncation = getTroncation(5, 1);
		syns = troncation.getSynsOfWord("insuffisan");
		assertEquals(0, syns.size());
	}

	@Test
	void testMinPrefixLength()
	{
		// insuffisance has 12 characters. More than 12, this word is ignored.
		Troncation troncation = getTroncation(12, 9);
		List<SynAlgo> syns = troncation.getSynsOfWord("insuffisance");
		assertEquals(1, syns.size());
		troncation = getTroncation(13, 9);
		syns = troncation.getSynsOfWord("insuffisance");
		assertEquals(0, syns.size());
	}
}
