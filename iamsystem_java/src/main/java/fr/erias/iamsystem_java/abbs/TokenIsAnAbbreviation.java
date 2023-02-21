package fr.erias.iamsystem_java.abbs;

import fr.erias.iamsystem_java.tokenize.IToken;

public interface TokenIsAnAbbreviation<T extends IToken>
{
	public boolean isAnAbb(T token);
}
