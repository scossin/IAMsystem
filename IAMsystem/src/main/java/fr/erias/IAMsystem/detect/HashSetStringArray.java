package fr.erias.IAMsystem.detect;

import java.util.Arrays;
import java.util.HashSet;

/**
 * I created this class because I needed to implement my own method of "contains" in a HashSet of String[]<br>
 * I didn't override the method "contains" but created a new one "containsArray"
 * @author Cossin Sebastien
 *
 */
public class HashSetStringArray extends HashSet<String[] > {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Check if the array of string already exists in the HashSet
	 * @param stringArray an array of string
	 * @return true if it exists
	 */
	public boolean containsArray( String[] stringArray ) {
		for( String[] string : this ) {
			if( Arrays.equals(string, stringArray)) {
				return true;
			}
		}
		return false;
	}
}
