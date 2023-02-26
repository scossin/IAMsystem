package fr.erias.iamsystem_java.fuzzy.abbreviations;

import fr.erias.iamsystem_java.tokenize.IToken;

public class TokenIsAnAbbFactory
{

	public static TokenIsAnAbbreviation<IToken> alwaysTrue = (token -> true);
	public static TokenIsAnAbbreviation<IToken> upperCaseOnly = (token -> token.label()
			.equals(token.label().toUpperCase()));

}
