package fr.erias.iamsystem_java.fuzzy.levenshtein;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.liblevenshtein.transducer.Algorithm;
import com.github.liblevenshtein.transducer.Candidate;
import com.github.liblevenshtein.transducer.ITransducer;

import fr.erias.iamsystem_java.annotation.IAnnotation;
import fr.erias.iamsystem_java.fuzzy.base.SimpleWords2ignore;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.stopwords.NoStopwords;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import fr.erias.iamsystem_java.utils.MockData;

class LevenshteinTest
{

	@BeforeEach
	void setUp() throws Exception
	{
	}

	@Test
	void testAscendingOrder()
	// Check it works even if words not in ascending order.

	{
		int maxdistance = 1;
		List<String> unigrams = Arrays.asList("insuffisance", "cardiaque");
		unigrams.sort(Comparator.naturalOrder());
		ITransducer<Candidate> transducer = Levenshtein.buildTransuder(maxdistance, unigrams, Algorithm.TRANSPOSITION);
		Levenshtein leven = new Levenshtein("Levenshtein", 5, transducer);
		List<SynAlgo> syns = leven.getSynsOfWord("insuffisanse");
		assertEquals(1, syns.size());
	}

	@Test
	void testGetSynsOfWord()

	{
		int maxdistance = 1;
		List<String> unigrams = Arrays.asList("insuffisance");
		ITransducer<Candidate> transducer = Levenshtein.buildTransuder(maxdistance, unigrams, Algorithm.TRANSPOSITION);
		Levenshtein leven = new Levenshtein("Levenshtein", 5, transducer);
		List<SynAlgo> syns = leven.getSynsOfWord("insuffisanse");
		assertEquals(1, syns.size());
	}

	@Test
	void testMatcher()
	{
		Matcher matcher = new Matcher(TokenizerFactory.getTokenizer(ETokenizer.FRENCH), new NoStopwords());
		matcher.addKeyword(MockData.getICG());
		int maxdistance = 1;
		ITransducer<Candidate> transducer = Levenshtein.buildTransuder(maxdistance, matcher, Algorithm.TRANSPOSITION);
		Levenshtein leven = new Levenshtein("Levenshtein", 5, transducer);
		matcher.addFuzzyAlgo(leven);
		matcher.setRemoveNestedAnnot(false);
		List<IAnnotation> annots = matcher.annot("insuffisanse cardique gache");
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
		Levenshtein leven = new Levenshtein("Levenshtein", minNbOfChar, transducer);
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
		Levenshtein leven = new Levenshtein("Levenshtein", 0, transducer);
		leven.setWords2ignore(words2ignore);
		List<SynAlgo> syns = leven.getSynsOfWord("insuffisance");
		assertEquals(0, syns.size());
		syns = leven.getSynsOfWord("insuffisanze");
		assertEquals(1, syns.size());
	}
}
