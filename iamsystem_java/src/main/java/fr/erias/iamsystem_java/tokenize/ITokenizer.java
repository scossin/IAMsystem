package fr.erias.iamsystem_java.tokenize;

import java.util.List;

/**
 * Tokenizer Interface.
 *
 * @author Sebastien Cossin
 * @param <T> A generic token.
 */
public interface ITokenizer<T extends IToken>
{

	/**
	 * Tokenize a string.
	 *
	 * @param text an unormalized string.
	 * @return A sequence of {@link IToken} type.
	 */
	public List<T> tokenize(String text);
}
