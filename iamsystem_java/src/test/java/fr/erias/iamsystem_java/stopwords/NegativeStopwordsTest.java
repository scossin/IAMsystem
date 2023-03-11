package fr.erias.iamsystem_java.stopwords;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.liblevenshtein.transducer.Algorithm;

import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.matcher.MatcherBuilder;
import fr.erias.iamsystem_java.tokenize.Token;

class NegativeStopwordsTest
{

	@Test
	void testFuzzyWithNegativeStopwords()
	{
		Matcher matcher = new MatcherBuilder().keywords("cancer du poumon")
				.abbreviations("k", "cancer")
				.levenshtein(4, 1, Algorithm.TRANSPOSITION)
				.stopwords("du")
				.negative(true)
				.build();
		List<IAnnotation> anns = matcher.annot("k poumons");
		assertEquals(anns.size(), 1);
	}

	@Test
	void testStopwordsFuzzyWithNegativeStopwords()
	{
		Matcher matcher = new MatcherBuilder().keywords("cancer du poumon")
				.abbreviations("k", "cancer")
				.levenshtein(4, 1, Algorithm.TRANSPOSITION)
				.stopwords("du", "poumonn")
				.negative(true)
				.build();
		List<IAnnotation> anns = matcher.annot("k poumonn");
		assertEquals(anns.size(), 0);
	}
	

	@Test
	void testNegativeStopwords()
	{
		Token token = new Token(0, 1, "important", "important", 0);
		List<String> words = new ArrayList<String>();
		NegativeStopwords stopwords = new NegativeStopwords(words);
		assertTrue(stopwords.isTokenAStopword(token));
		words.add("important");
		stopwords.add(words);
		assertTrue(!stopwords.isTokenAStopword(token));
	}
	

	@Test
	void testNegativeStopwordsFun2keep()
	{
		Token token = new Token(0, 1, "important", "important", 0);
		NegativeStopwords stopwords = new NegativeStopwords();
		assertTrue(stopwords.isTokenAStopword(token));
		stopwords.add((tok) -> tok.normLabel().equals("important") ? true : false);
		assertTrue(!stopwords.isTokenAStopword(token));
	}
}
