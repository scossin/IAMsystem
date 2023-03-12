package fr.erias.iamsystem.stopwords;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import fr.erias.iamsystem.stopwords.ISimpleStopwords;
import fr.erias.iamsystem.stopwords.Stopwords;

class StopwordsTest
{

	@Test
	void testSimpleStopwords()
	{
		List<String> words = new ArrayList<String>();
		words.add("le");
		words.add("la");
		ISimpleStopwords stopwords = new Stopwords(words);
		assertTrue(stopwords.isStopword("le"));
		assertTrue(!stopwords.isStopword("insuffisance"));
	}

	@Test
	void testSimpleStopwordsAccents()
	{
		List<String> words = new ArrayList<String>();
		words.add("à");
		ISimpleStopwords stopwords = new Stopwords(words);
		assertTrue(stopwords.isStopword("à"));
		assertTrue(!stopwords.isStopword("a"));
	}

	@Test
	void testSimpleStopwordsEmptyString()
	{
		ISimpleStopwords stopwords = new Stopwords();
		assertTrue(stopwords.isStopword(" "));
		assertTrue(stopwords.isStopword("\t"));
		assertTrue(stopwords.isStopword("\n"));
	}

	@Test
	void testSimpleStopwordsUppercase()
	{
		List<String> words = new ArrayList<String>();
		words.add("Insuffisance");
		ISimpleStopwords stopwords = new Stopwords(words);
		assertTrue(stopwords.isStopword("insuffisance"));
	}
}
