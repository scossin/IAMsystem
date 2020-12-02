package fr.erias.IAMsystem.stopwords;

import java.util.Arrays;

/**
 * Class representing stopwords
 * @author Cossin Sebastien
 *
 */
public interface IStopwords {
	
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
	
	/**
	 * Remove stopwords in an array of tokens
	 * @param tokensArray an array of tokens (string) that may contain stopwords
	 * @param stopwords : a {@link IStopwords} instance
	 * @return an array of tokens without stopwords
	 */
	public static String[] removeStopWords(IStopwords stopwords, String[] tokensArray){
		String[] newTokensArray = new String[tokensArray.length];
		int numberOfStopwords = 0;
		for (int i = 0 ; i<tokensArray.length; i++) {
			String token = tokensArray[i];
			if (stopwords.isStopWord(token)) {
				numberOfStopwords = numberOfStopwords + 1;
				continue;
			} else {
				newTokensArray[i-numberOfStopwords] = token;
			}
		}
		newTokensArray = Arrays.copyOfRange(newTokensArray, 0, 
				tokensArray.length - numberOfStopwords);
		return(newTokensArray);
	}
}
