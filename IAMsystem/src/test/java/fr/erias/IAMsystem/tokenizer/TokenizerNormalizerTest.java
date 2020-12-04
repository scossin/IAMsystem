package fr.erias.IAMsystem.tokenizer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.tokenizernormalizer.TNoutput;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;

public class TokenizerNormalizerTest {

	@Test
	public void tokenizationTest() throws IOException {
		String label = "Insuf. cardio-Vasculaire";
		StopwordsImpl stopwords = new StopwordsImpl();
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer(stopwords);
		TNoutput tnoutput = tokenizerNormalizer.tokenizeNormalize(label);
		String[] tokens =  tnoutput.getTokens();
		assertEquals(tokens.length, 3);
		assertEquals(tokens[0], "insuf");
		assertEquals(tokens[1], "cardio");
		assertEquals(tokens[2], "vasculaire");

		String[] tokensOriginal = tnoutput.getTokensArrayOriginal();
		assertEquals(tokensOriginal.length, 3);
		assertEquals(tokensOriginal[0], "Insuf");
		assertEquals(tokensOriginal[1], "cardio");
		assertEquals(tokensOriginal[2], "Vasculaire");
	}

	@Test
	public void tnoutputTest() throws IOException {
		String label = "Insuf. cardio-Vasculaire";
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer();
		TNoutput tnoutput = tokenizerNormalizer.tokenizeNormalize(label);

		assertEquals(tnoutput.getTokens().length, 3);
		assertEquals(tnoutput.getOriginalSentence(), label);
		assertEquals(tnoutput.getNormalizedSentence(), "insuf  cardio vasculaire");
		assertEquals(tnoutput.getStatus(), 200);
		String[] tokensOriginal = tnoutput.getTokensArrayOriginal();
		assertEquals(tokensOriginal[0], "Insuf");
		assertEquals(tokensOriginal[1], "cardio");
		assertEquals(tokensOriginal[2], "Vasculaire");
	}
	
	public void tokenizerTest() {
		TokenizerWhiteSpace tokenizerWhiteSpace = new TokenizerWhiteSpace();
		String sentence = "le patient prend du kardegic75";
		String tokens = ITokenizer.arrayToString(tokenizerWhiteSpace.tokenize(sentence), ";".charAt(0));
		assertEquals(tokens, "le;patient;prend;du;kardegic75");
		
		Tokenizer tokenizer = new Tokenizer();
		tokens = ITokenizer.arrayToString(tokenizer.tokenize(sentence), ";".charAt(0));
		assertEquals(tokens, "le;patient;prend;du;kardegic;75"); // in comparison with whitespace tokenizer kardegic and 75 are separated
	}
}
