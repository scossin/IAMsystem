package fr.erias.IAMsystem.normalizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.erias.IAMsystem.normalizer.NormalizerInterface;

public class NormalizerTermTest {
	
	@Test
    public void removeAccentTest() {
		String label = "aé;èïo-";
		String normalizedLabel = NormalizerInterface.flattenToAscii(label);
		String expectedNormalizedLabel = "ae;eio-";
		assertEquals(normalizedLabel, expectedNormalizedLabel);
    }
	
	@Test
    public void removePunctuationTest() {
		String label = "a;-?test.";
		String normalizedLabel = NormalizerInterface.removePunctuation(label);
		String expectedNormalizedLabel = "a   test ";
		assertEquals(normalizedLabel, expectedNormalizedLabel);
    }
	
	@Test
    public void normalizedTest() {
		String label = "aé;èïo-";
		String normalizedLabel = NormalizerInterface.normalizedSentence(label);
		String expectedNormalizedLabel = "ae eio ";
		assertEquals(normalizedLabel, expectedNormalizedLabel);
    }
}
