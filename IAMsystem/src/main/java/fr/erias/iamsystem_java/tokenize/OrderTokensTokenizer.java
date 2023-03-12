package fr.erias.iamsystem_java.tokenize;

import java.util.Comparator;
import java.util.List;

/**
 * A Tokenizer decorator that order tokens alphabetically.
 *
 * @author Sebastien Cossin
 *
 */
public class OrderTokensTokenizer implements ITokenizer
{

	private ITokenizer tokenizer;

	/**
	 * Decorate a {@link ITokenizer} the tokens are sorted alphabetically by their
	 * label.
	 *
	 * @param tokenizer the {@link ITokenizer} that does the tokenization.
	 */
	public OrderTokensTokenizer(ITokenizer tokenizer)
	{
		this.tokenizer = tokenizer;
	}

	/**
	 * the {@link ITokenizer} that does the tokenization.
	 *
	 * @return the inner {@link ITokenizer}.
	 */
	public ITokenizer getInnerTokenizer()
	{
		return this.tokenizer;
	}

	@Override
	public List<IToken> tokenize(String text)
	{
		List<IToken> tokens = this.tokenizer.tokenize(text);
		tokens.sort(new Comparator<IToken>()
		{

			@Override
			public int compare(IToken o1, IToken o2)
			{
				return o1.normLabel().compareTo(o2.normLabel());
			}
		});
		return tokens;
	}
}
