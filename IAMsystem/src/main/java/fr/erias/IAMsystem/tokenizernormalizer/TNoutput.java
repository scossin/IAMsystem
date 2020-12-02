package fr.erias.IAMsystem.tokenizernormalizer;

public class TNoutput {
	
	/**
	 * Return 200 if no error in the tokenizer/normalizer process
	 */
	private int status = 200;
	
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
	 * Get the array of tokens containing unormalized words
	 * @return Array of tokens of the original sentence
	 */
	public String[] getTokensArrayOriginal() {
		return tokensArrayOriginal;
	}

	/**
	 * Get the array of tokens containing normalized words
	 * @return
	 */
	public String[] getTokens() {
		return tokensArray;
	}

	/**
	 * Get the start and end of each token in the sentence
	 * @return An array (length number of tokens) of array (TokenStartPosition in sentence and TokenEndPosition in sentence)
	 */
	public int[][] getTokenStartEndInSentence() {
		return(tokenStartEndInSentence);
	}
	
	
	/**
	 * Return 200 if no error in the tokenizer/normalizer process
	 * @return
	 */
	public int getStatus() {
		return(this.status);
	}
	
	
	/**
	 * Set the status value (to indicate an error occured for example)
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * 
	 * @return a TNoutput object containing an error message
	 */
	public static TNoutput getErrorTNoutput() {
		String normalizedSentence = "an error occured";
		String originalSentence = normalizedSentence;
		String[] tokens = {"an","error","occured"};
		String[] tokensOriginal = tokens;
		int [][] tokensStartEnd = {{0,1},{2,6},{7,13}};
		TNoutput tnoutpout = new TNoutput(originalSentence,normalizedSentence,tokens,tokensOriginal,tokensStartEnd);
		tnoutpout.setStatus(500);
		return(tnoutpout);
	}
}
