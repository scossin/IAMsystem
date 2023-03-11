package fr.erias.iamsystem_java.brat;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.matcher.MatcherBuilder;

class BratFormattersTest
{

	private String text;
	private Matcher matcher;
	private List<IAnnotation> annots;
	private IAnnotation annot;

	@BeforeEach
	void setUp() throws Exception
	{
		this.matcher = new MatcherBuilder()
				.keywords("cancer prostate")
				.stopwords("de","la")
				.w(2)
				.build();
		this.text = "cancer de la glande prostate";
		this.annots = matcher.annot(this.text);
		this.annot = this.annots.get(0);
	}

	@AfterEach
	void tearDown() throws Exception
	{
	}

	@Test
	void testDefault()
	{
		assertEquals(annot.toString(), "cancer prostate	0 6;20 28	cancer prostate");
	}

}
