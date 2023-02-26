package fr.erias.iamsystem_java.fuzzy.base;

import fr.erias.iamsystem_java.tokenize.IToken;

public abstract class StringDistance<T extends IToken> extends NormLabelAlgo<T>
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

	public StringDistance(String name, int minNbChar, IWord2ignore word2ignore)
	{
		super(name);
		this.minNbChar = minNbChar;
		this.word2ignore = word2ignore;
	}

	public int getMinNbChar()
	{
		return minNbChar;
	}

	public boolean isAword2ignore(String normLabel)
	{
		return normLabel.length() < minNbChar || word2ignore.isWord2ignore(normLabel);
	}
}
