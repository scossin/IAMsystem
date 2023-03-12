package fr.erias.iamsystem.tokenize;

import java.util.List;
import java.util.stream.Collectors;

import fr.erias.iamsystem.stopwords.IStopwords;

/**
 * An interface to combine a tokenizer and stopwords. Theses two classes must be
 * used together to remove stopwords.
 *
 * @author Sebastien Cossin
 *
 */
public interface ITokenizerStopwords extends ITokenizer, IStopwords
{

	/**
	 * Tokenize the text, remove stopwords.
	 *
	 * @param text      a document / keyword.
	 * @param tokenizer A {@link ITokenizer}
	 * @param stopwords A {@link IStopwords}
	 * @return A sequence of {@link IToken} without stopwords.
	 */
	public static List<IToken> tokenizeRmStopwords(String text, ITokenizer tokenizer, IStopwords stopwords)
	{
		return tokenizer.tokenize(text)
				.stream()
				.filter(t -> !stopwords.isTokenAStopword(t))
				.collect(Collectors.toList());
	}

	/**
	 * Tokenize the text, remove stopwords.
	 *
	 * @param text    a document / keyword.
	 * @param tokstop a {@link ITokenizerStopwords} instance to tokenize and detect
	 *                stopwords.
	 * @return A sequence of {@link IToken} without stopwords.
	 */
	public static List<IToken> tokenizeRmStopwords(String text, ITokenizerStopwords tokstop)
	{
		return tokstop.tokenize(text).stream().filter(t -> !tokstop.isTokenAStopword(t)).collect(Collectors.toList());
	}
}
