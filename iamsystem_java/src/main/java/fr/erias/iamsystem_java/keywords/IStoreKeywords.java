package fr.erias.iamsystem_java.keywords;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.ITokenizerStopwords;

/**
 * A class that stores keywords.
 *
 * @author Sebastien Cossin
 *
 */
public interface IStoreKeywords
{

	/**
	 * Return all the unigrams of a dictionary (collection of keywords).
	 *
	 * @param keywords  collections of {@link IKeyword}.
	 * @param tokenizer to tokenize and normalize keywords.
	 * @param stopwords to remove stopwords.
	 * @return all the unigrams of the dictionary except stopwords.
	 */
	public static Set<String> getUnigrams(Iterable<IKeyword> keywords, ITokenizer tokenizer, IStopwords stopwords)
	{
		Set<String> unigrams = new HashSet<String>();

		for (IKeyword kw : keywords)
		{
			ITokenizerStopwords.tokenizeRmStopwords(kw.label(), tokenizer, stopwords)
					.stream()
					.forEach(t -> unigrams.add(t.normLabel()));
		}
		return unigrams;
	}

	/**
	 * Get all the unigrams (single words excluding stopwords) in the keywords.
	 *
	 * @param keywords a collection of {@link IKeyword}.
	 * @param tokstop  a {@link ITokenizerStopwords} instance.
	 * @return all the unigrams of the dictionary except stopwords.
	 */
	public static Set<String> getUnigrams(Iterable<IKeyword> keywords, ITokenizerStopwords tokstop)
	{
		Set<String> unigrams = new HashSet<String>();

		for (IKeyword kw : keywords)
		{
			ITokenizerStopwords.tokenizeRmStopwords(kw.label(), tokstop)
					.stream()
					.forEach(t -> unigrams.add(t.normLabel()));
		}
		return unigrams;
	}

	/**
	 * Add a keyword to the store.
	 *
	 * @param keyword a {@link IKeyword}.
	 */
	public void addKeyword(IKeyword keyword);

	/**
	 * Get the stored keywords.
	 *
	 * @return A collection of {@link IKeyword}.
	 */
	public Collection<IKeyword> getKeywords();
}
