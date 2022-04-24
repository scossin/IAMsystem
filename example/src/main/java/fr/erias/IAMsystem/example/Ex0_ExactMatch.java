package fr.erias.IAMsystem.example;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;

public class Ex0_ExactMatch {

	public static void main(String[] args) {
		TermDetector termDetector = new TermDetector();
		termDetector.addTerm("high blood pressure", "I10");
		String document = "The patient has a high blood pressure";
		DetectOutput detectOutput = termDetector.detect(document);
		System.out.println(detectOutput.toString());
//		1 terms detected.
//		term number 1:
//			 label in terminology: 'high blood pressure'
//			 written exactly like this in the sentence: 'high blood pressure'
//			 code in terminology: I10
//			 starting at position:18
//			 end at position:36
//			 first token number 4
//			 last token number 6
	}

}
