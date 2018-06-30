package fr.erias.IAMsystem.Romedi.config;

import java.util.HashMap;
import java.util.HashSet;

import fr.erias.IAMsystem.detect.Synonym;

/**
 * Drugs abbreviations example
 * @author Cossin Sebastien
 *
 */
public class AbbreviationsDrug implements Synonym {
	
	private HashMap<String, HashSet<String[]>> abbreviations;
	
	public AbbreviationsDrug() {
		abbreviations = new HashMap<String, HashSet<String[]>>();
		
		// adding abbreviations :
		HashSet<String[]> temp = new HashSet<String[]>();
		temp.add(new String[] {"acide"});
		abbreviations.put("ac", temp);
		
		temp = new HashSet<String[]>();
		temp.add(new String[] {"kardegic"});
		abbreviations.put("kdg", temp);
		
	}
	
	public HashSet<String[]> getSynonyms(String token){
		HashSet<String[]> arraySynonyms = new HashSet<String[]>();
		if (abbreviations.containsKey(token)) {
			arraySynonyms = abbreviations.get(token);
		}
		return(arraySynonyms);
	}
}
