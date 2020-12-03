package fr.erias.IAMsystem.tokenizernormalizer;

import org.json.JSONArray;
import org.json.JSONObject;

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
	 * @return array of tokens
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
	 * get Tokenizer Normalizer status
	 * @return Return 200 if no error in the tokenizer/normalizer process
	 */
	public int getStatus() {
		return(this.status);
	}
	
	
	/**
	 * Set the status value (to indicate an error occurred for example)
	 * @param status value indicating if an error occurred (success = 200)
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * Get a JSON object representation
	 * @return a JSON representation
	 */
	public JSONObject getJSONobject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", getStatus());
		jsonObject.put("originalSentence", getOriginalSentence());
		jsonObject.put("normalizedSentence", getNormalizedSentence());
		JSONArray jsonArray = new JSONArray();
		for (String token : tokensArray) {
			jsonArray.put(token);
		}
		jsonObject.put("tokensArray", jsonArray);
		JSONArray jsonArrayOriginal = new JSONArray();
		for (String token : tokensArrayOriginal) {
			jsonArrayOriginal.put(token);
		}
		jsonObject.put("tokensArrayOriginal", jsonArrayOriginal);
		return(jsonObject);
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
