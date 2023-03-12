package fr.erias.iamsystem.brat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem.annotation.Annotation;
import fr.erias.iamsystem.annotation.IAnnotation;
import fr.erias.iamsystem.annotation.PrintAnnot;
import fr.erias.iamsystem.brat.BratFormatters;
import fr.erias.iamsystem.matcher.Matcher;
import fr.erias.iamsystem.matcher.MatcherBuilder;

class BratFormattersTest
{

	private String text;
	private Matcher matcher;
	private List<IAnnotation> annots;
	private IAnnotation annot;

	@BeforeEach
	void setUp() throws Exception
	{
		this.matcher = new MatcherBuilder().keywords("cancer prostate").stopwords("de", "la").w(2).build();
		this.text = "cancer de la glande prostate";
		this.annots = matcher.annot(this.text);
		this.annot = this.annots.get(0);
	}

	@AfterEach
	void tearDown() throws Exception
	{
		Annotation.setPrintAnnot(new PrintAnnot());
	}

	@Test
	void testContSeqStop()
	{
		Annotation.setBratFormatter(BratFormatters.contSeqStopFormatter);
		assertEquals(annot.toString(), "cancer prostate	0 6;20 28	cancer prostate");
	}

	@Test
	void testDefault()
	{
		assertEquals(annot.toString(), "cancer prostate	0 6;20 28	cancer prostate");
	}

	@Test
	void testSpan()
	{
		Annotation.setBratFormatter(BratFormatters.spanFormatter);
		assertEquals(annot.toString(), "cancer de la glande prostate	0 28	cancer prostate");
	}
}
