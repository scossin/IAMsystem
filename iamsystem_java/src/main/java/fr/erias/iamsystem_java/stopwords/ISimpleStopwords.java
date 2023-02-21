package fr.erias.iamsystem_java.stopwords;

import fr.erias.iamsystem_java.tokenize.IToken;

public interface ISimpleStopwords<T extends IToken> extends IStopwords<T>
{

	/**
	 * Check if a string is a stopword.
	 *
	 * @param word A word to check.
	 * @return True if this string is a stopword.
	 */
	public boolean isStopword(String word);
}
