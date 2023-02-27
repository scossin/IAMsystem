package fr.erias.iamsystem_java.tokenize;

import java.util.List;

/**
 * Tokenizer Interface.
 *
 * @author Sebastien Cossin
 */
public interface ITokenizer
{

	/**
	 * Tokenize a string.
	 *
	 * @param text an unormalized string.
	 * @return A sequence of {@link IToken} type.
	 */
	public List<IToken> tokenize(String text);
}
