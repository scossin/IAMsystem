package fr.erias.iamsystem_java.abbreviations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.abbs.Abbreviations;
import fr.erias.iamsystem_java.abbs.TokenIsAnAbbFactory;
import fr.erias.iamsystem_java.fuzzy.FuzzyAlgo;
import fr.erias.iamsystem_java.matcher.IAnnotation;
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

	private Abbreviations<IToken> abbs;
	private ITokenizer<IToken> tokenizer;

	@BeforeEach
	void setUp() throws Exception
	{
		this.abbs = new Abbreviations<IToken>("Abbs");
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
	}

	@Test
	void testAdd()
	{
		this.abbs.add("ic", "insuffisance cardiaque", tokenizer);
		IToken token = new Token(0, 1, "ic", "ic", 0);
		String[] syn = this.abbs.getSynonyms(token);
		assertEquals(1, syn.length);
		assertEquals("insuffisance cardiaque", syn[0]);
	}

	@Test
	void testAddMultiple()
	{
		this.abbs.add("irc", "insuffisance  respiratoire   Chronique ", tokenizer);
		this.abbs.add("irc", "Insuffisance rénale chronique", tokenizer);
		IToken token = new Token(0, 1, "irc", "irc", 0);
		String[] syns = this.abbs.getSynonyms(token);
		assertEquals(2, syns.length);
		for (String syn : syns)
		{
			assertTrue(
					syn.equals("insuffisance respiratoire chronique") || syn.equals("insuffisance renale chronique"));
		}
	}

	@Test
	void testIsTokenFun()
	{
		this.abbs = new Abbreviations<IToken>("Abbs", TokenIsAnAbbFactory.upperCaseOnly);
		this.abbs.add("ic", "insuffisance cardiaque", tokenizer);
		IToken token = new Token(0, 1, "ic", "ic", 0);
		String[] syns = this.abbs.getSynonyms(token);
		assertEquals(syns, FuzzyAlgo.NO_SYN);
		token = new Token(0, 1, "IC", "ic", 0);
		syns = this.abbs.getSynonyms(token);
		assertEquals(1, syns.length);
	}

	@Test
	void testMatcher()
	{
		Matcher<IToken> matcher = new Matcher<IToken>(tokenizer, new NoStopwords());
		this.abbs = new Abbreviations<IToken>("Abbs", TokenIsAnAbbFactory.upperCaseOnly);
		this.abbs.add("ic", "insuffisance cardiaque", tokenizer);
		matcher.addKeyword(MockData.getICG());
		matcher.addFuzzyAlgo(abbs);
		List<IAnnotation<IToken>> anns = matcher.annot("IC gauche");
		assertEquals(anns.size(), 2);
	}

	@Test
	void testNoSynoyms()
	{
		IToken token = new Token(0, 1, "irc", "irc", 0);
		String[] syns = this.abbs.getSynonyms(token);
		assertEquals(0, syns.length);
		assertEquals(syns, FuzzyAlgo.NO_SYN);
	}
}
