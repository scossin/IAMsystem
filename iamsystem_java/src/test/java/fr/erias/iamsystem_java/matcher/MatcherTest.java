package fr.erias.iamsystem_java.matcher;

import static org.junit.jupiter.api.Assertions.*;

import fr.erias.iamsystem_java.stopwords.Stopwords;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.Token;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import fr.erias.iamsystem_java.utils.MockData;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MatcherTest
{

	private Matcher<IToken> matcher;
	private ITokenizer<IToken> tokenizer;
	private Stopwords<IToken> stopwords;

	@BeforeEach
	void setUp() throws Exception
	{
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		this.setStopwords();
		this.matcher = new Matcher<IToken>(tokenizer, stopwords);
	}

	private void setStopwords()
	{
		List<String> words = new ArrayList<String>();
		words.add("le");
		words.add("la");
		this.stopwords = new Stopwords<IToken>(words);
	}

	@Test
	void testAddKeywords()
	{
		this.matcher.addKeyword(MockData.getICG());
		assertEquals(this.matcher.getKeywords().size(), 2);
	}

	@Test
	void testAddKeywordsArray()
	{
		this.matcher.addKeyword(new String[] { "Insuffisance Cardiaque", "Insuffisance Cardiaque Gauche" });
		assertEquals(this.matcher.getKeywords().size(), 2);
	}

	@Test
	void testGetUnigrams()
	{
		this.matcher.addKeyword(MockData.getICG());
		Set<String> unigrams = this.matcher.getUnigrams();
		assertEquals(3, unigrams.size());
		assertTrue(unigrams.contains("insuffisance"));
		assertTrue(unigrams.contains("cardiaque"));
		assertTrue(unigrams.contains("gauche"));
	}

	@Test
	void testIsStopword()
	{
		this.matcher.addKeyword(MockData.getICG());
		Token token = new Token(0, 1, "important", "important", 0);
		assertFalse(this.matcher.isTokenAStopword(token));
		this.stopwords.add("important");
		assertTrue(this.matcher.isTokenAStopword(token));
	}

	@Test
	void testAnnot()
	{
		this.matcher.addKeyword(MockData.getICG());
		List<IAnnotation<IToken>> anns = this.matcher.annot("insuffisance cardiaque");
		assertEquals(anns.size(), 1);
		IAnnotation<IToken> ann = anns.get(0);
		assertEquals(ann.start_i(), 0);
		assertEquals(ann.end_i(), 1);
		assertEquals(ann.getAlgos().size(), 2);
		assertEquals(ann.stopTokens().size(), 0);
		assertEquals(ann.tokens().size(), 2);
		assertEquals(ann.getKeywords().size(), 1);
		assertEquals(ann.toString(), "insuffisance cardiaque	0 22	Insuffisance Cardiaque (I50.9)");
	}

	@Test
	void testAnnotWindow()
	{
		this.matcher.addKeyword(MockData.getICG());
		List<IAnnotation<IToken>> anns = this.matcher.annot("insuffisance de cardiaque");
		assertEquals(anns.size(), 0);
		this.matcher.setW(2);
		anns = this.matcher.annot("insuffisance de cardiaque");
		assertEquals(anns.size(), 1);
	}

	@Test
	void testAnnotStopwords()
	{
		this.matcher.addKeyword(MockData.getICG());
		List<IAnnotation<IToken>> anns = this.matcher.annot("insuffisance de cardiaque");
		assertEquals(anns.size(), 0);
		this.stopwords.add("de");
		anns = this.matcher.annot("insuffisance de cardiaque");
		assertEquals(anns.size(), 1);
		IAnnotation<IToken> ann = anns.get(0);
		assertEquals(ann.toString(), "insuffisance cardiaque	0 12;16 25	Insuffisance Cardiaque (I50.9)");
	}

	@Test
	void testMultipleAnnots()
	{
		this.matcher.addKeyword(MockData.getICG());
		List<IAnnotation<IToken>> anns = this.matcher.annot("insuffisance cardiaque gauche");
		assertEquals(anns.size(), 2);
	}
}
