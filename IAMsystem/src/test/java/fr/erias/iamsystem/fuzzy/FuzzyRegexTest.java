package fr.erias.iamsystem.fuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem.annotation.IAnnotation;
import fr.erias.iamsystem.fuzzy.FuzzyRegex;
import fr.erias.iamsystem.fuzzy.base.SynAlgo;
import fr.erias.iamsystem.matcher.Matcher;
import fr.erias.iamsystem.stopwords.NoStopwords;
import fr.erias.iamsystem.tokenize.IToken;
import fr.erias.iamsystem.tokenize.NormFunctions;
import fr.erias.iamsystem.tokenize.SplitFunctions;
import fr.erias.iamsystem.tokenize.Token;
import fr.erias.iamsystem.tokenize.TokenizerImp;

class FuzzyRegexTest
{

	private Token token;
	private FuzzyRegex fuzzy;
	private TokenizerImp tokenizer;

	@BeforeEach
	void setUp() throws Exception
	{
		this.fuzzy = new FuzzyRegex("regex_num", "^\\d*[.,]?\\d*$", "numval");
		this.tokenizer = new TokenizerImp(NormFunctions.normFrench, SplitFunctions.splitAlphaNumFloat);
		this.token = new Token(0, 0, "2.1", "norm label doesn' matter", 0);
	}

	@Test
	void testGetSynonyms()
	{
		List<SynAlgo> synsAlgo = this.fuzzy.getSynonyms(this.token);
		assertEquals(synsAlgo.size(), 1);
		assertEquals(synsAlgo.get(0).getSyn(), "numval");
	}

	@Test
	void testMatcher()
	{
		Matcher matcher = new Matcher(this.tokenizer, new NoStopwords());
		matcher.addFuzzyAlgo(this.fuzzy);
		matcher.addKeyword("CALCIUM NUMVAL mmol/L");
		List<IAnnotation> annots = matcher.annot("calcium 2.1 mmol/L");
		assertEquals(annots.size(), 1);
	}

	@Test
	void testTokenizer()
	{
		List<IToken> tokens = this.tokenizer.tokenize("calcium 2.1 mmol/L");
		assertEquals(tokens.size(), 4);
	}
}
