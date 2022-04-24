package fr.erias.IAMsystemFR.stemmer;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.EncoderException;
import org.junit.Test;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.synonym.StringEncoderSyn;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystemFR.synonyms.CapitaineSoundex;

public class SoundexFR {

	@Test
	public void detectSoundexFrTest() throws EncoderException {
		TermDetector termDetector = new TermDetector();
		CapitaineSoundex frSoundex = new CapitaineSoundex();
		StringEncoderSyn encoder = new StringEncoderSyn(frSoundex, 5, frSoundex.getSeparator());
		termDetector.addSynonym(encoder);
		Term term = new Term("seroplex","x");
		termDetector.addTerm(term);
		encoder.addTerm(term, termDetector.getTokenizerNormalizer());
		DetectOutput output = termDetector.detect("cerroppllex");
		assertEquals(1, output.getCTcodes().size());
	}
}
