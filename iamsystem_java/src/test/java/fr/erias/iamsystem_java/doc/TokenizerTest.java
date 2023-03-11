package fr.erias.iamsystem_java.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.annotation.IAnnotation;
import fr.erias.iamsystem_java.keywords.Entity;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.matcher.MatcherBuilder;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.ISplitF;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.NormFunctions;
import fr.erias.iamsystem_java.tokenize.SplitRegex;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import fr.erias.iamsystem_java.tokenize.TokenizerImp;

public class TokenizerTest
{

	@Test
	void testCustomTokenizer()
	{
		ISplitF split = new SplitRegex("[\\p{IsAlphabetic}0-9_]+|[+]+");
		ITokenizer tokenizer = new TokenizerImp(NormFunctions.lowerCase, split);
		List<IToken> tokens = tokenizer.tokenize("SARS-CoV+");
		assertEquals(3, tokens.size());
		assertEquals(tokens.get(2).toString(), "Token(label='+', norm_label='+', start=8, end=9, i=2)");
	}

	@Test
	void testMatcherCustomTokenizer()
	{
		ISplitF split = new SplitRegex("[\\p{IsAlphabetic}0-9_]+|[+]+");
		ITokenizer tokenizer = new TokenizerImp(NormFunctions.lowerCase, split);
		Entity ent1 = new Entity("SARS-CoV+", "95209-3");
		Matcher matcher = new MatcherBuilder().tokenizer(tokenizer).keywords(ent1).build();
		String text = "Pt c/o acute respiratory distress syndrome. RT-PCR sars-cov+";
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(1, annots.size());
	}

	@Test
	void testTokenizer()
	{
		ITokenizer tokenizer = TokenizerFactory.getTokenizer(ETokenizer.ENGLISH);
		List<IToken> tokens = tokenizer.tokenize("SARS-CoV+");
		assertEquals(2, tokens.size());
	}

	@Test
	void testUnorderedWordsSeq()
	{
		String text = "the level of calcium can measured in the blood.";
		Matcher matcher = new MatcherBuilder().keywords("blood calcium level").orderTokens(true).w(5).build();
		List<IAnnotation> annots = matcher.annot(text);
		assertEquals(1, annots.size());
	}
}
