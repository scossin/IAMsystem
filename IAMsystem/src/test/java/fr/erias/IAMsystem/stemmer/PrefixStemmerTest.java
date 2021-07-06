package fr.erias.IAMsystem.stemmer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.synonym.Stems;
import fr.erias.IAMsystem.terminology.Terminology;

public class PrefixStemmerTest {

	@Test
	public void frenchStemmertest() {
		IStemmer stemmer = new StemByPrefix(5);
		assertEquals(stemmer.stem("diabétique"), "diabé");
		assertEquals(stemmer.stem("scannographiques"), "scann");
	}
	
	@Test
	public void detectionWithStemsTest() {
		TermDetector termDetector = new TermDetector();
		
		Terminology terminology = new Terminology();
		terminology.addTerm("diabete", "E11", termDetector.getTokenizerNormalizer().getNormalizer());
		termDetector.addTerminology(terminology);
		
		IStemmer stemmer = new StemByPrefix(5);
		Stems stems = new Stems(stemmer, terminology, termDetector.getTokenizerNormalizer());
		termDetector.addSynonym(stems);
		
		DetectOutput detectOutput = termDetector.detect("le patient est diabétique");
		assertEquals(detectOutput.getCTcodes().size(), 1);
	}
}
