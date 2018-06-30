package fr.erias.IAMsystem.normalizer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import fr.erias.IAMsystem.load.Loader;
import fr.erias.IAMsystem.tokenizer.TokenizerNormalizer;

public class TokenizerNormalizerTest {
	
	@Test
    public void tokenizationTest() throws IOException {
		String label = "Insuf. cardio-Vasculaire";
		StopwordsImpl stopwords = new StopwordsImpl();
		TokenizerNormalizer tokenizerNormalizer = Loader.getTokenizerNormalizer(stopwords);
		tokenizerNormalizer.tokenize(label);
		String[] tokens =  tokenizerNormalizer.getTokens();
		assertEquals(tokens.length, 3);
		assertEquals(tokens[0], "insuf");
		assertEquals(tokens[1], "cardio");
		assertEquals(tokens[2], "vasculaire");
		
		String[] tokensOriginal = tokenizerNormalizer.getTokensArrayOriginal();
		assertEquals(tokensOriginal.length, 3);
		assertEquals(tokensOriginal[0], "Insuf");
		assertEquals(tokensOriginal[1], "cardio");
		assertEquals(tokensOriginal[2], "Vasculaire");
    }
}
