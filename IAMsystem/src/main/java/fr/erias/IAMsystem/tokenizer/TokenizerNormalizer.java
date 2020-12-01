package fr.erias.IAMsystem.tokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.exceptions.InvalidSentenceLength;
import fr.erias.IAMsystem.exceptions.MyExceptions;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.normalizer.NormalizerTerm;

/**
 * A class that tokenizes and normalizes a sentence
 * @author Cossin Sebastien
 *
 */
public class TokenizerNormalizer {

	final static Logger logger = LoggerFactory.getLogger(TokenizerNormalizer.class);

	/**
	 * A class to normalize a term
	 */
	private INormalizer normalizerTerm ;
	
	/**
	 * A class to tokenize
	 */
	private ITokenizer tokenizer ;

	
	/***************************** GETTERS ********************************/
	
	/**
	 * Constructor
	 * @param normalizerTerm A normalizer instance to normalize terms or sentences
	 */
	public TokenizerNormalizer(INormalizer normalizerTerm, ITokenizer tokenizer) {
		this.normalizerTerm = normalizerTerm;
		this.tokenizer = tokenizer;
	}
	
	/**
	 * Normalize and tokenize a sentence
	 * The normalization doesn't change the sentence length
	 * @param sentence a term or sentence to normalize
	 */
	public TNoutput tokenizeNormalize(String sentence) {
		String originalSentence = sentence;
		String normalizedSentence = this.normalizerTerm.getNormalizedSentence(sentence);
		// check it doesn't change the sentence length
		try {
			this.checkUnchangedLength(originalSentence, normalizedSentence);
		} catch (InvalidSentenceLength e) {
			logger.info("Something went wrong during normalization");
			logger.info("sentence of " + sentence.length() + " : \n " + sentence);
			String sentenceWithoutAccents = INormalizer.flattenToAscii(sentence);
			logger.info("sentence without accents :" + sentenceWithoutAccents.length() + " : \n " + sentenceWithoutAccents);
			String sentenceWithoutPuncutations = INormalizer.removeSomePunctuation(sentenceWithoutAccents);
			logger.info("sentence without punctuation :" + sentenceWithoutPuncutations.length() + " : \n " + sentenceWithoutPuncutations);
			MyExceptions.logException(logger, e);
			e.printStackTrace();
		}
		
		// tokenize and setTokensStartandEnd
		String[] tokensArray = this.tokenizer.tokenize(normalizedSentence);
		int[][] tokenStartEndInSentence = null;
		try {
			tokenStartEndInSentence = this.getTokensStartEndInSentence(
					originalSentence,
					normalizedSentence, 
					tokensArray
					);
		} catch (UnfoundTokenInSentence e) {
			logger.info("Something went wrong during detecting start and end of each token");
			e.printStackTrace();
		}
		String[] tokensArrayOriginal = this.getTokensArrayOriginal(tokensArray, originalSentence, tokenStartEndInSentence);
		TNoutput tnoutput = new TNoutput(originalSentence, normalizedSentence, tokensArray, tokensArrayOriginal, tokenStartEndInSentence);
		return(tnoutput);
	}
	
	private String[] getTokensArrayOriginal(String[] tokensArray, String originalSentence, int[][] tokenStartEndInSentence) {
		String [] tokensArrayOriginal = new String[tokensArray.length];
		for (int i = 0; i<tokenStartEndInSentence.length;i++) {
			int[] OneTokenStartEnd = tokenStartEndInSentence[i];
			int tokenStart = OneTokenStartEnd[0];
			int tokenEnd = OneTokenStartEnd[1];
			tokensArrayOriginal[i] = originalSentence.substring(tokenStart, tokenEnd + 1);
		}
		return(tokensArrayOriginal);
	}
	
	/**
	 * Check if characters removal didn't change the sentence length
	 * @param modifiedSentence a modified sentence
	 * @throws InvalidSentenceLength if the length of the modified sentence is unexpected
	 */
	private void checkUnchangedLength(String originalSentence, String modifiedSentence) throws InvalidSentenceLength {
		if (originalSentence.length() != modifiedSentence.length()) {
			String msg = "Original length " + originalSentence.length() + " : " + originalSentence + "\n" +
		"Modified length " + modifiedSentence.length() + modifiedSentence;
			throw new InvalidSentenceLength(logger, msg);
		}
		return;
	}

