package fr.erias.IAMsystem.example;

import org.apache.commons.codec.EncoderException;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.ClosestSubString;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.tree.PrefixTrie;

public class Ex1_Fuzzy_ClosestSubString {

	public static void main(String[] args) throws EncoderException {
		String document = "The patient has a very high blood pressuress";
		int minPrefixSize = 4; // ignore truncated word less than 5 characters
		PrefixTrie prefixTrie = new PrefixTrie(minPrefixSize);
		IStopwords stopwords = new StopwordsImpl();
		Term term = new Term("high blood pressure", "I10");
		prefixTrie.addTerm(term, stopwords);
		int maxDistance = 2; // minimum number of characters from pressure to pressuress
		ISynonym fuzzyAlgorithm = new ClosestSubString(prefixTrie, maxDistance);

		TermDetector termDetector = new TermDetector();
		termDetector.addTerm(term); 
		termDetector.addFuzzyAlgorithm(fuzzyAlgorithm);
		DetectOutput detectOutput = termDetector.detect(document);
		System.out.println(detectOutput.toString());
//		1 terms detected.
//		term number 1:
//			 label in terminology: 'high blood pressure'
//			 written exactly like this in the sentence: 'high blood pressuress'
//			 code in terminology: I10
//			 starting at position:23
//			 end at position:43
//			 first token number 5
//			 last token number 7
	}
}
