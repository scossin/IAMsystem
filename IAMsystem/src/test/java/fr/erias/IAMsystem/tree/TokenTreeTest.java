package fr.erias.IAMsystem.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizer.Tokenizer;

public class TokenTreeTest {
    
	@Test
    public void tokenTreeTest() {
		String term = "avc sylvien droit";
		ITokenizer tokenizer = ITokenizer.getDefaultTokenizer();
		String[] tokensArray = tokenizer.tokenize(term);
		TokenTree tokenTree = new TokenTree(null,tokensArray,"I63");
		
		// first token is "avc" : 
		String tokenAVC = tokenTree.getToken();
		assertEquals(tokenAVC, "avc");
		
		// first child after "avc" is "sylvien"
		String tokenSylvien = tokenTree.getTokenTreeChild().getToken();
		assertEquals(tokenSylvien, "sylvien");
		
		// sylvien has a child so no code is available : 
		String code = tokenTree.getTokenTreeChild().getCode();
		assertTrue(code == null);
		
		// the last token has the code
		code = tokenTree.getTokenTreeChild().getTokenTreeChild().getCode();
		assertTrue(code.equals("I63"));
		
		// the depth of the last child is 2
		int depth = tokenTree.getTokenTreeChild().getTokenTreeChild().getDepth();
		assertEquals(depth, 2);
    }
	
	@Test
    public void previousCodeTest() {
		String term = "avc sylvien droit";
		ITokenizer tokenizer = ITokenizer.getDefaultTokenizer();
		String[] tokensArray = tokenizer.tokenize(term);
		TokenTree tokenTree = new TokenTree(null,tokensArray,"I63");
		
		// the last token has the code
		tokenTree = tokenTree.getTokenTreeChild().getTokenTreeChild();
		String[] tokens = tokenTree.getCurrentAndPreviousTokens();
		assertTrue(Arrays.equals(tokens, tokensArray));
		
		tokens = tokenTree.getPreviousTokens();
		assertTrue(tokens[0].equals("avc"));
		assertTrue(tokens[1].equals("sylvien"));
		
		// with only one term : 
		term = "avc";
		tokensArray = tokenizer.tokenize(term);
		tokenTree = new TokenTree(null,tokensArray,"I63");
		tokens = tokenTree.getCurrentAndPreviousTokens();
		assertTrue(Arrays.equals(tokens, tokensArray));
		
		// no previous token
		tokens = tokenTree.getPreviousTokens();
		assertEquals(tokens.length, 0);
    }
}
