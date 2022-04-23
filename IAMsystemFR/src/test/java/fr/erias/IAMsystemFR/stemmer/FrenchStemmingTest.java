package fr.erias.IAMsystemFR.stemmer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystemFR.synonyms.Stem;

public class FrenchStemmingTest {

	@Test
	public void stemFrenchWordsTest() {
		Stem stems = new Stem();
		assertEquals(stems.stem("diabétique"), "diabet");
		assertEquals(stems.stem("scannographiques"), "scannograph");
		assertEquals(stems.stem("aspects"), "aspect");
	}
	
	@Test
	public void detectWithStemTest() {
		TermDetector termDetector = new TermDetector();
		Stem stem = new Stem();
		termDetector.addSynonym(stem);
		
		Term term = new Term("aspects scannographiques", "E11");
		termDetector.addTerm(term);
		stem.addTerm(term, termDetector.getTokenizerNormalizer());
		
		String document = "Aspect scannograph normaux";
		DetectOutput detection = termDetector.detect(document);
		assertEquals(1, detection.getCTcodes().size());
	}
}
