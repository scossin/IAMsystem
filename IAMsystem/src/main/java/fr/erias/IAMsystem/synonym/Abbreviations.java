package fr.erias.IAMsystem.synonym;

import java.util.HashMap;
import java.util.HashSet;

import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;

/**
 * Manage and store abbreviations
 * 
 * @author Cossin Sebastien
 *
 */
public class Abbreviations implements ISynonym {
	
	private HashMap<String, HashSet<String[]>> abbreviations;
	
	/**
	 * Create an instance of Abbreviations
	 */
	public Abbreviations() {
		abbreviations = new HashMap<String, HashSet<String[]>>();
	}
	
	/**
	 * Add abbreviations
	 * @param tokensArray (ex : 'accident' 'vasculaire' 'cerebral'). See {@link ITokenizerNormalizer} to tokenize
	 * @param abbreviation (ex : 'avc')
	 */
	public void addAbbreviation(String[] tokensArray, String abbreviation) {
		HashSet<String[]> temp = new HashSet<String[]>();
		if (!abbreviations.containsKey(abbreviation)) {
			temp.add(tokensArray);
			abbreviations.put(abbreviation, temp);
			return;
		}
		abbreviations.get(abbreviation).add(tokensArray);
	}
	
	/**
	 * Add abbreviations
	 * @param term (ex : 'insuf')
	 * @param abbreviation (ex : 'insuffisance')
	 * @param tokenizerNormalizer a {@link ITokenizerNormalizer}
	 */
	public void addAbbreviation(String term, String abbreviation, ITokenizerNormalizer tokenizerNormalizer) {
		String normalizedTerm = tokenizerNormalizer.getNormalizer().getNormalizedSentence(term);
		String[] tokensArray = tokenizerNormalizer.getTokenizer().tokenize(normalizedTerm);
		addAbbreviation(tokensArray, abbreviation);
	}
	
	/**
	 * Retrieve synonyms stored by abbreviations
	 */
	public HashSet<String[]> getSynonyms(String token){
		HashSet<String[]> arraySynonyms = new HashSet<String[]>();
		if (abbreviations.containsKey(token)) {
			arraySynonyms = abbreviations.get(token);
		}
		return(arraySynonyms);
	}
}
