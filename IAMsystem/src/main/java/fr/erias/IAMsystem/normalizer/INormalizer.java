package fr.erias.IAMsystem.normalizer;

import fr.erias.IAMsystem.stopwords.IStopwords;

public interface INormalizer {
	
	/**
	 * @return Normalize a sentence
	 */
	public String getNormalizedSentence(String sentence);
	
	/**
	 * @return the stopwords instance of the normalizerTerm
	 */
	public IStopwords getStopwords();
	
	
}
