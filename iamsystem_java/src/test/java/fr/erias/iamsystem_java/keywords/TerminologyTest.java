package fr.erias.iamsystem_java.keywords;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.stopwords.NoStopwords;
import fr.erias.iamsystem_java.stopwords.Stopwords;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;

class TerminologyTest
{

	private Entity icg;
	private Keyword kw;

	@BeforeEach
	void setUp() throws Exception
	{
		this.icg = new Entity("Insuffisance Cardiaque Gauche", "I50.1");
		this.kw = new Keyword("insuffisance");
	}

	@Test
	void testCasting()
	{
		Terminology termino = new Terminology();
		termino.addKeyword(icg);
		List<IEntity> entities = termino.getKeywords().stream().filter(kw -> kw instanceof IEntity)
				.map(kw -> (IEntity) kw).collect(Collectors.toList());
		assertEquals(1, entities.size());
	}

	@Test
	void testGetUnigrams()
	{
		IStopwords stopwords = new NoStopwords();
		ITokenizer tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		Terminology termino = new Terminology();
		termino.addKeyword(icg);
		termino.addKeyword(icg);
		Set<String> unigrams = IStoreKeywords.getUnigrams(termino, tokenizer, stopwords);
		assertEquals(3, unigrams.size());
	}

	@Test
	void testGetUnigramsStopwords()
	{
		List<String> words = new ArrayList<String>();
		words.add("insuffisance");
		IStopwords stopwords = new Stopwords(words);
		ITokenizer tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		Terminology termino = new Terminology();
		termino.addKeyword(icg);
		termino.addKeyword(icg);
		Set<String> unigrams = IStoreKeywords.getUnigrams(termino, tokenizer, stopwords);
		assertEquals(2, unigrams.size());
	}

	@Test
	void testMultipleTypes()
	{
		Terminology termino = new Terminology();
		termino.addKeyword(icg);
		termino.addKeyword(kw);
		assertEquals(2, termino.size());
	}

	@Test
	void testTerminoDuplicated()
	{
		// adding 2 times the same entity returns 2 entities.
		Terminology termino = new Terminology();
		termino.addKeyword(icg);
		termino.addKeyword(icg);
		assertEquals(2, termino.size());
	}

	@Test
	void testTerminoInit()
	{
		Terminology termino = new Terminology();
		assertEquals(0, termino.size());
		termino.addKeyword(icg);
		assertEquals(1, termino.size());
	}

	@Test
	void testTerminoIterator()
	{
		Terminology termino = new Terminology();
		termino.addKeyword(icg);
		for (IKeyword kw : termino)
		{
			assertEquals(kw.label(), "Insuffisance Cardiaque Gauche");
		}
	}
}
