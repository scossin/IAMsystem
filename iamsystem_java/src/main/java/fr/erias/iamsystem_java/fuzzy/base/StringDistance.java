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
	private IWord2ignore word2ignore;

	/**
	 * Build a string distance fuzzy algorithm.
	 *
	 * @param name      algorithm's name
	 * @param minNbChar the minimum number of characters a word must have in order
	 *                  not to be ignored.
	 */
	public StringDistance(String name, int minNbChar)
	{
		this(name, minNbChar, new NoWord2ignore());
	}

	/**
	 *
	 * @param name        a name given to the algorithm (ex: Levenshtein).
	 * @param minNbChar   the minimum number of characters a word must have in order
	 *                    not to be ignored.
	 * @param word2ignore
	 */
	public StringDistance(String name, int minNbChar, IWord2ignore word2ignore)
	{
		super(name);
		this.minNbChar = minNbChar;
		this.word2ignore = word2ignore;
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
}
