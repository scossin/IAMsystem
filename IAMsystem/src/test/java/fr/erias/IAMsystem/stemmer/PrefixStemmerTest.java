package fr.erias.IAMsystem.stemmer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PrefixStemmerTest {

	@Test
	public void frenchStemmertest() {
		IStemmer stemmer = new StemByPrefix(5);
		assertEquals(stemmer.stem("diabétique"), "diabé");
		assertEquals(stemmer.stem("scannographiques"), "scann");
	}
}
