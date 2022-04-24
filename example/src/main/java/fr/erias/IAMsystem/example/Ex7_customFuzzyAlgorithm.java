package fr.erias.IAMsystem.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.terminology.Term;

public class Ex7_customFuzzyAlgorithm {

	public static void main(String[] args) {
		TermDetector termDetector = new TermDetector();
		Term term = new Term("high blood pressure", "I10");
		Term term2 = new Term("bi polar", "E58");
		termDetector.addTerm(term); 
		termDetector.addTerm(term2); 
		ISynonym fuzzyAlgorithm = new ISynonym() {
			
			@Override
			public Set<List<String>> getSynonyms(String token) {
				// some complicated stuff here
				Set<List<String>> synonyms = new HashSet<List<String>>();
				if (token.equals("bp")) {
					List<String> list = new ArrayList<String>();
					list.add("blood");
					list.add("pressure");
					synonyms.add(list);
					// let's add another abb
					List<String> syn2 = new ArrayList<String>();
					syn2.add("bi");
					syn2.add("polar");
					synonyms.add(syn2);
				}
				return synonyms;
			}
		};
		
		String document = "The patient has a high bp.";
		termDetector.addFuzzyAlgorithm(fuzzyAlgorithm);
		DetectOutput detectOutput = termDetector.detect(document);
		System.out.println(detectOutput.toString());
//		1 terms detected.
//		term number 1:
//			 label in terminology: 'high blood pressure'
//			 written exactly like this in the sentence: 'high bp'
//			 code in terminology: I10
//			 starting at position:18
//			 end at position:24
//			 first token number 4
//			 last token number 5
		
		detectOutput = termDetector.detect("patient has bp");
		System.out.println(detectOutput.toString());
//		1 terms detected.
//		term number 1:
//			 label in terminology: 'bi polar'
//			 written exactly like this in the sentence: 'bp'
//			 code in terminology: E58
//			 starting at position:12
//			 end at position:13
//			 first token number 2
//			 last token number 2

	}
}
