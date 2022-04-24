package fr.erias.IAMsystem.example;

import org.apache.commons.codec.EncoderException;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.terminology.Term;

public class Ex1_Fuzzy_Abbrevations {

	public static void main(String[] args) throws EncoderException {
		TermDetector termDetector = new TermDetector();
		Term term = new Term("high blood pressure", "I10");
		termDetector.addTerm(term); 
		String document = "The patient has a very high BP.";
		Abbreviations abbreviations = new Abbreviations();
		abbreviations.addAbbreviation("blood pressure", "bp");
		termDetector.addFuzzyAlgorithm(abbreviations);
		DetectOutput detectOutput = termDetector.detect(document);
		System.out.println(detectOutput.toString());
//		1 terms detected.
//		term number 1:
//			 label in terminology: 'high blood pressure'
//			 written exactly like this in the sentence: 'high BP'
//			 code in terminology: I10
//			 starting at position:23
//			 end at position:29
//			 first token number 5
//			 last token number 6
	}
}
