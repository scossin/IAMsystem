package fr.erias.IAMsystem.tokenizer;

public interface ITokenizer {

	/**
	 * Tokenize a string into chunks
	 * @param normalizedSentence A string to tokenize (e.g. already normalized)
	 * @return an Array of tokens
	 */
	public String[] tokenize(String normalizedSentence);
	
}
