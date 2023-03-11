package fr.erias.iamsystem_java.matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.matcher.strategy.EMatchingStrategy;

class NoOverlapMatchingTest
{

	private Matcher matcher;

	@BeforeEach
	void setUp() throws Exception
	{
		this.matcher = new MatcherBuilder().keywords("cancer", "cancer de la prostate", "prostate", "de la")
				.strategy(EMatchingStrategy.NoOverlapStrategy)
				.build();

	}

	@Test
	void testNoOverlap()
	{
		String text = "cancer de la prostate";
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(1, annots.size());
		IAnnotation annot = annots.get(0);
		assertEquals(annot.toString(), "cancer de la prostate	0 21	cancer de la prostate");
	}

	@Test
	void testNoOverlapBackTrack()
	{
		String text = "cancer de la something else prostate";
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(3, annots.size());
		assertEquals("cancer	0 6	cancer", annots.get(0).toString());
		assertEquals("de la	7 12	de la", annots.get(1).toString());
		assertEquals("prostate	28 36	prostate", annots.get(2).toString());
	}

	@Test
	void testNoOverlapLastStates()
	{
		Matcher matcher = new MatcherBuilder().keywords("portail de la médecine instutionnelle", "médecine")
				.strategy(EMatchingStrategy.NoOverlapStrategy)
				.build();
		String text = "Portail de la médecine";
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(1, annots.size());
	}

	@Test
	void testNoOverlapStopwords()
	{
		this.matcher = new MatcherBuilder().keywords("cancer", "cancer de la prostate", "prostate", "de la")
				.strategy(EMatchingStrategy.NoOverlapStrategy)
				.stopwords("de", "la")
				.build();
		String text = "cancer de la prostate";
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(1, annots.size());
		assertEquals("cancer prostate	0 6;13 21	cancer de la prostate", annots.get(0).toString());
		text = "cancer du colon";
		annots = matcher.annot(text);
		assertEquals(1, annots.size());
		assertEquals("cancer	0 6	cancer", annots.get(0).toString());
	}
}
