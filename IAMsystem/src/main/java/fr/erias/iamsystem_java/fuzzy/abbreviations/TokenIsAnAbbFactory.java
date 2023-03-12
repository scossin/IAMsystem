package fr.erias.iamsystem_java.fuzzy.abbreviations;

/**
 * An utility class to store the commons to check if a token is an abbreviation.
 *
 * @author Sebastien Cossin
 *
 */
public class TokenIsAnAbbFactory
{

	public static TokenIsAnAbbreviation alwaysTrue = (token -> true);
	public static TokenIsAnAbbreviation upperCaseOnly = (token -> token.label().equals(token.label().toUpperCase()));
	public static TokenIsAnAbbreviation firstLetterCapitalized = (token -> !TokenIsAnAbbFactory.upperCaseOnly
			.isAnAbb(token) && Character.isUpperCase(token.label().charAt(0)));
}
