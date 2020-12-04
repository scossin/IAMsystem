package fr.erias.IAMsystem.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizer.Tokenizer;

public class SetTokenTreeTest {
	
	public static SetTokenTree getSetTokenTreeTest() {
		SetTokenTree setTokenTree = new SetTokenTree();
		// first term of the terminology
		String term = "avc sylvien droit";
		ITokenizer tokenizer = ITokenizer.getDefaultTokenizer();
		String[] tokensArray = tokenizer.tokenize(term);
		TokenTree tokenTree = new TokenTree(null,tokensArray,"I63");
		setTokenTree.addTokenTree(tokenTree);
		
		// second term of the terminology
		term = "avc hemorragique";
		tokensArray = tokenizer.tokenize(term);
		tokenTree = new TokenTree(null,tokensArray,"I61");
		setTokenTree.addTokenTree(tokenTree);
		
		// third term of the terminology
		term = "accident vasculaire cerebral embolique";
		tokensArray = tokenizer.tokenize(term);
		tokenTree = new TokenTree(null,tokensArray,"I63");
		setTokenTree.addTokenTree(tokenTree);
		return(setTokenTree);
	}
	
	@Test
    public void availableEntriesTest() {
		SetTokenTree setTokenTree = getSetTokenTreeTest();
		
		Set<String> availableTokens = setTokenTree.getAvailableTokens();
		
		// 2 tokens available : 
		assertEquals(availableTokens.size(), 2);
		
		// tokens are "avc" and "accident" : 
		assertTrue(availableTokens.contains("avc"));
		assertTrue(availableTokens.contains("accident"));
    }
	
	@Test
    public void getSetTokenTest() {
		SetTokenTree setTokenTree = getSetTokenTreeTest();
		
		// Set of TokenTree after "accident" : 
		SetTokenTree afterAccident = setTokenTree.getSetTokenTree("accident");
		// 2 tokens available : 
		assertEquals(afterAccident.getAvailableTokens().size(), 1);
		assertTrue(afterAccident.getAvailableTokens().contains("vasculaire"));
		
		// after avc :  
		SetTokenTree afterAVC = setTokenTree.getSetTokenTree("avc");
		// 2 tokens available : 
		assertEquals(afterAVC.getAvailableTokens().size(), 2);
		assertTrue(afterAVC.getAvailableTokens().contains("sylvien"));
		assertTrue(afterAVC.getAvailableTokens().contains("hemorragique"));
    }
	
	@Test
    public void getSetTokenArrayTest() {
		SetTokenTree setTokenTree = getSetTokenTreeTest();
		
		// one token that doesn't exist : setTokenTree is empty 
		String token = "accident vasculaire cerebral";
		SetTokenTree afterAVC = setTokenTree.getSetTokenTree(token);
		// 2 tokens available : 
		assertEquals(afterAVC.getAvailableTokens().size(), 0);
		assertFalse(setTokenTree.containToken(token));
		
		// it works with a string of array. going to hemorragique 
		String[] array1 = {"accident","vasculaire","cerebral"};
		afterAVC = setTokenTree.getSetTokenTree(array1);
		assertTrue(afterAVC.getAvailableTokens().contains("embolique"));
		
		// I want everything after "accident","vasculaire","cerebral" but this term has abbrevation avc
		// so I want everything after "avc" too :
		String[] array2 = {"avc"};
		HashSet<String[]> synonyms = new HashSet<String[]>();
		synonyms.add(array1);
		synonyms.add(array2);
		afterAVC = setTokenTree.getSetTokenTree(synonyms);
		// 3 entries after AVC : embolique, hemorragique and sylvien
		assertEquals(afterAVC.getAvailableTokens().size(), 3);
		assertTrue(afterAVC.getAvailableTokens().contains("embolique"));
		assertTrue(afterAVC.getAvailableTokens().contains("hemorragique"));
		assertTrue(afterAVC.getAvailableTokens().contains("sylvien"));
    }
	
	@Test
    public void getCodeTest() {
		SetTokenTree setTokenTree = getSetTokenTreeTest();
		String[] array1 = {"accident","vasculaire","cerebral","embolique"};
		
		// check it exists : 
		HashSet<String[]> synonyms = new HashSet<String[]>();
		synonyms.add(array1);
		assertTrue(setTokenTree.containTokenBi(synonyms));
		
		SetTokenTree afterAccident = setTokenTree.getSetTokenTree(array1);
		// no entry after this term (child is null after embolique) : 
		assertEquals(afterAccident.getAvailableTokens().size(), 0);
		
		// but we have one code ! 
		assertEquals(afterAccident.getPreviousTokenTrees().size(), 1);
		
		// and this code is I63
		assertEquals(afterAccident.getOneCode(), "I63");
    }
}
