package fr.erias.iamsystem_java.tokenize;

/**
 * A factory to store default tokenizers.
 * 
 * @author Sebastien Cossin
 *
 */
public class TokenizerFactory
{

	public static ITokenizer getTokenizer(ETokenizer tokenizer)
	{
		return tokenizer.getInstance();
	}
}
