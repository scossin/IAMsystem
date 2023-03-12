package fr.erias.iamsystem.stopwords;

import fr.erias.iamsystem.tokenize.IToken;

/**
 * Delegate stopword checking to a token's label.
 *
 * @author Sebastien Cossin
 *
 */
public abstract class SimpleStopwords implements ISimpleStopwords
{

	@Override
	public abstract boolean isStopword(String word);

	@Override
	public boolean isTokenAStopword(IToken token)
	{
		return isStopword(token.label());
	}
}
