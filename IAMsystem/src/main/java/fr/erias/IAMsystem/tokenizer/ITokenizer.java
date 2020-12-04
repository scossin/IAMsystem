package fr.erias.IAMsystem.tokenizer;

public interface ITokenizer {

	/**
	 * Tokenize a string into chunks
	 * @param normalizedSentence A string to tokenize (e.g. already normalized)
	 * @return an Array of tokens
	 */
	public String[] tokenize(String normalizedSentence);
	
	/**
	 * Transform an array of String to a String
	 * @param anArray A string array
	 * @param sep A separator
	 * @return Concatenated String
	 */
	public static String arrayToString(String[] anArray, char sep){
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < anArray.length; i++) {
		  if (i != 0) {
			  strBuilder.append(sep);
		  }
		   strBuilder.append(anArray[i]);
		}
		String newString = strBuilder.toString();
		return(newString);
	}
	
	/**
	 * Retrieve the default tokenizer
	 * @return a default {@link ITokenizer}
	 */
	public static ITokenizer getDefaultTokenizer() {
		return new TokenizerWhiteSpace();
	}
}
