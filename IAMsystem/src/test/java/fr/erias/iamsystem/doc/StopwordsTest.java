package fr.erias.iamsystem.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem.annotation.IAnnotation;
import fr.erias.iamsystem.keywords.Entity;
import fr.erias.iamsystem.matcher.Matcher;
import fr.erias.iamsystem.matcher.MatcherBuilder;
import fr.erias.iamsystem.tokenize.ETokenizer;
import fr.erias.iamsystem.tokenize.TokenizerFactory;

class StopwordsTest
{

	@BeforeEach
	void setUp() throws Exception
	{
	}

	@Test
	void testAddStopword()
	{
		Entity ent = new Entity("Essential hypertension, unspecified", "I10.9");
		Matcher matcher = new MatcherBuilder().keywords(ent)
				.tokenizer(TokenizerFactory.getTokenizer(ETokenizer.ENGLISH))
				.stopwords("unspecified")
				.build();
		String text = "Medical history: essential hypertension";
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(annots.size(), 1);
		assertEquals("essential hypertension	17 39	Essential hypertension, unspecified (I10.9)",
				annots.get(0).toString());
	}

	@Test
	void testNegativeStopword()
	{
		Matcher matcher = new MatcherBuilder().keywords("calcium blood").negative(true).build();
		String text = "the level of calcium can be measured in the blood.";
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(annots.size(), 1);
	}
}
