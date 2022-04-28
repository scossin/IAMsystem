package fr.erias.IAMsystem.tokenizernormalizer;

import java.util.ArrayList;
import java.util.List;

import fr.erias.IAMsystem.stopwords.IStopwords;

/**
 * A class that tokenizes on white space and keeps only letter or digit
 * It performs the normalization and the tokenization at the same time
 * 
 * @author Cossin Sebastien
 *
 */

public class SimpleTokenizerNormalizer implements ITokenizerNormalizer {

	private final IStopwords stopwords;
	
	public SimpleTokenizerNormalizer() {
		this(IStopwords.noStopwords);
	}
	
	public SimpleTokenizerNormalizer(IStopwords stopwords) {
		this.stopwords = stopwords;
	}
	
	private boolean isACharacter2keep(char c) {
		return(java.lang.Character.isLetterOrDigit(c));
	}
	
	@Override
	public TNoutput tokenizeNormalize(String string) {
		TokenList tokenList = new TokenList();
		char[] out = new char[string.length()];
		String norm = java.text.Normalizer.normalize(string, java.text.Normalizer.Form.NFD);
		int j = 0;
		for (int i = 0, n = norm.length(); i < n; ++i) {
			char c = norm.charAt(i);
			int type = Character.getType(c);
			if (type != Character.NON_SPACING_MARK){
				if (isACharacter2keep(c)) {
					char cLower = java.lang.Character.toLowerCase(c);
					Token token = tokenList.getToken(j); // starts at j, not i (j!=i)
					token.addChar();
					out[j] = cLower;
				} else {
					tokenList.endOfToken();
					out[j] = ' '; // replace a character not to keep by blank
				}
				j++;
			}
		}
		String normString = new String(out);
		List<Token> tokens = tokenList.tokensList;
		String[] tokensArray = new String[tokens.size()];
		String[] tokensArrayOriginal = new String[tokens.size()]; 
		int[][] tokenStartEndInSentence = new int[tokensArray.length][2];
		for (int i = 0; i<tokens.size(); i++) {
			Token token = tokens.get(i);
			int tokenStart = token.start;
			int tokenEnd = token.start + token.length;
			String tokenNorm = normString.substring(tokenStart, tokenEnd);
			tokensArray[i] = tokenNorm;
			String tokenOrigin = string.substring(tokenStart, tokenEnd);
			tokensArrayOriginal[i] = tokenOrigin;
			int[] OneTokenStartEnd = {tokenStart,tokenEnd-1};
			tokenStartEndInSentence[i] = OneTokenStartEnd;
		}
		TNoutput tnoutput = new TNoutput(string, normString,
				tokensArray, tokensArrayOriginal,tokenStartEndInSentence);
		return tnoutput;
	}

	@Override
	public String[] tokenize(String normalizedSentence) {
		TNoutput tnoutput = tokenizeNormalize(normalizedSentence);
		return(tnoutput.getTokens());
	}

	@Override
	public String getNormalizedSentence(String sentence) {
		TNoutput tnoutput = tokenizeNormalize(sentence);
		return(tnoutput.getNormalizedSentence());
	}

	@Override
	public boolean isStopWord(String token) {
		return this.stopwords.isStopWord(token);
	}
}

class Token {
	public final int start;
	public int length = 0;
	
	public Token(int start) {
		this.start = start;
	}
	
	public void addChar() {
		this.length += 1;
	}
}

class TokenList {
	public final List<Token> tokensList = new ArrayList<Token>();
	
	private boolean add2lastToken = false;
	
	public Token getToken(int start) {
		if (add2lastToken) {
			return tokensList.get(tokensList.size() - 1);
		} else {
			Token token = new Token(start);
			tokensList.add(token);
			add2lastToken = true;
			return(token);
		}
	}
	
	public void endOfToken() {
		this.add2lastToken = false;
	}
}
