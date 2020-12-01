package fr.erias.IAMsystem.normalizer;

import java.text.Normalizer;

public interface INormalizer {
	
	/**
	 * Add a token to the list of stopword
	 * @param token a token to add
	 */
	public void addStopwords(String token);
	
	/**
	 * 
	 * @param token Check if this token is a stopword
	 * @return true if the token is a stopword
	 */
	public boolean isStopWord(String token);
	
	/**
	 * Normalize sentence or term
	 * @param sentence a term or sentence to normalize
	 */
	public void setSentence(String sentence);
	
	/**
	 * 
	 * @return The sentence after normalization
	 */
	public String getNormalizedSentence();
	
	/**
	 * @return The original sentence
	 */
	public String getOriginalSentence();
	
	

	/**
	 * Remove any character that is not "A-Za-z0-9µ" by a white space
	 * @param sentence a sentence to remove punctuation
	 * @return The sentence with punctuation replace by white space
	 */
	public static String removeSomePunctuation(String sentence) {
		String output = sentence.replaceAll("[^A-Za-z0-9µ]", " "); // µ in µg
		return(output);
	}
	
	/**
	 * Normalize a sentence or a term : remove accents, punctuation and lowercase
	 * @param sentence a string to normalize
	 * @return a normalized String
	 */
	public static String normalizedSentence(String sentence){
		String normalizedSentence = null;
		
		//remove accents : 
		normalizedSentence = flattenToAscii(sentence); //.replaceAll("[^\\p{ASCII}]", "");
		
		// remove 
		normalizedSentence = removeSomePunctuation(normalizedSentence); // µ in µg

		// lowercase
		normalizedSentence = normalizedSentence.toLowerCase();
		return (normalizedSentence);
	}
	
	// https://stackoverflow.com/questions/3322152/is-there-a-way-to-get-rid-of-accents-and-convert-a-whole-string-to-regular-lette
	// The idea is to replace accent (éè...) by non accent char (e) without removing non-ASCII char
	// Previous method was : Normalizer.normalize(sentence, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	// it removes accent but also char like "µ" too (this is a bug)
	/**
	 * Remove accents
	 * @param string A term
	 * @return The term without accents
	 */
	public static String flattenToAscii(String string) {
	    char[] out = new char[string.length()];
	    String norm = Normalizer.normalize(string, Normalizer.Form.NFD);

	    int j = 0;
	    for (int i = 0, n = norm.length(); i < n; ++i) {
	        char c = norm.charAt(i);
	        int type = Character.getType(c);
	        if (type != Character.NON_SPACING_MARK){
	            out[j] = c;
	            j++;
	        }
	    }
	    return new String(out);
	}
	
	@Deprecated
	/**
	 * Use normalizedSentence
	 * Normalized a term
	 * @param term a term to normalize
	 * @return a normalized term
	 */
	public static String getSimpleNormalizedTerm(String term){
		//remove accents : 
		String string = Normalizer.normalize(term, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		// remove punctuation
		string = string.replaceAll("\\p{P}", " ");
		// remove multispace
		string = string.replaceAll("[ ]+", " ");
		// remove space at the end
		string = string.trim();
		// lowercase
		string = string.toLowerCase();
		return (string);
	}
}