	/********************************* Tokenizers function ***********************************/

	/**
	 * Same as tokenize function without calculating start and end offset for each token
	 * @param sentence a String to tokenize
	 */
	public String[] tokenizeWithoutEndStart(String sentence) {
		String normalizedSentence = normalizerTerm.getNormalizedSentence(sentence);
		String[] tokensArray = this.tokenizer.tokenize(normalizedSentence);
		return(tokensArray);
	}

	/************************************* Setters ****************************************/

	/**
	 * Change the normalizer
	 * @param normalizerTerm {@link NormalizerTerm } set a new normalizerTerm instance
	 */
	public void setNormalizer(NormalizerTerm normalizerTerm) {
		this.normalizerTerm = normalizerTerm;
	}

	/**
	 * Get the normalizer
	 * @return {@link INormalizer}
	 */
	public INormalizer getNormalizerTerm() {
		return(normalizerTerm);
	}
	
	/**
	 * Get the tokenizer
	 * @return {@link ITokenizer }
	 */
	public ITokenizer getTokenizer() {
		return(tokenizer);
	}

	/**
	 * Calculate the start and end of each token in the sentence
	 * @throws UnfoundTokenInSentence If start or end of a token can't be found in the sentence
	 */
	private int[][] getTokensStartEndInSentence(
			String originalSentence,
			String normalizedSentence,
			String[] tokensArray
			) throws UnfoundTokenInSentence {
		//this.tokensArrayOriginal = new String[tokensArray.length];
		int[][] tokenStartEndInSentence = new int[tokensArray.length][2];

		int sentenceLength = normalizedSentence.length();
		int sentencePosition = 0;
		int tokenStart = 0 ;
		int tokenEnd = 0;
		char[] sentenceCharArray = normalizedSentence.toCharArray();

		for (int i = 0 ; i<tokensArray.length ; i++){ // for each token
			String token = tokensArray[i]; 
			logger.debug("Current Token to found in sentence : " + token);
			char[] tokenCharArray = token.toCharArray();

			for (int y = 0 ; y<tokenCharArray.length ; y++){ // for each char of the token
				char tokenChar = tokenCharArray[y];
				char sentenceChar = sentenceCharArray[sentencePosition]; // first token char = first sentence char ?

				logger.debug("\t Comparing" + tokenChar + " and " + sentenceChar);

				while (tokenChar != sentenceChar){ // it's not true in case of whites space => go to the next char of the sentence
					sentencePosition ++ ;
					if (sentencePosition > sentenceLength){
						throw new UnfoundTokenInSentence(logger, token, normalizedSentence);
					}
					sentenceChar = sentenceCharArray[sentencePosition]; // next sentence char
					logger.debug("\t advancing to next char");
					logger.debug("\t Comparing now" + tokenChar + " and " + sentenceChar);
				}
				if (y == 0){ // First char the token => start position
					tokenStart = sentencePosition ;
				}
				if (y == tokenCharArray.length - 1){ // Last char of the token => end position
					tokenEnd = sentencePosition ;
				}
				logger.debug("\t char found at position" +  Integer.toString(sentencePosition)); // this info is not recorded ; only tokenStart and end matters
				sentencePosition = sentencePosition + 1 ; // don't stay at the end of the previous token : move to the next char
			}
			logger.debug("\t token found at positions " +  Integer.toString(tokenStart) + " - " + Integer.toString(tokenEnd)); 
			int[] OneTokenStartEnd = {tokenStart,tokenEnd};
			tokenStartEndInSentence[i] = OneTokenStartEnd;

			// Token Original Form : 
			//tokensArrayOriginal[i] = originalSentence.substring(tokenStart, tokenEnd + 1); // + 1 because method stop at indexEnd - 1
			//logger.debug("\t token original : " + tokensArrayOriginal[i]);
		}
		return(tokenStartEndInSentence);
	}
}
