package fr.erias.iamsystem.stopwords;

import fr.erias.iamsystem.tokenize.IToken;

/**
 * Stopword interface.
 *
 * @author Sebastien Cossin
 */
public interface IStopwords
{

	/**
	 * Check if a token is a stopword.
	 *
	 * @param token a generic Token created by a tokenizer.
	 * @return True if this token is a stopword.
	 */
	public boolean isTokenAStopword(IToken token);
}
