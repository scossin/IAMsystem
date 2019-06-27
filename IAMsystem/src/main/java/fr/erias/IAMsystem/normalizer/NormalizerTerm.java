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
public class NormalizerTerm implements NormalizerInterface {

	final static Logger logger = LoggerFactory.getLogger(NormalizerTerm.class);
	
	/**
	 * Original term or sentence
	 */
	private String originalSentence;
	
	/**
	 * Term or sentence normalized 
	 */
	private String normalizedSentence;
	
	/**
	 * Stopwords to exclude during normalization
	 */
	private Stopwords stopwords ;
	
	/**
	 * Constructor 
	 * @param stopwords a class containing stopwords
	 */
	public NormalizerTerm(Stopwords stopwords) {
		this.stopwords = stopwords;
	}
	
	/********************************************* Getters ***************************************/
	
	/**
	 * 
	 * @return The sentence after normalization
	 */
	public String getNormalizedSentence() {
		return(this.normalizedSentence);
	}
	
	/**
	 * 
	 * @return The original sentence
	 */
	public String getOriginalSentence() {
		return(originalSentence);
	}
	
	/**
	 * 
	 * @return the stopwords instance of the normalizerTerm
	 */
	public Stopwords getStopwords() {
		return(stopwords);
	}
	
	@Override
	public boolean isStopWord(String token) {
		return stopwords.isStopWord(token);
	}
	
	/**
	 * Normalize sentence of term
	 * @param sentence a term or sentence to normalize
	 */
	public void setSentence(String sentence) {
		this.originalSentence = sentence;
		try {
			this.normalizedSentence = NormalizerInterface.normalizedSentence(sentence);
			checkUnchangedLength(this.normalizedSentence);
		} catch (InvalidSentenceLength e) {
			logger.info("Something went wrong during normalization");
			logger.info("sentence of " + sentence.length() + " : \n " + sentence);
			String sentenceWithoutAccents = NormalizerInterface.flattenToAscii(sentence);
			logger.info("sentence without accents :" + sentenceWithoutAccents.length() + " : \n " + sentenceWithoutAccents);
			String sentenceWithoutPuncutations = NormalizerInterface.removeSomePunctuation(sentenceWithoutAccents);
			logger.info("sentence without punctuation :" + sentenceWithoutPuncutations.length() + " : \n " + sentenceWithoutPuncutations);
			MyExceptions.logException(logger, e);
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Check if characters removal didn't change the sentence length
	 * @param modifiedSentence a modified sentence
	 * @throws InvalidSentenceLength if the length of the modified sentence is unexpected
	 */
	private void checkUnchangedLength(String modifiedSentence) throws InvalidSentenceLength {
		if (originalSentence.length() != modifiedSentence.length()) {
			String msg = "Original length " + originalSentence.length() + " : " + originalSentence + "\n" +
		"Modified length " + modifiedSentence.length() + modifiedSentence;
			throw new InvalidSentenceLength(logger, msg);
		}
		return;
	}

	@Override
	public void addStopwords(String token) {
		this.stopwords.addStopwords(token);
	}
}
