package fr.erias.IAMsystem.fuzzymatching;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;

import org.junit.Test;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.lucene.IndexBigramLucene;
import fr.erias.IAMsystem.synonym.LevenshteinTypoLucene;
import fr.erias.IAMsystem.terminology.Terminology;
public class LevenshteinTest {

	
	@Test
	public void DetectionTermTest() throws IOException {
		TermDetector termDetector = new TermDetector();
		Terminology terminology = new Terminology();
		terminology.addTerm("gastroenterite", "X1");
		termDetector.addTerminology(terminology);
		
		IndexBigramLucene.IndexLuceneUniqueTokensBigram(terminology, termDetector.getTokenizerNormalizer());
		LevenshteinTypoLucene levenshteinTypoLucene = new LevenshteinTypoLucene();
		termDetector.addFuzzyAlgorithm(levenshteinTypoLucene);
		assertEquals(termDetector.getFuzzyAlgorithms().size(), 1); // check it was correctly added
		
		DetectOutput detectOutput = termDetector.detect("gastroenteritee");
		assertEquals(detectOutput.getCTcodes().size(), 1);
		CTcode CTdetected = detectOutput.getCTcodes().iterator().next();
		assertEquals(CTdetected.getCandidateTermString(),"gastroenteritee");
		assertEquals(CTdetected.getCode(), "X1");
		
		// check token ignored
		String token2ignore = "gastroenteritea";
		levenshteinTypoLucene.addAtoken2ignore(token2ignore);
		detectOutput = termDetector.detect(token2ignore);
		assertEquals(detectOutput.getCTcodes().size(), 0);
		// remove the token2ignore and check it's detected:
		levenshteinTypoLucene.setTokens2ignore(new HashSet<String>());
		detectOutput = termDetector.detect(token2ignore);
		assertEquals(detectOutput.getCTcodes().size(), 0); // not detected ! the cache system
		termDetector.addFuzzyAlgorithm(levenshteinTypoLucene); // reset the cache system
		detectOutput = termDetector.detect(token2ignore);
		assertEquals(detectOutput.getCTcodes().size(), 1); 
	}
}
