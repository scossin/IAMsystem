package fr.erias.IAMsystem.normalizer;

import fr.erias.IAMsystem.stopwords.IStopwords;

public class NoNormalizer implements INormalizer {

	private final IStopwords stopwords = IStopwords.noStopwords;
	
	@Override
	public String getNormalizedSentence(String sentence) {
		return sentence;
	}

	@Override
	public boolean isStopWord(String token) {
		return stopwords.isStopWord(token);
	}
}
