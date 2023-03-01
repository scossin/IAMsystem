package fr.erias.iamsystem_java.tokenize;

import java.util.List;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.stopwords.IStopwords;

public interface ITokenizerStopwords extends ITokenizer, IStopwords
{

	public static List<IToken> tokenizeRmStopwords(String text, ITokenizer tokenizer, IStopwords stopwords)
	{
		return tokenizer.tokenize(text)
				.stream()
				.filter(t -> !stopwords.isTokenAStopword(t))
				.collect(Collectors.toList());
	}

	public static List<IToken> tokenizeRmStopwords(String text, ITokenizerStopwords tokstop)
	{
		return tokstop.tokenize(text).stream().filter(t -> !tokstop.isTokenAStopword(t)).collect(Collectors.toList());
	}
}
