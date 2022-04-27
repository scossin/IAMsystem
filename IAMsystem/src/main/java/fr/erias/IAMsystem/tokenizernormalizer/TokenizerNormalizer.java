package fr.erias.IAMsystem.tokenizernormalizer;

import fr.erias.IAMsystem.exceptions.InvalidSentenceLength;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.normalizer.Normalizer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.tokenizer.ITokenizer;

/**
 * A class that tokenizes and normalizes a sentence
 * Warning: you can instanciate this class with a normalizer and a tokenizer 
 * but you can't choose the order (normalize first, tokenize after or the reverse). 
 * 
 * @author Cossin Sebastien
 *
 */
public class TokenizerNormalizer implements ITokenizerNormalizer {

	/**
	 * A class to normalize a term
	 */
	private INormalizer normalizer ;
	
	/**
	 * A class to tokenize
	 */
	private ITokenizer tokenizer ;

	/**
	 * Constructor
	 * @param normalizer A normalizer instance to normalize terms or sentences
	 * @param tokenizer A tokenizer to tokenize terms or sentences
	 */
	public TokenizerNormalizer(INormalizer normalizer, ITokenizer tokenizer) {
		this.normalizer = normalizer;
		this.tokenizer = tokenizer;
	}
	
	/**
	 * Get a default {@link TokenizerNormalizer} 
	 * @param stopwords an instance of {@link IStopwords}
	 * @return The tokenizerNormalizer
	 */
	public static TokenizerNormalizer getDefaultTokenizerNormalizer(IStopwords stopwords){
		ITokenizer tokenizer = ITokenizer.getDefaultTokenizer();
		Normalizer normalizerTerm = new Normalizer(stopwords);
		TokenizerNormalizer tokenizerNormalizer = new TokenizerNormalizer(normalizerTerm, tokenizer);
		return(tokenizerNormalizer);
	}
	
	/**
	 * Get a default {@link TokenizerNormalizer} 
	 * @return The tokenizerNormalizer
	 */
	public static TokenizerNormalizer getDefaultTokenizerNormalizer(){
		return(getDefaultTokenizerNormalizer(IStopwords.noStopwords));
	}
	
	/**
	 * Normalize and tokenize a sentence
	 * The normalization doesn't change the sentence length
	 * @param sentence a term or sentence to normalize
	 */
	public TNoutput tokenizeNormalize(String sentence) {
		String originalSentence = sentence;
		String normalizedSentence = this.normalizer.getNormalizedSentence(sentence);
		// check it doesn't change the sentence length
		try {
			this.checkUnchangedLength(originalSentence, normalizedSentence);
		} catch (InvalidSentenceLength e) {
			StringBuilder sb = new StringBuilder();
			sb.append("Something went wrong during normalization");
			sb.append("\t original sentence:" + originalSentence);
			sb.append("\t\toriginal sentence length: " + sentence.length());
			sb.append("\t normalizedSentence:" + normalizedSentence);
			sb.append("\t\tnormalizedSentence length: " + normalizedSentence.length());
			sb.append("sentence length: " + sentence.length() + " : \n " + sentence);
			System.err.println(sb.toString());
			e.printStackTrace();
			// we recover from this error by assigning the normalized form to the original sentence:
			originalSentence = normalizedSentence;
		}
		
		// tokenize and setTokensStartandEnd
		String[] tokensArray = this.tokenizer.tokenize(normalizedSentence);
		int[][] tokenStartEndInSentence = null;
		try {
			tokenStartEndInSentence = this.getTokensStartEndInSentence(
					normalizedSentence, 
					tokensArray
					);
		} catch (UnfoundTokenInSentence e) {
			e.printStackTrace();
			return(TNoutput.getErrorTNoutput());
		}
		String[] tokensArrayOriginal = this.getTokensArrayOriginal(tokensArray, originalSentence, tokenStartEndInSentence);
		TNoutput tnoutput = new TNoutput(originalSentence, normalizedSentence, tokensArray, tokensArrayOriginal, tokenStartEndInSentence);
		return(tnoutput);
	}
	
