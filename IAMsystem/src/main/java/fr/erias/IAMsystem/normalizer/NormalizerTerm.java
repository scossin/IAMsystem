package fr.erias.IAMsystem.normalizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.exceptions.InvalidSentenceLength;
import fr.erias.IAMsystem.exceptions.MyExceptions;

/**
 * Class to normalize terms and sentences
 * @author Cossin Sebastien
 *
 */
public class NormalizerTerm implements INormalizer {

	final static Logger logger = LoggerFactory.getLogger(NormalizerTerm.class);
	
	/**
	 * Stopwords to exclude during normalization
	 */
	private IStopwords stopwords ;
	
	/**
	 * Constructor 
	 * @param stopwords a class containing stopwords
	 */
	public NormalizerTerm(IStopwords stopwords) {
		this.stopwords = stopwords;
	}
	
	/********************************************* Getters ***************************************/
	
	/**
	 * 
	 * @return the stopwords instance of the normalizerTerm
	 */
	public IStopwords getStopwords() {
		return(stopwords);
	}
	
	/**
	 * 
	 * @param token Check if this token is a stopword
	 * @return true if the token is a stopword
	 */
	public boolean isStopWord(String token) {
		return stopwords.isStopWord(token);
	}
	
	/**
	 * Add a token to the list of stopword
	 * @param token a token to add
	 */
	public void addStopwords(String token) {
		this.stopwords.addStopwords(token);
	}

	@Override
	public String getNormalizedSentence(String sentence) {
		return (INormalizer.normalizedSentence(sentence));
	}
}
