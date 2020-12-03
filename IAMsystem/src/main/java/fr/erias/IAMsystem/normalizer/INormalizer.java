package fr.erias.IAMsystem.normalizer;

import fr.erias.IAMsystem.stopwords.IStopwords;

public interface INormalizer {
	
	/**
	 * Normalize a text/(e.g. a sentence)
	 * @param sentence a sentence to normalize
	 * @return a normalized a sentence
	 */
	public String getNormalizedSentence(String sentence);
	
	/**
	 * Retrieve the stopwords
	 * @return the stopwords instance of the normalizer
	 */
	public IStopwords getStopwords();
}