	/********************************* Tokenizers function ***********************************/

	/**
	 * Same as tokenize function without calculating start and end offset for each token
	 * @param sentence a String to tokenize
	 * @return an array of tokens
	 */
	public String[] tokenizeWithoutEndStart(String sentence) {
		String normalizedSentence = normalizer.getNormalizedSentence(sentence);
		String[] tokensArray = this.tokenizer.tokenize(normalizedSentence);
		return(tokensArray);
	}
	
	/**
	 * 
	 * @param tokensArray The array of tokens containing normalized words
	 * @param originalSentence The unormalized sentence
	 * @param tokenStartEndInSentence the start and end of each token in the sentence
	 * @return the array of tokens containing unormalized words
	 */
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
			throw new InvalidSentenceLength(msg);
		}
		return;
	}


	/************************************* Setters / Getters ****************************************/

	/**
	 * Change the normalizer
	 * @param normalizer {@link INormalizer } set a new normalizerTerm instance
	 */
	public void setNormalizer(INormalizer normalizer) {
		this.normalizer = normalizer;
	}


	/**
	 * Change the tokenizer
	 * @param tokenizer {@link ITokenizer } set a new normalizerTerm instance
	 */
	public void setTokenizer(ITokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
	
	/**
	 * Get the normalizer
	 * @return {@link INormalizer}
	 */
	public INormalizer getNormalizer() {
		return(normalizer);
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
	 * @param normalizedSentence the normalized sentence by the {@link INormalizer}
	 * @param tokensArray normalize array of tokens from {@link ITokenizer}
	 * @return An array (length number of tokens) of array (TokenStartPosition in sentence and TokenEndPosition in sentence)
	 * @throws UnfoundTokenInSentence If start or end of a token can't be found in the sentence
	 */
	private int[][] getTokensStartEndInSentence(String normalizedSentence, String[] tokensArray) throws UnfoundTokenInSentence {
		int[][] tokenStartEndInSentence = new int[tokensArray.length][2];
		int sentenceLength = normalizedSentence.length();
		int sentencePosition = 0;
		int tokenStart = 0 ;
		int tokenEnd = 0;
		char[] sentenceCharArray = normalizedSentence.toCharArray();

		for (int i = 0 ; i<tokensArray.length ; i++){ // for each token
			String token = tokensArray[i]; 
			char[] tokenCharArray = token.toCharArray();

			for (int y = 0 ; y<tokenCharArray.length ; y++){ // for each char of the token
				char tokenChar = tokenCharArray[y];
				char sentenceChar = sentenceCharArray[sentencePosition]; // first token char = first sentence char ?

				while (tokenChar != sentenceChar){ // it's not true in case of whites space => go to the next char of the sentence
					sentencePosition ++ ;
					if (sentencePosition > sentenceLength){
						throw new UnfoundTokenInSentence(token, normalizedSentence);
					}
					sentenceChar = sentenceCharArray[sentencePosition]; // next sentence char
				}
				if (y == 0){ // First char the token => start position
					tokenStart = sentencePosition ;
				}
				if (y == tokenCharArray.length - 1){ // Last char of the token => end position
					tokenEnd = sentencePosition ;
				}
				sentencePosition = sentencePosition + 1 ; // don't stay at the end of the previous token : move to the next char
			}
			int[] OneTokenStartEnd = {tokenStart,tokenEnd};
			tokenStartEndInSentence[i] = OneTokenStartEnd;
		}
		return(tokenStartEndInSentence);
	}

	@Override
	public String[] tokenize(String normalizedSentence) {
		return this.getTokenizer().tokenize(normalizedSentence);
	}

	@Override
	public String getNormalizedSentence(String sentence) {
		return this.getNormalizer().getNormalizedSentence(sentence);
	}

	@Override
	public boolean isStopWord(String token) {
		return this.normalizer.isStopWord(token);
	}
}
