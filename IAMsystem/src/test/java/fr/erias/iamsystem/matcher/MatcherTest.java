package fr.erias.iamsystem.matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem.annotation.IAnnotation;
import fr.erias.iamsystem.keywords.Entity;
import fr.erias.iamsystem.keywords.IEntity;
import fr.erias.iamsystem.matcher.Matcher;
import fr.erias.iamsystem.matcher.MatcherBuilder;
import fr.erias.iamsystem.matcher.strategy.EMatchingStrategy;
import fr.erias.iamsystem.stopwords.Stopwords;
import fr.erias.iamsystem.tokenize.ETokenizer;
import fr.erias.iamsystem.tokenize.ITokenizer;
import fr.erias.iamsystem.tokenize.Token;
import fr.erias.iamsystem.tokenize.TokenizerFactory;
import fr.erias.iamsystem.utils.MockData;

class MatcherTest
{

	private Matcher matcher;
	private ITokenizer tokenizer;
	private Stopwords stopwords;

	private void setStopwords()
	{
		List<String> words = new ArrayList<String>();
		words.add("le");
		words.add("la");
		this.stopwords = new Stopwords(words);
	}

	@BeforeEach
	void setUp() throws Exception
	{
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		this.setStopwords();
		this.matcher = new Matcher(tokenizer, stopwords);
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
	void testAnnot()
	{
		this.matcher.addKeyword(MockData.getICG());
		List<IAnnotation> anns = this.matcher.annot("insuffisance cardiaque");
		assertEquals(anns.size(), 1);
		IAnnotation ann = anns.get(0);
		assertEquals(ann.start_i(), 0);
		assertEquals(ann.end_i(), 1);
		assertEquals(ann.getAlgos().size(), 2);
		assertEquals(ann.stopTokens().size(), 0);
		assertEquals(ann.tokens().size(), 2);
		assertEquals(ann.getKeywords().size(), 1);
		assertEquals(ann.toString(), "insuffisance cardiaque	0 22	Insuffisance Cardiaque (I50.9)");
	}

	@Test
	void testAnnotStopwords()
	{
		this.matcher.addKeyword(MockData.getICG());
		List<IAnnotation> anns = this.matcher.annot("insuffisance de cardiaque");
		assertEquals(anns.size(), 0);
		this.stopwords.add("de");
		anns = this.matcher.annot("insuffisance de cardiaque");
		assertEquals(anns.size(), 1);
		IAnnotation ann = anns.get(0);
		assertEquals(ann.toString(), "insuffisance cardiaque	0 12;16 25	Insuffisance Cardiaque (I50.9)");
	}

	@Test
	void testAnnotWindow()
	{
		this.matcher.addKeyword(MockData.getICG());
		List<IAnnotation> anns = this.matcher.annot("insuffisance de cardiaque");
		assertEquals(anns.size(), 0);
		this.matcher.setW(2);
		anns = this.matcher.annot("insuffisance de cardiaque");
		assertEquals(anns.size(), 1);
	}

	@Test
	void testDuplicateStatesGenerateOverlaps()
	{
		Matcher matcher = new MatcherBuilder().keywords("cancer de la prostate").w(3).build();
		List<IAnnotation> anns = matcher.annot("cancer cancer de de la la prostate prostate");
		assertEquals(anns.size(), 2);
	}

	@Test
	void testEdansO()
	{
		IEntity ent1 = new Entity("fœtal", "C1305737");
		this.matcher.addKeyword(ent1);
		this.matcher.setRemoveNestedAnnot(false);
		List<IAnnotation> anns = this.matcher.annot("foetal");
		assertEquals(anns.size(), 1);
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
	void testLineBreak()
	{
		Matcher matcher = new MatcherBuilder().keywords("cancer du poumon").stopwords("de", "la").w(2).build();
		List<IAnnotation> anns = matcher.annot("cancer du\npoumon");
		assertEquals(anns.size(), 1);
		assertEquals(anns.get(0).toString(), "cancer du\\npoumon	0 16	cancer du poumon");
	}

	@Test
	void testMultipleAnnots()
	{
		this.matcher.addKeyword(MockData.getICG());
		this.matcher.setRemoveNestedAnnot(false);
		List<IAnnotation> anns = this.matcher.annot("insuffisance cardiaque gauche");
		assertEquals(anns.size(), 2);
	}

	@Test
	void testNoOverlapEndToken()
	{
		Matcher matcher = new MatcherBuilder().keywords("portail de la médecine instutionnelle", "médecine")
				.strategy(EMatchingStrategy.NoOverlapStrategy)
				.w(3)
				.build();
		List<IAnnotation> anns = matcher.annot("Portail de la médecine");
		assertEquals(anns.size(), 1);
	}

	@Test
	void testRemoveNestedAnnot()
	{
		Matcher matcher = new MatcherBuilder().tokenizer(TokenizerFactory.getTokenizer(ETokenizer.FRENCH))
				.keywords("nævus", "nævus anemique", "anemique")
				.removeNestedAnnot(true)
				.strategy(EMatchingStrategy.WindowStrategy)
				.w(3)
				.build();
		List<IAnnotation> anns = matcher.annot("nævus anemique");
		assertEquals(anns.size(), 1);
	}
	
	@Test
	void testRepeatedWords()
	{
		Matcher matcher = new MatcherBuilder().tokenizer(TokenizerFactory.getTokenizer(ETokenizer.FRENCH))
				.keywords("cancer")
				.strategy(EMatchingStrategy.WindowStrategy)
				.w(3)
				.build();
		List<IAnnotation> anns = matcher.annot("cancer cancer");
		assertEquals(anns.size(), 2);
	}
	
	@Test
	void testRepeatedWordsLargeWindow()
	{
		Matcher matcher = new MatcherBuilder().tokenizer(TokenizerFactory.getTokenizer(ETokenizer.FRENCH))
				.keywords("cancer")
				.strategy(EMatchingStrategy.LargeWindowStrategy)
				.w(3)
				.build();
		List<IAnnotation> anns = matcher.annot("cancer cancer");
		assertEquals(anns.size(), 2);
	}
	
	@Test
	void testStatesOverriding()
	{
		Matcher matcher = new MatcherBuilder().tokenizer(TokenizerFactory.getTokenizer(ETokenizer.FRENCH))
				.keywords("cancer", "cancer de la prostate")
				.w(10)
				.build();
		List<IAnnotation> anns = matcher.annot("cancer cancer cancer de la prostate");
		assertEquals(anns.size(), 3);
	}
}
