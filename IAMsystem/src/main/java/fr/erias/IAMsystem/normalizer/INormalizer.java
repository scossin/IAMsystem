package fr.erias.IAMsystem.normalizer;

import fr.erias.IAMsystem.stopwords.IStopwords;

public interface INormalizer extends IStopwords {

	public static final INormalizer noNormalizer = new NoNormalizer();
	
	/**
	 * Normalize a text/(e.g. a sentence)
	 * @param sentence a sentence to normalize
	 * @return a normalized a sentence
	 */
	public String getNormalizedSentence(String sentence);
}
