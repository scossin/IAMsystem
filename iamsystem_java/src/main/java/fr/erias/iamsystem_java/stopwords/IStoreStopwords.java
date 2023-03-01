package fr.erias.iamsystem_java.stopwords;

import java.util.Collection;

/**
 * A IStopwords that stores stopwords.
 *
 * @author Sebastien Cossin
 *
 */
public interface IStoreStopwords extends IStopwords
{

	/**
	 * Add stopwords.
	 *
	 * @param words A collection of stopwords.
	 */
	public void add(Collection<String> words);

	/**
	 * Add stopword.
	 *
	 * @param words A stopword
	 */
	public void add(String word);
}
