package fr.erias.IAMsystem.normalizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;

/**
 * Class to normalize terms and sentences
 * @author Cossin Sebastien
 *
 */
public class Normalizer implements INormalizer {

	final static Logger logger = LoggerFactory.getLogger(Normalizer.class);
	
	/**
	 * Stopwords to exclude during normalization
	 */
	private IStopwords stopwords ;
	
	
	/**
	 * Remove every character except these ones after normalization (lower case and remove accents):
	 */
	private String regexNormalizer = "[^a-z0-9]";
	
	/**
	 * Constructor 
	 * @param stopwords a class containing stopwords
	 */
	public Normalizer(IStopwords stopwords) {
		this.stopwords = stopwords;
	}
	
	/**
	 * Constructor 
	 */
	public Normalizer() {
		this.stopwords = new StopwordsImpl(); // default stopwords implementation
	}
	
	/********************************************* Getters ***************************************/
	
	/**
	 * 
	 * @return the stopwords instance of the normalizerTerm
	 */
	public IStopwords getStopwords() {
		return(stopwords);
	}
	
	/**
	 * 
	 * @param token Check if this token is a stopword
	 * @return true if the token is a stopword
	 */
	public boolean isStopWord(String token) {
		return stopwords.isStopWord(token);
	}
	
	/**
	 * Add a token to the list of stopword
	 * @param token a token to add
	 */
	public void addStopwords(String token) {
		this.stopwords.addStopwords(token);
	}
	
	/**
	 * Remove any character that is not in the regular expression
	 * @param sentence a sentence to remove punctuation
	 * @return The sentence with punctuation replace by white space
	 */
	public String removeSomePunctuation(String sentence) {
		String output = sentence.replaceAll(this.regexNormalizer, " ");
		return(output);
	}
	
	/**
	 * Remove every character except these ones after normalization (lower case and remove accents)
	 * Default any character except "[^a-z0-9]"
	 * @param regexNormalizer regular expression - remove every character except these ones
	 */
	public void setRegexNormalizer (String regexNormalizer) {
		this.regexNormalizer = regexNormalizer;
	}
	
	/**
	 * Normalize a sentence or a term : remove accents, punctuation and lowercase
	 * @param sentence a string to normalize
	 * @return a normalized String
	 */
	public String normalizedSentence(String sentence){
		String normalizedSentence = null;
		// the order matters
		
		// lowercase
		normalizedSentence = sentence.toLowerCase();
		
		//remove accents : 
		normalizedSentence = flattenToAscii(normalizedSentence); //.replaceAll("[^\\p{ASCII}]", "");
		
		// remove punctuation
		normalizedSentence = removeSomePunctuation(normalizedSentence); // µ in µg

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
	public String flattenToAscii(String string) {
	    char[] out = new char[string.length()];
	    String norm = java.text.Normalizer.normalize(string, java.text.Normalizer.Form.NFD);

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
	public String getSimpleNormalizedTerm(String term){
		//remove accents : 
		String string = java.text.Normalizer.normalize(term, java.text.Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
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
	
	@Override
	public String getNormalizedSentence(String sentence) {
		return (this.normalizedSentence(sentence));
	}
}
