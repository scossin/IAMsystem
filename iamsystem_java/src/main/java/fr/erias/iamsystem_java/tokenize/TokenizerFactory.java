package fr.erias.iamsystem_java.tokenize;

public class TokenizerFactory
{

	public static ITokenizer getTokenizer(ETokenizer tokenizer)
	{
		return tokenizer.getInstance();
	}
}
