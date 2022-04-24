package fr.erias.IAMsystem.example;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.language.Soundex;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.synonym.StringEncoderSyn;
import fr.erias.IAMsystem.terminology.Term;

public class Ex6_customFuzzyStringEncoder {

	public static void main(String[] args) throws EncoderException {
		TermDetector termDetector = new TermDetector();
		Term term = new Term("high blood pressure", "I10");
		termDetector.addTerm(term); 
		String document = "The patient has a highhhh blood pressure.";

		StringEncoder stringEncoder = new StringEncoder() {
			@Override // ignore this one :
			public Object encode(Object source) throws EncoderException {
				return new EncoderException();
			}
			
			@Override
			public String encode(String string) throws EncoderException {
				// some complicated stuff here
				// high and highhhh have the same encoding so it will match
				if (string.equals("high")) {
					return("ENCODING_OF_HIGH");
				}
				if (string.equals("highhhh")) {
					return("ENCODING_OF_HIGH");
				}
				return("OTHER_ENCODING");
			}
		};
		
		StringEncoderSyn mySoundex = new StringEncoderSyn(stringEncoder, -1);
		mySoundex.addTerm(term, termDetector.getTokenizerNormalizer());
		termDetector.addFuzzyAlgorithm(mySoundex);
		DetectOutput detectOutput = termDetector.detect(document);
		System.out.println(detectOutput.toString());
//		1 terms detected.
//		term number 1:
//			 label in terminology: 'high blood pressure'
//			 written exactly like this in the sentence: 'highhhh blood pressure'
//			 code in terminology: I10
//			 starting at position:18
//			 end at position:39
//			 first token number 4
//			 last token number 6
	}
}
