package fr.erias.IAMsystem.detectct;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.lucene.IndexBigramLucene;
import fr.erias.IAMsystem.synonym.LevenshteinTypoLucene;
import fr.erias.IAMsystem.terminology.Terminology;
public class DetectImpLevenshteinTest {

	
	@Test
	public void DetectionTermTest() throws IOException {
		TermDetector termDetector = new TermDetector();
		Terminology terminology = new Terminology();
		terminology.addTerm("gastroenterite", "X1");
		termDetector.addTerminology(terminology);
		
		IndexBigramLucene.IndexLuceneUniqueTokensBigram(terminology, termDetector.getTokenizerNormalizer());
		LevenshteinTypoLucene levenshteinTypoLucene = new LevenshteinTypoLucene();
		termDetector.addSynonym(levenshteinTypoLucene);
		assertEquals(termDetector.getSynonyms().size(), 1); // check it was correctly added
		DetectOutput detectOutput = termDetector.detect("gastroenteritee");
		
		assertEquals(detectOutput.getCTcodes().size(), 1);
		CTcode CTdetected = detectOutput.getCTcodes().iterator().next();
		assertEquals(CTdetected.getCandidateTermString(),"gastroenteritee");
		assertEquals(CTdetected.getCode(), "X1");
	}
}
