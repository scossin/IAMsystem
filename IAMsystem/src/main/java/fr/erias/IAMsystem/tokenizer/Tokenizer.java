package fr.erias.IAMsystem.tokenizer;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenize by extracting pattern with a regular expression
 * 
 * @author Cossin Sebastien
 *
 */
public class Tokenizer implements ITokenizer {

	/**
	 * Default pattern : token with alphanumeric or numbers
	 */
	private String pattern = "[a-z]+|[0-9]+";
	
	/**
	 * 
	 */
	private Pattern VALID_PATTERN; 
	
	/**
	 * Create a new Tokenizer
	 */
	public Tokenizer() {
		this.setPattern(this.pattern);
	}
	
	/**
	 * Tokenize when a regular expression pattern is found (default) : "[a-z]+|[0-9]+"
	 * @param normalizedSentence A normalized term or sentence to tokenize
	 * @return An array of token
	 */
	public String[] tokenizeAlphaNum(String normalizedSentence) {
		List<String> chunks = new LinkedList<String>();
		Matcher matcher = this.VALID_PATTERN.matcher(normalizedSentence);
		while (matcher.find()) {
			chunks.add( matcher.group());
		}
		String[] tokenArray = new String[chunks.size()];
		tokenArray = chunks.toArray(tokenArray);
		return tokenArray;
	}
	
	/**
	 * Change the tokenizer pattern - default "[a-z]+|[0-9]+";
	 * @param pattern A regular expression to tokenize a sentence
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
		this.VALID_PATTERN = Pattern.compile(pattern);
	}

	@Override
	public String[] tokenize(String normalizedSentence) {
		return tokenizeAlphaNum(normalizedSentence);
	}
}
