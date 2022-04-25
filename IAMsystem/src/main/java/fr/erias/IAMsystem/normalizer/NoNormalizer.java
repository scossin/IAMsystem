package fr.erias.IAMsystem.normalizer;

import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;

public class NoNormalizer implements INormalizer {

	private final IStopwords stopwords = new StopwordsImpl();
	@Override
	public String getNormalizedSentence(String sentence) {
		return sentence;
	}

	@Override
	public IStopwords getStopwords() {
		return stopwords;
	}

	@Override
	public void setStopwords(IStopwords stopwords) {
		
	}
}
