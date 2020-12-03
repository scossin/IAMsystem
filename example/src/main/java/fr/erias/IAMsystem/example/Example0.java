package fr.erias.IAMsystem.example;

import java.io.IOException;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.normalizer.Normalizer;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizer.Tokenizer;

/**
 * In this example we show how to use TermDetector class
 * 1) change default Normalizer/Tokenizer
 * 2) add abbrevations
 * 3) add Terms manually 
 * 4) create a Lucene index
 * 5) Detect
 * 
 * @author Cossin Sebastien
 *
 */
public class Example0 {

	public static void main(String[] args) throws IOException {
		// this class encapsulates everything you need
		// default Normalizer/Tokenizer
		TermDetector termDetector = new TermDetector();
		
		// change the default normalizer and tokenizer if you need:
		// Normalizer:
		Normalizer normalizer = new Normalizer(termDetector.getStopwords());
		normalizer.setRegexNormalizer("[^A-Za-z0-9µ+-]"); // default [^A-Za-z0-9µ]
		termDetector.getTokenizerNormalizer().setNormalizer(normalizer);
		
		// Tokenizer:
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.setPattern("[0-9]+|[a-z]+|\\+"); // default "[0-9]+|[a-z]+";
		termDetector.getTokenizerNormalizer().setTokenizer(tokenizer);
		
		// add abbreviations
		termDetector.addAbbreviations("positive", "+");
		termDetector.addAbbreviations("Sars-Cov-2", "covid");
		
		// add term or terminology
		termDetector.addTerm("PCR Sars-Cov-2 positive", "codePostive");
		
		//
		String sentence = "le patient a une PCR Covid +";
		DetectOutput detectOutput = termDetector.detect(sentence);
		System.out.println(detectOutput.toString());
		
		sentence = "le patient a une PCR SarsCov-2 +"; // not detected because not exactly like Sars-Cov-2
		detectOutput = termDetector.detect(sentence);
		System.out.println(detectOutput.toString());
		
		// add Levenshtein distance to detect it:
		Terminology terminology = new Terminology(); // can be loaded from a CSV file see: new Terminology(in, sep, colLabel, colCode) 
		terminology.addTerm("PCR Sars-Cov-2 positive", "codePostive", normalizer);
		termDetector.addLevenshteinIndex(terminology);
		detectOutput = termDetector.detect(sentence);
		System.out.println(detectOutput.toString());
	}
}
