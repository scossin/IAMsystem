package fr.erias.IAMsystem.example;

import java.io.IOException;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.normalizer.Normalizer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;

public class Ex5_customTokenizerNormalizer {

	public static void main(String[] args) throws IOException {
		IStopwords stopwords = new StopwordsImpl();
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer();
		
		// Custom normalizer: the default pattern is [^a-z0-9] which removes "+" symbol
		Normalizer normalizer = new Normalizer(stopwords);
		normalizer.setRegexNormalizer("[^a-z0-9+-]"); // this pattern keeps "+" and "-" symbol
		tokenizerNormalizer.setNormalizer(normalizer);
		
		TermDetector termDetector = new TermDetector(tokenizerNormalizer);

		// You can also change the default whitespace tokenizer by a regular expression tokenizer:
//		Tokenizer tokenizer = new Tokenizer();
//		tokenizer.setPattern("[0-9]+|[a-z]+|\\+"); // default "[0-9]+|[a-z]+";
//		termDetector.getTokenizerNormalizer().setTokenizer(tokenizer);
		
		// add abbreviations
		Abbreviations abbreviations = new Abbreviations();
		termDetector.addFuzzyAlgorithm(abbreviations);
		
		// use the TokenizerNormalizer of the termDetector to tokenize and normalize abbreviation:
		abbreviations.addAbbreviation("positive", "+"); // no normalization
		abbreviations.addAbbreviation("Sars-Cov-2", "covid", tokenizerNormalizer); // normalization: "Sars-Cov-2 => sars cov 2"
		
		// add term or terminology
		termDetector.addTerm("PCR Sars-Cov-2 positive", "loinc_code");
		
		String document = "PCR Covid +";
		DetectOutput detectOutput = termDetector.detect(document);
		System.out.println(detectOutput.toString());	
//		1 terms detected.
//		term number 1:
//			 label in terminology: 'PCR Sars-Cov-2 positive'
//			 written exactly like this in the sentence: 'PCR Covid +'
//			 code in terminology: loinc_code
//			 starting at position:0
//			 end at position:10
//			 first token number 0
//			 last token number 2
	}
}
