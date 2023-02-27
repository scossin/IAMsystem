package fr.erias.iamsystem_java.tokenize;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTokNorm implements ITokenizerStopwords
{

	public static List<IToken> tokenizeRmStopwords(String text, ITokenizerStopwords tokstop)
	{
		return tokstop.tokenize(text).stream().filter(t -> !tokstop.isTokenAStopword(t)).collect(Collectors.toList());
	}

	/**
	 * Tokenize and remove tokens that are stopwords.
	 *
	 * @param text A document/keyword.
	 * @return Tokens without keywords.
	 */
	public List<IToken> tokenizeRmStopwords(String text)
	{
		return this.tokenize(text).stream().filter(t -> !this.isTokenAStopword(t)).collect(Collectors.toList());
	}
}
