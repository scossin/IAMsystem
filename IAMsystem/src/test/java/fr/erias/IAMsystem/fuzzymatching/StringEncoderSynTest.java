package fr.erias.IAMsystem.fuzzymatching;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.codec.language.bm.BeiderMorseEncoder;
import org.apache.commons.codec.language.bm.RuleType;
import org.junit.Test;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.synonym.StringEncoderSyn;
import fr.erias.IAMsystem.terminology.Term;

public class StringEncoderSynTest {

	@Test
	public void detectSoundexTest() throws EncoderException {
		TermDetector termDetector = new TermDetector();
		StringEncoder stringEncoder = new Soundex();
		StringEncoderSyn encoder = new StringEncoderSyn(stringEncoder, 5);
		termDetector.addFuzzyAlgorithm(encoder);
		Term term = new Term("amoxicilline","A522"); // A522 is the soundex code
		termDetector.addTerm(term);
		encoder.addTerm(term, termDetector.getTokenizerNormalizer());
		DetectOutput output = termDetector.detect("amocssicilllline"); // same code A522
		assertEquals(1, output.getCTcodes().size());
	}
	
	@Test
	public void minLengthTokenTest() throws EncoderException {
		TermDetector termDetector = new TermDetector();
		StringEncoder stringEncoder = new Soundex();
		int minLengthToken = "amoxicilline".length() + 1 ;
		StringEncoderSyn encoder = new StringEncoderSyn(stringEncoder, minLengthToken);
		termDetector.addFuzzyAlgorithm(encoder);
		Term term = new Term("amoxicilline","A522");
		termDetector.addTerm(term);
		encoder.addTerm(term, termDetector.getTokenizerNormalizer());
		DetectOutput output = termDetector.detect("amocssicilllline");
		assertEquals(0, output.getCTcodes().size()); 
		// 0 because the token is ignored because it's too long
	}
	
	@Test
	public void detectMetaphoneTest() throws EncoderException {
		TermDetector termDetector = new TermDetector();
		StringEncoder stringEncoder = new Metaphone();
		StringEncoderSyn encoder = new StringEncoderSyn(stringEncoder, 5);
		termDetector.addFuzzyAlgorithm(encoder);
		Term term = new Term("amoxicilline","AMKS"); // AMKS is metaphone code
		termDetector.addTerm(term);
		encoder.addTerm(term, termDetector.getTokenizerNormalizer());
		DetectOutput output = termDetector.detect("amocssicilllline"); // same code AMKS
		assertEquals(1, output.getCTcodes().size());
	}
	
	@Test
	public void detectBeiderMorseEncoderTest() throws EncoderException {
		TermDetector termDetector = new TermDetector();
		BeiderMorseEncoder morseEncoder = new BeiderMorseEncoder();
		morseEncoder.setRuleType(RuleType.EXACT); // default APPROX
		morseEncoder.setMaxPhonemes(10);
		// the pipe | is the separator in BeiderMorseEncoder 
		StringEncoderSyn encoder = new StringEncoderSyn(morseEncoder, 5, "\\|"); 
		termDetector.addFuzzyAlgorithm(encoder);
		Term term = new Term("keratocone","x"); // encoded: keratokone|keratotsone
		termDetector.addTerm(term);
		encoder.addTerm(term, termDetector.getTokenizerNormalizer());
		DetectOutput output = termDetector.detect("querattocone"); // encoded: gveratokone|keratokone
		assertEquals(1, output.getCTcodes().size());
	}
}
