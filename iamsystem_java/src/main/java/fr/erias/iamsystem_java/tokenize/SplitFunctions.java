package fr.erias.iamsystem_java.tokenize;

public class SplitFunctions
{
	// p{IsAlphabetic} includes accents, [a-zA-Z] does not.
	public static ISplitF splitAlphaNum = new SplitRegex("[\\p{IsAlphabetic}0-9_]+");

	public static ISplitF splitChar = new SplitRegex("[\\p{IsAlphabetic}0-9_]");
	
	public static ISplitF splitAlphaNumFloat = new SplitRegex("[\\p{IsAlphabetic}0-9_,\\.]+");

}
