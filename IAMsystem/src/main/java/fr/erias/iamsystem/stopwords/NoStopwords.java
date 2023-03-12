package fr.erias.iamsystem.stopwords;

import fr.erias.iamsystem.tokenize.IToken;

/**
 * Utility class. Class to use when no stopwords are used.
 *
 * @author Sebastien Cossin
 */
public class NoStopwords implements ISimpleStopwords
{

	@Override
	public boolean isStopword(String word)
	{
		return false;
	}

	@Override
	public boolean isTokenAStopword(IToken token)
	{
		return false;
	}
}
