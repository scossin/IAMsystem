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
public class TokStopImp extends AbstractTokNorm
{

	private final ITokenizer tokenizer;
	private final IStopwords stopwords;

	public TokStopImp(ITokenizer tokenizer, IStopwords stopwords)
	{
		this.tokenizer = tokenizer;
		this.stopwords = stopwords;
	}

	@Override
	public boolean isTokenAStopword(IToken token)
	{
		return this.stopwords.isTokenAStopword(token);
	}

	@Override
	public List<IToken> tokenize(String text)
	{
		return tokenizer.tokenize(text);
	}
}
