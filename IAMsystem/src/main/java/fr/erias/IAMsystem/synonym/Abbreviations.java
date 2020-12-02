package fr.erias.IAMsystem.synonym;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Manage and store abbreviations
 * 
 * @author Cossin Sebastien
 *
 */
public class Abbreviations implements Synonym {
	
	private HashMap<String, HashSet<String[]>> abbreviations;
	
	/**
	 * Create an instance of Abbreviations
	 */
	public Abbreviations() {
		abbreviations = new HashMap<String, HashSet<String[]>>();
	}
	
	/**
	 * Add abbreviations
	 * @param tokensArray (ex : 'accident' 'vasculaire' 'cerebral'
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
