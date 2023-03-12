package fr.erias.iamsystem_java.abbreviations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.annotation.IAnnotation;
import fr.erias.iamsystem_java.fuzzy.abbreviations.Abbreviations;
import fr.erias.iamsystem_java.fuzzy.abbreviations.TokenIsAnAbbFactory;
import fr.erias.iamsystem_java.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.stopwords.NoStopwords;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.Token;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import fr.erias.iamsystem_java.utils.MockData;

class AbbreviationsTest
{

	private Abbreviations abbs;
	private ITokenizer tokenizer;

	@BeforeEach
	void setUp() throws Exception
	{
		this.abbs = new Abbreviations("Abbs");
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
	}

	@Test
	void testAdd()
	{
		this.abbs.add("ic", "insuffisance cardiaque", tokenizer);
		IToken token = new Token(0, 1, "ic", "ic", 0);
		List<SynAlgo> syns = this.abbs.getSynonyms(token);
		assertEquals(1, syns.size());
		assertEquals("insuffisance cardiaque", syns.get(0).getSyn());
		assertEquals("Abbs", syns.get(0).getAlgo());
	}

	@Test
	void testAddMultiple()
	{
		this.abbs.add("irc", "insuffisance  respiratoire   Chronique ", tokenizer);
		this.abbs.add("irc", "Insuffisance rénale chronique", tokenizer);
		IToken token = new Token(0, 1, "irc", "irc", 0);
		List<SynAlgo> syns = this.abbs.getSynonyms(token);
		assertEquals(2, syns.size());
		for (SynAlgo synAlgo : syns)
		{
			assertTrue(synAlgo.getSyn().equals("insuffisance respiratoire chronique")
					|| synAlgo.getSyn().equals("insuffisance renale chronique"));
		}
	}

	@Test
	void testIsTokenFun()
	{
		this.abbs = new Abbreviations("Abbs", TokenIsAnAbbFactory.upperCaseOnly);
		this.abbs.add("ic", "insuffisance cardiaque", tokenizer);
		IToken token = new Token(0, 1, "ic", "ic", 0);
		List<SynAlgo> syns = this.abbs.getSynonyms(token);
		assertEquals(syns, FuzzyAlgo.NO_SYN);
		token = new Token(0, 1, "IC", "ic", 0);
		syns = this.abbs.getSynonyms(token);
		assertEquals(1, syns.size());
	}

	@Test
	void testMatcher()
	{
		Matcher matcher = new Matcher(tokenizer, new NoStopwords());
		this.abbs = new Abbreviations("Abbs", TokenIsAnAbbFactory.upperCaseOnly);
		this.abbs.add("ic", "insuffisance cardiaque", tokenizer);
		matcher.addKeyword(MockData.getICG());
		matcher.addFuzzyAlgo(abbs);
		matcher.setRemoveNestedAnnot(false);
		List<IAnnotation> anns = matcher.annot("IC gauche");
		assertEquals(anns.size(), 2);
	}

	@Test
	void testNoSynoyms()
	{
		IToken token = new Token(0, 1, "irc", "irc", 0);
		List<SynAlgo> syns = this.abbs.getSynonyms(token);
		assertEquals(0, syns.size());
		assertEquals(syns, FuzzyAlgo.NO_SYN);
	}
}
