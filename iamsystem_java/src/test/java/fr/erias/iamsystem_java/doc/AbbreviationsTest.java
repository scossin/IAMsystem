package fr.erias.iamsystem_java.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.fuzzy.abbreviations.Abbreviations;
import fr.erias.iamsystem_java.fuzzy.abbreviations.TokenIsAnAbbFactory;
import fr.erias.iamsystem_java.keywords.Entity;
import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.matcher.MatcherBuilder;

class AbbreviationsTest
{

	private Entity ent1;
	private Entity ent2;
	private Entity ent3;
	private Entity ent4;

	@BeforeEach
	void setUp() throws Exception
	{
		this.ent1 = new Entity("acute respiratory distress", "J80");
		this.ent2 = new Entity("patient", "D007290");
		this.ent3 = new Entity("patient hospitalized", "D007297");
		this.ent4 = new Entity("physiotherapy", "D007297");
	}

	@Test
	void testAbbs()
	{
		Matcher matcher = new MatcherBuilder().abbreviations("Pt", "patient")
				.abbreviations("PT", "physiotherapy")
				.abbreviations("ARD", "Acute Respiratory Distress")
				.keywords(ent1, ent2, ent3, ent4)
				.build();
		List<IAnnotation> annots = matcher.annot("Pt hospitalized with ARD. Treament: PT");
		assertEquals(4, annots.size());
	}

	@Test
	void testAbbsFun()
	{

		Abbreviations upperCase = new Abbreviations("AbbsUpper", TokenIsAnAbbFactory.upperCaseOnly);
		Abbreviations capitalized = new Abbreviations("AbbsCapit", TokenIsAnAbbFactory.firstLetterCapitalized);
		Matcher matcher = new MatcherBuilder().keywords(ent1, ent2, ent3, ent4).build();
		upperCase.add("PT", "physiotherapy", matcher);
		upperCase.add("ARD", "Acute Respiratory Distress", matcher);
		capitalized.add("Pt", "patient", matcher);
		matcher.addFuzzyAlgo(upperCase);
		matcher.addFuzzyAlgo(capitalized);
		List<IAnnotation> annots = matcher.annot("Pt hospitalized with ARD. Treament: PT");
		assertEquals(3, annots.size());
	}

}
