package fr.erias.iamsystem_java.tokenize;

import java.util.Comparator;
import java.util.List;

public class OrderTokensTokenizer implements ITokenizer
{

	private ITokenizer tokenizer;
	public OrderTokensTokenizer(ITokenizer tokenizer) {
		this.tokenizer = tokenizer;
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
	
	public ITokenizer getInnerTokenizer() {
		return this.tokenizer;
	}
}
