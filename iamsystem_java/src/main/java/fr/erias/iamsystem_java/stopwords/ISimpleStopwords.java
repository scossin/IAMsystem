package fr.erias.iamsystem_java.stopwords;

public interface ISimpleStopwords extends IStopwords
{

	/**
	 * Check if a string is a stopword.
	 *
	 * @param word A word to check.
	 * @return True if this string is a stopword.
	 */
	public boolean isStopword(String word);
}
