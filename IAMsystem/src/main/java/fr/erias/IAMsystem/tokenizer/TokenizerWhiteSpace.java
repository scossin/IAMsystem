package fr.erias.IAMsystem.tokenizer;

/**
 * Tokenize on white space
 * 
 * @author Cossin Sebastien
 *
 */
public class TokenizerWhiteSpace implements ITokenizer {

	/**
	 * split on white space
	 */
	private final String split = "\\s+";
	
	/**
	 * Constructor
	 */
	public TokenizerWhiteSpace() {
		
	}

	@Override
	public String[] tokenize(String normalizedSentence) {
		return normalizedSentence.split(split);
	}
}
