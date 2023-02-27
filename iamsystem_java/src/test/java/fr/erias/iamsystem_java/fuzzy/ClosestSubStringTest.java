package fr.erias.iamsystem_java.fuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.fuzzy.closestSubString.ClosestSubString;
import fr.erias.iamsystem_java.fuzzy.troncation.PrefixTrie;
import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.stopwords.NoStopwords;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import fr.erias.iamsystem_java.utils.MockData;

class ClosestSubStringTest
{

	ClosestSubString getClosestSubstring(int minPrefixLength, int maxDistance)
	{
		PrefixTrie trie = new PrefixTrie(minPrefixLength);
		Matcher matcher = new Matcher(TokenizerFactory.getTokenizer(ETokenizer.FRENCH), new NoStopwords());
		matcher.addKeyword(MockData.getICG());
		trie.addToken(matcher.getUnigrams());
		ClosestSubString algo = new ClosestSubString("closest", trie, maxDistance);
		return algo;
	}

	@BeforeEach
	void setUp() throws Exception
	{
	}

	@Test
	void test()
	{
		ClosestSubString closestSub = getClosestSubstring(5, 9);
		List<SynAlgo> syns = closestSub.getSynsOfWord("insuffisanceee");
		assertEquals(1, syns.size());
	}

	@Test
	void testMatcher()
	{
		PrefixTrie trie = new PrefixTrie(4);
		Matcher matcher = new Matcher(TokenizerFactory.getTokenizer(ETokenizer.FRENCH), new NoStopwords());
		matcher.addKeyword(MockData.getICG());
		trie.addToken(matcher.getUnigrams());
		ClosestSubString closestSub = new ClosestSubString("closest", trie, 2);
		matcher.addFuzzyAlgo(closestSub);
		matcher.setRemoveNestedAnnot(false);
		List<IAnnotation> annots = matcher.annot("insuffisancee cardiaquee gaucheee");
		assertEquals(2, annots.size());
	}

	@Test
	void testMaxDistance()
	{
		ClosestSubString closestSub = getClosestSubstring(5, 2);
		List<SynAlgo> syns = closestSub.getSynsOfWord("insuffisanceee");
		assertEquals(syns.size(), 1);
		closestSub = getClosestSubstring(5, 1);
		syns = closestSub.getSynsOfWord("insuffisanceee");
		assertEquals(0, syns.size());
	}
}
