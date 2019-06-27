package fr.erias.IAMsystem.normalizer;

/**
 * Class representing stopwords
 * @author Cossin Sebastien
 *
 */
public interface Stopwords {
	
	/**
	 * Is this term or token a stopword ?
	 * @param token A term to check
	 * @return true if it's a stopword
	 */
	public boolean isStopWord(String token);

	/**
	 * Add a term or a token to a list of stopword
	 * @param token a stopword
	 */
	public void addStopwords(String token);
}
