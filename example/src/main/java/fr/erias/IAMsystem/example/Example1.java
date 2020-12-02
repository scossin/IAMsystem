package fr.erias.IAMsystem.example;

import java.util.HashSet;

import fr.erias.IAMsystem.detect.DetectDictionaryEntry;
import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.normalizer.Normalizer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizer.Tokenizer;
import fr.erias.IAMsystem.tokenizernormalizer.TNoutput;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystem.tree.SetTokenTreeBuilder;

/**
 * In this example we:
 * 1) show how the normalizer/tokenizer works and how to personalized them
 * 2) create manually a terminology by adding term
 * 3) manually create abbreviations
 * 4) transform the terminology to a tree datastructure
 * 5) show basic detection
 * 
 * @author Cossin Sebastien
 *
 */
public class Example1 {

	public static void main(String[] args) {
		// stopwords are empty words that will be removed from terminology and text:
		IStopwords stopwords = new StopwordsImpl();
		// add stopwords from a file or manually:
		stopwords.addStopwords("le");
		stopwords.addStopwords("la");
		
		// Normalizer:
		Normalizer normalizer = new Normalizer(stopwords);
		// this normalizer will remove accents, lowercase and
		// replace in terminology and text: 1) stopwords, 2) everything that is in the regex: [^A-Za-z0-9µ]
		String sentence = "patient Covid +";
		System.out.println(sentence + "     ------ default normalizer ----->      " + normalizer.getNormalizedSentence(sentence)); // patient covid 
		// change normalizer regex:
		normalizer.setRegexNormalizer("[^A-Za-z0-9µ+]");
		System.out.println(sentence + "     ------ personnalized normalizer ----->      " + normalizer.getNormalizedSentence(sentence)); // patient covid  
		
		// Tokenizer will tokenize a text accordingn to a regular expression
		Tokenizer tokenizer = new Tokenizer();
		String normalizeSentence = "patient covid +";
		System.out.println(normalizeSentence + "     ------ default tokenizer ----->      " + ITokenizer.arrayToString(tokenizer.tokenize(normalizeSentence), ";".charAt(0)));
		// change tokenizer expression:
		tokenizer.setPattern("[0-9]+|[a-z]+|\\+");
		System.out.println(normalizeSentence + "     ------ personnalized tokenizer ----->      " + ITokenizer.arrayToString(tokenizer.tokenize(normalizeSentence), ";".charAt(0)));
		
		// combined the 2 operations :
		sentence = "PCR Covid +";
		System.out.println("\n" + sentence + ":");
		TokenizerNormalizer tokenizerNormalizer = new TokenizerNormalizer(normalizer, tokenizer);
		TNoutput tnoutput = tokenizerNormalizer.tokenizeNormalize(sentence);
		System.out.println("unormalized token ----->      " + ITokenizer.arrayToString(tnoutput.getTokensArrayOriginal(), ";".charAt(0))); // original 
		System.out.println("normalized token ----->      " + ITokenizer.arrayToString(tnoutput.getTokens(), ";".charAt(0))); // normalized 
		
		System.out.println("\n detection in sentence: " + sentence);
		// terminology: load a terminology from a CSV file or manually by adding term:
		Terminology terminology = new Terminology();
		terminology.addTerm("covid positive", "covidPosCode", normalizer);
		
		// convert Terminology to a tree data structure:
		SetTokenTree setTokenTree = SetTokenTreeBuilder.loadTokenTree(terminology, tokenizerNormalizer);
		
		// optional: add synonyms (abbreviations etc...)
		Abbreviations abbreviations = new Abbreviations();
		abbreviations.addAbbreviation("positive","+"); // positive can be replaced by +
		HashSet<ISynonym> synonyms = new HashSet<ISynonym>();
		synonyms.add(abbreviations);
		
		//
		DetectDictionaryEntry detect = new DetectDictionaryEntry(setTokenTree, tokenizerNormalizer, synonyms);
		sentence = "le patient a une PCR Covid +";
		// detectOutput:
		DetectOutput detectOutput = detect.detectCandidateTerm(sentence);
		
		// 
		detectOutput.getTNoutput(); // this we already know
		detectOutput.getCTcodes(); // this contains the detected terms
		
		System.out.println(detectOutput.toString());
	}
}
