package fr.erias.IAMsystem.tokenizernormalizer;

import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;

public class TNoutput {
	/**
	 * Original term or sentence
	 */
	private String originalSentence;
	
	/**
	 * Term or sentence normalized 
	 */
	private String normalizedSentence;
	
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
	 * Tokenizer Normalizer output
	 * @param originalSentence Original term or sentence
	 * @param normalizedSentence  Term or sentence normalized 
	 * @param tokensArray Array of token after tokenization and normalization
	 * @param tokensArrayOriginal Array of token without normalization
	 * @param tokenStartEndInSentence For each token, the start and the end offset in the sentence
	 */
	public TNoutput(String originalSentence, String normalizedSentence,
			String[] tokensArray, String[] tokensArrayOriginal, int[][] tokenStartEndInSentence) {
		this.originalSentence = originalSentence;
		this.normalizedSentence = normalizedSentence;
		this.tokensArray = tokensArray;
		this.tokensArrayOriginal = tokensArrayOriginal;
		this.tokenStartEndInSentence = tokenStartEndInSentence;
	}
	
	
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
}
