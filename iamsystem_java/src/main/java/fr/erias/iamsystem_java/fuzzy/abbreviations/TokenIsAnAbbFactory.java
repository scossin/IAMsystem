package fr.erias.iamsystem_java.fuzzy.abbreviations;

public class TokenIsAnAbbFactory
{

	public static TokenIsAnAbbreviation alwaysTrue = (token -> true);
	public static TokenIsAnAbbreviation upperCaseOnly = (token -> token.label().equals(token.label().toUpperCase()));

}
