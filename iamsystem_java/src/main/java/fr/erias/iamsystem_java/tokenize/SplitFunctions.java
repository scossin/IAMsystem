package fr.erias.iamsystem_java.tokenize;

public class SplitFunctions
{

	public static ISplitF splitAlphaNum = new SplitRegex("[\\p{IsAlphabetic}0-9_]+");
	// \\p{IsAlphabetic} includes accents, [a-zA-Z] does not.
}
