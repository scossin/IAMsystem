package fr.erias.iamsystem_java.tokenize;

import java.util.List;

import fr.erias.iamsystem_java.stopwords.IStopwords;

/**
 * Utility class to encapsulate a {@link ITokenizer} and a {@link IStopwords}
 * that handle the same generic token.
 *
 * @author Sebastien Cossin
 * @param <T>
 */
public class TokStopImp<T extends IToken> extends AbstractTokNorm<T>
{

	private final ITokenizer<T> tokenizer;
	private final IStopwords<T> stopwords;

	public TokStopImp(ITokenizer<T> tokenizer, IStopwords<T> stopwords)
	{
		this.tokenizer = tokenizer;
		this.stopwords = stopwords;
	}

	@Override
	public boolean isTokenAStopword(T token)
	{
		return this.stopwords.isTokenAStopword(token);
	}

	@Override
	public List<T> tokenize(String text)
	{
		return tokenizer.tokenize(text);
	}
}
