package fr.erias.iamsystem.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.liblevenshtein.transducer.Algorithm;

import fr.erias.iamsystem.annotation.IAnnotation;
import fr.erias.iamsystem.keywords.Entity;
import fr.erias.iamsystem.keywords.IEntity;
import fr.erias.iamsystem.matcher.Matcher;
import fr.erias.iamsystem.matcher.MatcherBuilder;

class MatcherTest
{

	@BeforeEach
	void setUp() throws Exception
	{
	}

	@Test
	void testReadme() {
		Matcher matcher = new MatcherBuilder()
				.keywords("North America", "South America")
				.stopwords("and")
				.abbreviations("amer", "America")
				.levenshtein(5, 1, Algorithm.TRANSPOSITION)
				.w(2)
				.build();
		List<IAnnotation> annots = matcher.annot("Northh and south Amer.");
		assertEquals(2, annots.size());
	}
	
	@Test
	void testExactMatchCustomKeyword()
	{
		class MyKeyword implements IEntity
		{
			private String label;
			private String category;
			private String kb_name;
			private String uri;

			public MyKeyword(String label, String category, String kb_name, String uri)
			{
				this.label = label;
				this.category = category;
				this.kb_name = kb_name;
				this.uri = uri;
			}

			@Override
			public String kbid()
			{
				return this.uri;
			}

			@Override
			public String label()
			{
				return this.label;
			}
			
			@Override
			public String toString() {
				return this.uri;
			}
		}
		MyKeyword ent1 = new MyKeyword("acute respiratory distress syndrome", "disease", "wikipedia",
				"https://www.wikidata.org/wiki/Q344873");
		Entity ent2 = new Entity("diarrrhea", "R19.7");
		String text = "Pt c/o acute respiratory distress syndrome and diarrrhea";
		Matcher matcher = new MatcherBuilder().keywords(ent1, ent2).build();
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(2, annots.size());
		assertEquals(annots.get(0).toString(),
				"acute respiratory distress syndrome	7 42	https://www.wikidata.org/wiki/Q344873");
	}

	@Test
	void testExactMatchEnts()
	{
		Entity ent1 = new Entity("acute respiratory distress syndrome", "J80");
		Entity ent2 = new Entity("diarrrhea", "R19.7");
		Matcher matcher = new MatcherBuilder().keywords(ent1, ent2).build();
		List<IAnnotation> annots = matcher.annot("Pt c/o acute respiratory distress syndrome and diarrrhea");
		assertEquals(2, annots.size());
		assertEquals(annots.get(1).toString(), "diarrrhea	47 56	diarrrhea (R19.7)");
	}

	@Test
	void testExactMatchKeyword()
	{
		Matcher matcher = new MatcherBuilder().keywords("acute respiratory distress syndrome", "diarrrhea").build();
		List<IAnnotation> annots = matcher.annot("Pt c/o Acute Respiratory Distress Syndrome and diarrrhea");
		assertEquals(2, annots.size());
		IAnnotation ann = annots.get(1);
		assertEquals(ann.toString(), "diarrrhea	47 56	diarrrhea");
	}

	@Test
	void testFailOrder()
	{
		Matcher matcher = new MatcherBuilder().keywords("calcium level").w(2).build();
		List<IAnnotation> annots = matcher.annot("level calcium");
		assertEquals(0, annots.size());
	}

	@Test
	void testReadmeExample()
	{
		Matcher matcher = new MatcherBuilder().keywords("North America", "South America")
				.stopwords("and")
				.abbreviations("amer", "America")
				.levenshtein(4, 1, Algorithm.TRANSPOSITION)
				.w(2)
				.build();
		List<IAnnotation> annots = matcher.annot("Northh and south Amer.");
		assertEquals(2, annots.size());
	}

	@Test
	void testWindow()
	{
		Matcher matcher = new MatcherBuilder().keywords("calcium level").w(2).build();
		List<IAnnotation> annots = matcher.annot("calcium blood level");
		assertEquals(1, annots.size());
		assertEquals(annots.get(0).toString(), "calcium level	0 7;14 19	calcium level");
	}

}
