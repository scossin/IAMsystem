package fr.erias.IAMsystem.detect;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.terminology.Terminology;
public class DetectionLevenshteinTest {

	
	@Test
	public void DetectionTermTest() throws IOException {
		TermDetector termDetector = new TermDetector();
		Terminology terminology = new Terminology();
		terminology.addTerm("gastroenterite", "X1");
		termDetector.addLevenshteinIndex(terminology);
		termDetector.addTerminology(terminology);
		DetectOutput detectOutput = termDetector.detect("gastroenteritee");
		
		assertEquals(detectOutput.getCTcodes().size(), 1);
		CTcode CTdetected = detectOutput.getCTcodes().iterator().next();
		assertEquals(CTdetected.getCandidateTermString(),"gastroenteritee");
		assertEquals(CTdetected.getCode(), "X1");
	}
}
