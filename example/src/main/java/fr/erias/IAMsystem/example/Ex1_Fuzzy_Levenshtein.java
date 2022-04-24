package fr.erias.IAMsystem.example;

import java.io.IOException;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.lucene.IndexBigramLucene;
import fr.erias.IAMsystem.synonym.LevenshteinTypoLucene;
import fr.erias.IAMsystem.terminology.Terminology;

public class Ex1_Fuzzy_Levenshtein {

	public static void main(String[] args) throws IOException {
		TermDetector termDetector = new TermDetector();
		Terminology terminology = new Terminology();
		terminology.addTerm("high blood pressure", "I10");
		termDetector.addTerminology(terminology);
		String document = "The patient has a very highblood pressuree";
		
		// create a Lucene Index, open it and add it 
		IndexBigramLucene.IndexLuceneUniqueTokensBigram(terminology, termDetector.getTokenizerNormalizer()); // create the index ; do it only once
		LevenshteinTypoLucene levenshteinTypoLucene = new LevenshteinTypoLucene(); // open the index
		termDetector.addFuzzyAlgorithm(levenshteinTypoLucene);
		levenshteinTypoLucene.setMaxEdits(1); // default
		
		DetectOutput detectOutput = termDetector.detect(document);
		System.out.println(detectOutput.toString());
//		1 terms detected.
//		term number 1:
//			 label in terminology: 'high blood pressure'
//			 written exactly like this in the sentence: 'highblood pressuree'
//			 code in terminology: I10
//			 starting at position:23
//			 end at position:41
//			 first token number 5
//			 last token number 6
	}
}
