package fr.erias.IAMsystem.example;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;

public class Ex2_Stopwords {

	public static void main(String[] args) {
		TermDetector termDetector = new TermDetector();
		IStopwords stopwords = new StopwordsImpl();
		stopwords.addStopwords("sai");
		stopwords.addStopwords("very");
		termDetector.setStopwords(stopwords); // otherwise it doesn't detect
		termDetector.addTerm("high blood pressure SAI", "I10");
		String document = "The patient has a high (very very) blood pressure";
		DetectOutput detectOutput = termDetector.detect(document);
		System.out.println(detectOutput.toString());
//		1 terms detected.
//		term number 1:
//			 label in terminology: 'high blood pressure SAI'
//			 written exactly like this in the sentence: 'high (very very) blood pressure'
//			 code in terminology: I10
//			 starting at position:18
//			 end at position:48
//			 first token number 4
//			 last token number 8
	}

}
