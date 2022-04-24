package fr.erias.IAMsystem.example;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Soundex;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.synonym.StringEncoderSyn;
import fr.erias.IAMsystem.terminology.Term;

public class Ex1_Fuzzy_Soundex {

	public static void main(String[] args) throws EncoderException {
		TermDetector termDetector = new TermDetector();
		Term term = new Term("high blood pressure", "I10");
		termDetector.addTerm(term); 
		String document = "The patient has a very highhhh BP.";
		Abbreviations abbreviations = new Abbreviations();
		abbreviations.addAbbreviation("blood pressure", "bp");
		StringEncoderSyn soundex = new StringEncoderSyn(new Soundex(), -1);
		termDetector.addFuzzyAlgorithm(abbreviations);
		termDetector.addFuzzyAlgorithm(soundex);
		soundex.addTerm(term, termDetector.getTokenizerNormalizer());
		DetectOutput detectOutput = termDetector.detect(document);
		System.out.println(detectOutput.toString());
//		1 terms detected.
//		term number 1:
//			 label in terminology: 'high blood pressure'
//			 written exactly like this in the sentence: 'highhhh BP'
//			 code in terminology: I10
//			 starting at position:23
//			 end at position:32
//			 first token number 5
//			 last token number 6
	}
}
