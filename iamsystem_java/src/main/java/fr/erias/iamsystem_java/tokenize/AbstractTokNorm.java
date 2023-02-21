package fr.erias.iamsystem_java.tokenize;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTokNorm<T extends IToken> implements ITokenizerStopwords<T>
{

	/**
	 * Tokenize and remove tokens that are stopwords.
	 *
	 * @param text A document/keyword.
	 * @return Tokens without keywords.
	 */
	public List<T> tokenizeRmStopwords(String text)
	{
		return this.tokenize(text).stream().filter(t -> !this.isTokenAStopword(t)).collect(Collectors.toList());
	}
}
