package fr.erias.iamsystem_java.fuzzy.levenshtein;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.liblevenshtein.transducer.Algorithm;
import com.github.liblevenshtein.transducer.Candidate;
import com.github.liblevenshtein.transducer.ITransducer;

import fr.erias.iamsystem_java.fuzzy.base.SimpleWords2ignore;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.stopwords.NoStopwords;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import fr.erias.iamsystem_java.utils.MockData;

class LevenshteinTest
{

	@BeforeEach
	void setUp() throws Exception
	{
	}

	@Test
	void testGetSynsOfWord()

	{
		int maxdistance = 1;
		List<String> unigrams = Arrays.asList("insuffisance");
		ITransducer<Candidate> transducer = Levenshtein.buildTransuder(maxdistance, unigrams, Algorithm.TRANSPOSITION);
		Levenshtein<IToken> leven = new Levenshtein<IToken>("Levenshtein", 5, transducer);
		List<SynAlgo> syns = leven.getSynsOfWord("insuffisanse");
		assertEquals(1, syns.size());
	}

	@Test
	void testMatcher()
	{
		Matcher<IToken> matcher = new Matcher<IToken>(TokenizerFactory.getTokenizer(ETokenizer.FRENCH),
				new NoStopwords());
		matcher.addKeyword(MockData.getICG());
		int maxdistance = 1;
		ITransducer<Candidate> transducer = Levenshtein.buildTransuder(maxdistance, matcher, Algorithm.TRANSPOSITION);
		Levenshtein<IToken> leven = new Levenshtein<IToken>("Levenshtein", 5, transducer);
		matcher.addFuzzyAlgo(leven);
		matcher.setRemoveNestedAnnot(false);
		List<IAnnotation<IToken>> annots = matcher.annot("insuffisanse cardique gache");
		assertEquals(2, annots.size());
	}

	@Test
	void testMinNbOfChar()
	// insuffisance is ignored since its 12 characters < min 13
	{
		int minNbOfChar = 13;
		int maxdistance = 1;
		List<String> unigrams = Arrays.asList("insuffisance");
		ITransducer<Candidate> transducer = Levenshtein.buildTransuder(maxdistance, unigrams, Algorithm.TRANSPOSITION);
		Levenshtein<IToken> leven = new Levenshtein<IToken>("Levenshtein", minNbOfChar, transducer);
		List<SynAlgo> syns = leven.getSynsOfWord("insuffisanse");
		assertEquals(0, syns.size());
	}

	@Test
	void testWords2ignore()
	// no string distance is calculated for 'insuffisance' since it's a word to
	// ignore.
	{
		SimpleWords2ignore words2ignore = new SimpleWords2ignore();
		words2ignore.addWord2ignore("insuffisance");
		int maxdistance = 1;
		List<String> unigrams = Arrays.asList("insuffisance");
		ITransducer<Candidate> transducer = Levenshtein.buildTransuder(maxdistance, unigrams, Algorithm.TRANSPOSITION);
		Levenshtein<IToken> leven = new Levenshtein<IToken>("Levenshtein", 0, words2ignore, transducer);
		List<SynAlgo> syns = leven.getSynsOfWord("insuffisance");
		assertEquals(0, syns.size());
		syns = leven.getSynsOfWord("insuffisanze");
		assertEquals(1, syns.size());
	}
}
