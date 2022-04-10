package fr.erias.IAMsystem.tree;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import fr.erias.IAMsystem.detect.DetectionTest;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.synonym.ISynonym;


public class SynonymTest {

	public static ISynonym getAbbreviation() {
		Abbreviations abbreviation = new Abbreviations();
		abbreviation.addAbbreviation("cardiaque", "cardiaque");
		return(abbreviation);
	}
	
	@Test
	public void listEqualityTest() {
		// Lists in Java are ordered by nature. 
		// So, two lists are considered to be equal if they contain the exact same elements in the same order
		Set<List<String>> currentSynonyms = new HashSet<List<String>>(); 
		List<String> tokens1 = List.of("insuffisance","cardiaque");
		List<String> tokens2 = List.of("insuffisance","cardiaque");
		currentSynonyms.add(tokens1);
		currentSynonyms.add(tokens2);
		assertTrue(currentSynonyms.size() == 1);
	}
	
	@Test
	public void numberOfSequenceReturnedBySynonymsTest() {
		ISynonym levenshtein = DetectionTest.getCardiaquTypo();
		ISynonym abbreviation = getAbbreviation();
		Set<ISynonym> synonyms = new HashSet<ISynonym>();
		synonyms.add(levenshtein);
		synonyms.add(abbreviation);
		// find synonyms (typos and abbreviations) :
		Set<List<String>> currentSynonyms = new HashSet<List<String>>(); // reinitializing synonyms

		// find synonyms: 
		String token = "cardiaque";
		for (ISynonym synonym : synonyms) {
			currentSynonyms.addAll(synonym.getSynonyms(token)); // ex : typos and abbreviations
		}
		assertTrue(currentSynonyms.size() == 1); // both returns the same synonym: 'cardiaqu'
	}
}
