package fr.erias.iamsystem_java.stopwords;

import fr.erias.iamsystem_java.tokenize.IToken;

/**
 * Negative Stopwords utility function.
 *
 * @author Sebastien Cossin
 *
 */
public interface IWord2keepF
{
	/**
	 * Check if a word should be kept.
	 *
	 * @param token A token to check.
	 * @return True if the word should be kept.
	 */
	public boolean isAword2keep(IToken token);
}
