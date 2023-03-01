package fr.erias.iamsystem_java.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.keywords.Entity;
import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.matcher.MatcherBuilder;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;

class AnnotationTest
{

	@BeforeEach
	void setUp() throws Exception
	{
	}

	@Test
	void testAnnotationFormat()
	{
		Entity ent = new Entity("infectious disease", "D007239");
		Matcher matcher = new MatcherBuilder().keywords(ent).abbreviations("infect", "infectious").w(2).build();
		String text = "Infect mononucleosis disease";
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(annots.size(), 1);
		IAnnotation annot = annots.get(0);
		assertEquals(annot.toString(), "Infect disease	0 6;21 28	infectious disease (D007239)");
	}

	@Test
	void testAnnotationOverlappingAncestors()
	{
		Matcher matcher = new MatcherBuilder().keywords("lung", "lung cancer").w(1).build();
		String text = "Presence of a lung cancer";
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(annots.size(), 1);
		matcher.setRemoveNestedAnnot(false);
		annots = matcher.annot(text);
		assertEquals(annots.size(), 2);
	}

	@Test
	void testAnnotationOverlappingNotAncestors()
	{
		Matcher matcher = new MatcherBuilder().keywords("North America", "South America").w(3).build();
		String text = "North and South America";
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(annots.size(), 2);
	}

	@Test
	void testAnnotationPartialOverlap()
	{
		Matcher matcher = new MatcherBuilder().keywords("lung cancer", "cancer prognosis").build();
		String text = "lung cancer prognosis";
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(annots.size(), 2);
	}

	@Test
	void testMultipleKeywords()
	{
		Entity ent1 = new Entity("Infectious Disease", "C0042029");
		Entity ent2 = new Entity("infectious disease", "C0042029");
		Entity ent3 = new Entity("infectious disease, unspecified", "C0042029");
		Matcher matcher = new MatcherBuilder().keywords(ent1, ent2, ent3)
				.tokenizer(TokenizerFactory.getTokenizer(ETokenizer.ENGLISH))
				.stopwords("unspecified")
				.w(2)
				.build();
		String text = "History of infectious disease";
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(annots.size(), 1);
		assertEquals(annots.get(0).getKeywords().size(), 3);
	}
}
