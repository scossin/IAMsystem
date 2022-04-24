package fr.erias.IAMsystemFR.synonyms;

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
import fr.erias.IAMsystemFR.synonyms.FrenchStemmer;

public class FrenchStemmingTest {

	@Test
	public void stemFrenchWordsTest() {
		FrenchStemmer stemmer = new FrenchStemmer();
		assertEquals(stemmer.stem("diabétique"), "diabet");
		assertEquals(stemmer.stem("scannographiques"), "scannograph");
		assertEquals(stemmer.stem("aspects"), "aspect");
	}
	
	@Test
	public void detectWithStemTest() throws EncoderException {
		TermDetector termDetector = new TermDetector();
		FrenchStemmer stemmer = new FrenchStemmer();
		StringEncoderSyn encoder = new StringEncoderSyn(stemmer, 5);
		termDetector.addSynonym(encoder);
		
		Term term = new Term("aspects scannographiques", "E11");
		termDetector.addTerm(term);
		encoder.addTerm(term, termDetector.getTokenizerNormalizer());
		
		String document = "Aspect scannograph normaux";
		DetectOutput detection = termDetector.detect(document);
		assertEquals(1, detection.getCTcodes().size());
	}
	
	@Test
	public void detectSoundexTest() throws EncoderException {
		TermDetector termDetector = new TermDetector();
		StringEncoder stringEncoder = new Soundex();
		StringEncoderSyn encoder = new StringEncoderSyn(stringEncoder, 5);
		termDetector.addSynonym(encoder);
		Term term = new Term("amoxicilline","A522"); // A522 is the soundex code
		termDetector.addTerm(term);
		encoder.addTerm(term, termDetector.getTokenizerNormalizer());
		DetectOutput output = termDetector.detect("amocssicilllline"); // same code A522
		assertEquals(1, output.getCTcodes().size());
	}
	
	@Test
	public void detectMetaphoneTest() throws EncoderException {
		TermDetector termDetector = new TermDetector();
		StringEncoder stringEncoder = new Metaphone();
		StringEncoderSyn encoder = new StringEncoderSyn(stringEncoder, 5);
		termDetector.addSynonym(encoder);
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
		morseEncoder.setRuleType(RuleType.EXACT); // defalut APPROX
		morseEncoder.setMaxPhonemes(10);
		// the pipe | is the separator in BeiderMorseEncoder 
		StringEncoderSyn encoder = new StringEncoderSyn(morseEncoder, 5, "\\|"); 
		termDetector.addSynonym(encoder);
		Term term = new Term("keratocone","x"); // encoded: keratokone|keratotsone
		termDetector.addTerm(term);
		encoder.addTerm(term, termDetector.getTokenizerNormalizer());
		DetectOutput output = termDetector.detect("querattocone"); // encoded: gveratokone|keratokone
		assertEquals(1, output.getCTcodes().size());
	}
}
