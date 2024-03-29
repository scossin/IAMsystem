package fr.erias.iamsystem.stopwords;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.erias.iamsystem.tokenize.IToken;

/**
 * Like a negative image (a total inversion, in which light areas appear dark
 * and vice versa), every token is a stopword until proven otherwise.
 *
 * @author Sebastien Cossin
 */

public class NegativeStopwords implements IStopwords
{

	private Set<String> words2keep = new HashSet<String>();
	private List<IWord2keepF> funs = new ArrayList<IWord2keepF>();

	public NegativeStopwords()
	{
	}

	/**
	 * Constructor with words2keep.
	 *
	 * @param words2keep a Collection of words to keep.
	 */
	public NegativeStopwords(Collection<String> words2keep)
	{
		this.add(words2keep);
	}

	/**
	 * Add words to keep.
	 *
	 * @param words A collection of words.
	 */
	public void add(Collection<String> words)
	{
		words.forEach((w) -> this.words2keep.add(w));
	}

	/**
	 * Add a function that checks if a word should be kept.
	 *
	 * @param word2keepFunction a function that check if a word should be kept.
	 */
	public void add(IWord2keepF word2keepFunction)
	{
		this.funs.add(word2keepFunction);
	}

	/**
	 * Check if a token is not a stopword.
	 *
	 * @param token a token to check.
	 * @return False if it's not known (so it's a stopword).
	 */
	public boolean isAtoken2keep(IToken token)
	{
		return words2keep.contains(token.normLabel()) || funs.stream().anyMatch(f -> f.isAword2keep(token));
	}

	@Override
	public boolean isTokenAStopword(IToken token)
	{
		return !this.isAtoken2keep(token);
	}
}
