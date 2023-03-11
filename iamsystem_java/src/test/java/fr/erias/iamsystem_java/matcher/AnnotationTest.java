package fr.erias.iamsystem_java.matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.annotation.IAnnotation;
import fr.erias.iamsystem_java.stopwords.NoStopwords;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.IOffsets;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import fr.erias.iamsystem_java.utils.MockData;

class AnnotationTest
{

	private ITokenizer tokenizer;
	private Matcher matcher;

	@BeforeEach
	void setUp() throws Exception
	{
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		this.matcher = new Matcher(tokenizer, new NoStopwords());
		this.matcher.addKeyword(MockData.getICG());
		this.matcher.addKeyword(MockData.getNorthSouthAmer());
		this.matcher.setRemoveNestedAnnot(false);
	}

	@Test
	void testAnnotation2String()
	{
		this.matcher.setW(3);
		List<IAnnotation> annots = this.matcher.annot("North and South America");
		assertEquals(2, annots.size());
		assertEquals("North America	0 5;16 23	north AMERICA", annots.get(0).toString());
		assertEquals("South America	10 23	south AMERICA", annots.get(1).toString());
	}

	@Test
	void testIsNotShortAnnotOf()
	{
		this.matcher.setW(3);
		List<IAnnotation> annots = this.matcher.annot("North and South America");
		assertEquals(2, annots.size());
		assertFalse(IAnnotation.isAncestorAnnotOf(annots.get(0), annots.get(1)));
		assertTrue(IOffsets.offsetsOverlap(annots.get(0), annots.get(1)));
	}

	@Test
	void testIsShortAnnotOf()
	{
		List<IAnnotation> annots = this.matcher.annot("insuffisance cardiaque gauche");
		assertEquals(2, annots.size());
		IAnnotation.isAncestorAnnotOf(annots.get(0), annots.get(1));
	}

	@Test
	void testOffsetsOverlaps()
	{
		List<IAnnotation> annots = this.matcher.annot("insuffisance cardiaque gauche");
		assertEquals(2, annots.size());
		assertTrue(IOffsets.offsetsOverlap(annots.get(0), annots.get(1)));
	}
}
