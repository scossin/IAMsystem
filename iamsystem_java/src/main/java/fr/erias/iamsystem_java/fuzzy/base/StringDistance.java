package fr.erias.iamsystem_java.fuzzy.base;

/**
 * Base class of string distance algorithms.
 *
 * @author Sebastien Cossin
 *
 */
public abstract class StringDistance extends NormLabelAlgo
{

	private final int minNbChar;
	private IWord2ignore word2ignore = new NoWord2ignore();

	/**
	 *
	 * @param name      a name given to the algorithm (ex: Levenshtein).
	 * @param minNbChar the minimum number of characters a word must have in order
	 *                  not to be ignored.
	 */
	public StringDistance(String name, int minNbChar)
	{
		super(name);
		this.minNbChar = minNbChar;
	}

	/**
	 * the minimum number of characters a word must have in order not to be ignored.
	 *
	 * @return an integer.
	 */
	public int getMinNbChar()
	{
		return minNbChar;
	}

	/**
	 * Check if a word is ignored by this string distance algorithm.
	 *
	 * @param normLabel a normalized label to check.
	 * @return True if the word is ignored.
	 */
	public boolean isAword2ignore(String normLabel)
	{
		return normLabel.length() < minNbChar || word2ignore.isWord2ignore(normLabel);
	}

	/**
	 * Change words2ignore instance.
	 *
	 * @param word2ignore a class that checks if a word should be ignored.
	 */
	public void setWords2ignore(IWord2ignore word2ignore)
	{
		this.word2ignore = word2ignore;
	}
}
