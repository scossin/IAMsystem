package fr.erias.IAMsystemFR.stemmer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystemFR.synonyms.MedicalAbbsFR;

public class MedicalAbbsTest {

	@Test
	public void abbreviationTest() throws IOException {
		TermDetector termDetector = new TermDetector();
		Term term3 = new Term ("insuffisance cardiaque aigue", "I50");
		termDetector.addTerm(term3);
		Abbreviations abbreviations = MedicalAbbsFR.getSenseInventory(new HashSet<String>());
		termDetector.addSynonym(abbreviations);
		DetectOutput output = termDetector.detect("ins cardiaque aigue");
		assertEquals(output.getCTcodes().size(), 1);
	}
	
	@Test
	public void ignoreAbbreviationTest() throws IOException {
		TermDetector termDetector = new TermDetector();
		Term term3 = new Term ("insuffisance cardiaque aigue", "I50");
		termDetector.addTerm(term3);
		Set<String> short2ignore = new HashSet<String>();
		short2ignore.add("ins");
		Abbreviations abbreviations = MedicalAbbsFR.getSenseInventory(short2ignore);
		termDetector.addSynonym(abbreviations);
		DetectOutput output = termDetector.detect("ins cardiaque aigue");
		assertEquals(output.getCTcodes().size(), 0); // ins was ignored
	}
}
