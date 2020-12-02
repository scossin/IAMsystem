package fr.erias.IAMsystem.normalizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NormalizerTest {
	
	@Test
    public void removeAccentTest() {
		Normalizer normalizer = new Normalizer();
		String label = "aé;èïo-";
		String normalizedLabel = normalizer.flattenToAscii(label);
		String expectedNormalizedLabel = "ae;eio-";
		assertEquals(normalizedLabel, expectedNormalizedLabel);
    }
	
	@Test
    public void removePunctuationTest() {
		Normalizer normalizer = new Normalizer();
		String label = "a;-?test.";
		String normalizedLabel = normalizer.removeSomePunctuation(label);
		String expectedNormalizedLabel = "a   test ";
		assertEquals(normalizedLabel, expectedNormalizedLabel);
    }
	
	@Test
    public void normalizedTest() {
		Normalizer normalizer = new Normalizer();
		String label = "Covid +";
		String normalizedLabel = normalizer.normalizedSentence(label);
		String expectedNormalizedLabel = "covid  ";
		assertEquals(normalizedLabel, expectedNormalizedLabel);
		
		// change regex normalizer expression
		normalizer.setRegexNormalizer("[^A-Za-z0-9µ+]");
		normalizedLabel = normalizer.normalizedSentence(label);
		expectedNormalizedLabel = "covid +";
		assertEquals(normalizedLabel, expectedNormalizedLabel);
    }
}
