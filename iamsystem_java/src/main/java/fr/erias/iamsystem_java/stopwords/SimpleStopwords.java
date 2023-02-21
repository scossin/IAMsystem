package fr.erias.iamsystem_java.stopwords;

import fr.erias.iamsystem_java.tokenize.IToken;

public abstract class SimpleStopwords<T extends IToken> implements ISimpleStopwords<T>
{

	@Override
	public abstract boolean isStopword(String word);

	@Override
	public boolean isTokenAStopword(IToken token)
	{
		return isStopword(token.label());
	}
}
