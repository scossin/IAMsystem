package fr.erias.iamsystem_java.fuzzy.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class to store words that should be ignored by string distance
 * algorithms.
 *
 * @author Sebastien Cossin
 *
 */
public class SimpleWords2ignore implements IWord2ignore
{

	private final Set<String> words2ignore = new HashSet<String>();

	public SimpleWords2ignore()
	{
	}

	/**
	 * Create a new instance with words to be ignored.
	 * 
	 * @param words2ignore words to ignore.
	 */
	public SimpleWords2ignore(Collection<String> words2ignore)
	{
		this.addWords2ignore(words2ignore);
	}

	/**
	 * Add word to be ignored.
	 * 
	 * @param word2ignore a word to ignore.
	 */
	public void addWord2ignore(String word2ignore)
	{
		this.words2ignore.add(word2ignore);
	}

	/**
	 * Add words to be ignored.
	 * 
	 * @param words2ignore words to ignore.
	 */
	public void addWords2ignore(Collection<String> words2ignore)
	{
		this.words2ignore.addAll(words2ignore);
	}

	@Override
	public boolean isWord2ignore(String word)
	{
		return this.words2ignore.contains(word);
	}
}
