package fr.erias.IAMsystem.fuzzymatching;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.terminology.Term;

public class AbbreviationTest {

	@Test
	public void abbreviationTest() {
		TermDetector termDetector = new TermDetector();
		Term term3 = new Term ("insuffisance cardiaque aigue", "I50");
		termDetector.addTerm(term3);
		Abbreviations abbreviations = new Abbreviations();
		abbreviations.addAbbreviation("insuffisance", "insuf",termDetector.getTokenizerNormalizer());
		termDetector.addFuzzyAlgorithm(abbreviations);
		DetectOutput output = termDetector.detect("insuf cardiaque aigue");
		assertEquals(output.getCTcodes().size(), 1);
	}
	
	@Test
	public void abbreviationStopwordTest() {
		StopwordsImpl stopwords = new StopwordsImpl();
		stopwords.addStopwords("du");
		TermDetector termDetector = new TermDetector(stopwords);
		Term term = new Term ("infarctus du myocarde", "I50");
		termDetector.addTerm(term);
		Abbreviations abbreviations = new Abbreviations();
		abbreviations.addAbbreviation("infarctus du myocarde", "idm",termDetector.getTokenizerNormalizer());
		termDetector.addFuzzyAlgorithm(abbreviations);
		DetectOutput output = termDetector.detect("idm");
		assertEquals(output.getCTcodes().size(), 1);
	}
}
