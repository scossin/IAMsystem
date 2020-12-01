package fr.erias.IAMsystem.normalizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.erias.IAMsystem.normalizer.INormalizer;

public class NormalizerTermTest {
	
	@Test
    public void removeAccentTest() {
		String label = "aé;èïo-";
		String normalizedLabel = INormalizer.flattenToAscii(label);
		String expectedNormalizedLabel = "ae;eio-";
		assertEquals(normalizedLabel, expectedNormalizedLabel);
    }
	
	@Test
    public void removePunctuationTest() {
		String label = "a;-?test.";
		String normalizedLabel = INormalizer.removeSomePunctuation(label);
		String expectedNormalizedLabel = "a   test ";
		assertEquals(normalizedLabel, expectedNormalizedLabel);
    }
	
	@Test
    public void normalizedTest() {
		String label = "aé;èïo-";
		String normalizedLabel = INormalizer.normalizedSentence(label);
		String expectedNormalizedLabel = "ae eio ";
		assertEquals(normalizedLabel, expectedNormalizedLabel);
    }
}
