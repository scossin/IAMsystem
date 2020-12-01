package fr.erias.IAMsystem.tokenizer;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.normalizer.NormalizerTerm;

/**
 * A class that tokenizes and normalizes a sentence
 *  * @author Cossin Sebastien
 *
 */
public class TokenizerNormalizer {

	final static Logger logger = LoggerFactory.getLogger(TokenizerNormalizer.class);

	/**
	 * A class to normalize a term
	 */
	private INormalizer normalizerTerm ;

	/**
	 * Array of token after tokenization and normalization
	 */
	private String[] tokensArray;

	/**
	 * Array of token without normalization
	 */
	private String[] tokensArrayOriginal;

	/**
	 * For each token, the start and the end offset in the sentence
	 */
	private int[][] tokenStartEndInSentence ;

	/**
	 * Default pattern : token with alphanumeric or numbers
	 */
	private static String pattern = "[0-9]+|[a-z]+";


	/**
	 * Constructor
	 * @param normalizerTerm A normalizer instance to normalize terms or sentences
	 */
	public TokenizerNormalizer(INormalizer normalizerTerm) {
		this.normalizerTerm = normalizerTerm;
	}

	/********************************* Tokenizers function ***********************************/

	/**
	 * Tokenize with the following pattern : "[0-9]+|[a-z]+"
	 * @param normalizedSentence A normalized term or sentence to tokenize
	 * @return An array of token
	 */
	public static String[] tokenizeAlphaNum(String normalizedSentence) {
		List<String> chunks = new LinkedList<String>();
		Pattern VALID_PATTERN = Pattern.compile(pattern);
		Matcher matcher = VALID_PATTERN.matcher(normalizedSentence);
		while (matcher.find()) {
			chunks.add( matcher.group() );
		}
		String[] tokenArray = new String[chunks.size()];
		tokenArray = chunks.toArray(tokenArray);
		return tokenArray;
	}

	/**
	 * Normalize and tokenize a sentence
	 * @param sentence A string to tokenize
	 */
	public void tokenize(String sentence) {
		normalizerTerm.setSentence(sentence);
		this.tokensArray = tokenizeAlphaNum(normalizerTerm.getNormalizedSentence());
		try {
			setTokensStartEndInSentence();
		} catch (UnfoundTokenInSentence e) {
			logger.info("Something went wrong during detecting start and end of each token");
			e.printStackTrace();
		}
	}

	/**
	 * Same as tokenize function without calculating start and end offset for each token
	 * @param sentence a String to tokenize
	 */
	public void tokenizeWithoutEndStart(String sentence) {
		normalizerTerm.setSentence(sentence);
		this.tokensArray = tokenizeAlphaNum(normalizerTerm.getNormalizedSentence());
	}


	/**
	 * Tokenize and normalize each token, return the normalized label
	 * @param label a label to normalize
	 * @return the normalized label
	 */
	public String normalizeLabel(String label) {
		// remove first and last quote
		label = label.replaceAll("^\"", "");
		label = label.replaceAll("\"$", "");
		// normalizedTerm :
		tokenizeWithoutEndStart(label);
		String normalizedTerm = getNormalizerTerm().getNormalizedSentence();
		if (normalizedTerm.equals("")) {
			normalizedTerm = "nothingRemains";
			logger.info(label + " \t is a stopword - nothing remains of this label");
		}
		return(normalizedTerm);
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
	 * Change the tokenizer pattern
	 * @param pattern A regular expression to tokenize a sentence
	 */
	public static void setPattern(String pattern) {
		TokenizerNormalizer.pattern = pattern;
	}

	public INormalizer getNormalizerTerm() {
		return(normalizerTerm);
	}

	/************************************* Getters ****************************************/
	/**
	 * 
	 * @return Array of tokens of the original sentence
	 */
	public String[] getTokensArrayOriginal() {
		return tokensArrayOriginal;
	}

	public String[] getTokens() {
		return tokensArray;
	}

	public int[][] getTokenStartEndInSentence() throws UnfoundTokenInSentence {
		return(tokenStartEndInSentence);
	}

	/**
	 * Calculate the start and end of each token in the sentence
	 * @throws UnfoundTokenInSentence If start or end of a token can't be found in the sentence
	 */
	private void setTokensStartEndInSentence() throws UnfoundTokenInSentence{
		String normalizedSentence = normalizerTerm.getNormalizedSentence();
		String originalSentence = normalizerTerm.getOriginalSentence();

		tokensArrayOriginal = new String[tokensArray.length];
		tokenStartEndInSentence = new int[tokensArray.length][2];

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
			tokensArrayOriginal[i] = originalSentence.substring(tokenStart, tokenEnd + 1); // + 1 because method stop at indexEnd - 1
			logger.debug("\t token original : " + tokensArrayOriginal[i]);
		}
	}
}
