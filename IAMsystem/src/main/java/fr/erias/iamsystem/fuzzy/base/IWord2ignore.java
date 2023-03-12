package fr.erias.iamsystem.fuzzy.base;

/**
 * Utility class to check if a word must be ignored by a fuzzy algorithm. This
 * interface is very similar to the IStopword but its meaning is different.
 *
 * @author Sebastien Cossin
 *
 */
public interface IWord2ignore
{
	/**
	 * Check if a string distance algorithm should ignore this word to avoid a false
	 * positive.
	 *
	 * @param word a word to test
	 * @return True if the word should be ignored.
	 */
	public boolean isWord2ignore(String word);

}
