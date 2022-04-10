package fr.erias.IAMsystem.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import fr.erias.IAMsystem.detect.DetectionBackwardTest;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.tokenizer.ITokenizer;

@Deprecated
public class TokenTreeTest {
    
	@Test
    public void tokenTreeTest() {
		TokenTree tokenTree = DetectionBackwardTest.getTokenTree("avc sylvien droit", "I63");
		// first token is "avc" : 
		String tokenAVC = tokenTree.getToken();
		assertEquals(tokenAVC, "avc");
		
		// first child after "avc" is "sylvien"
		String tokenSylvien = tokenTree.getTokenTreeChild().getToken();
		assertEquals(tokenSylvien, "sylvien");
		
		// sylvien has a child so no code is available : 
		Term term = tokenTree.getTokenTreeChild().getTerm();
		assertTrue(term == null);
		
		// the last token has the code
		String code = tokenTree.getTokenTreeChild().getTokenTreeChild().getTerm().getCode();
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
		TokenTree tokenTree = new TokenTree(null,tokensArray,new Term(term,"I63"));
		
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
		tokenTree = new TokenTree(null,tokensArray,new Term(term,"I63"));
		tokens = tokenTree.getCurrentAndPreviousTokens();
		assertTrue(Arrays.equals(tokens, tokensArray));
		
		// no previous token
		tokens = tokenTree.getPreviousTokens();
		assertEquals(tokens.length, 0);
    }
}
