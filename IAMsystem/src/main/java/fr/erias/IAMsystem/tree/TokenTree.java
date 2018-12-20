package fr.erias.IAMsystem.tree;

import java.util.Arrays;

/**
 * A terminology contains terms that are stored in a tree datastructure {@link SetTokenTree} <br>
 * For example, for these 2 terms : "avc sylvien droit" and "avc sylvien gauche" ; each one is first tokenized : "avc" "sylvien" "droit" and "gauche"  <br> 
 * The first token "avc" is the same ; the second "sylvien" is the same ; the third is not : "droit" and "gauche"  <br> 
 * Instances of this class represent a node (= a token) in the tree. <br> 
 * Each node has a child (or null). For example "avc" has child "sylvien"  <br> 
 * Each node has a parent (or null). "sylvien" has parent "avc"  <br> 
 * We must reach the last token to get the code (uri). The final token has no child but a code (uri)<br> 
 * @author cossin
 *
 */
public class TokenTree {

	/**
	 * A unique code or URI 
	 */
	private final String code ;
	
	/**
	 * The child tree or null
	 */
	private final TokenTree tokenTreeChild ;

	/**
	 * The parent tree or null
	 */
	private final TokenTree tokenTreeParent;

	/**
	 * The token at this location
	 */
	private final String token ;

	/**
	 * Create a new TokenTree
	 * @param tokenTreeParent The tokenTree instance of this object or null if the token is the first (e.g. the value is null)
	 * @param tokensArray A String array of tokens ; the results of tokenization of a term
	 * @param code A code or URI of the term
	 */
	public TokenTree(TokenTree tokenTreeParent, String[] tokensArray, String code) {
		this.tokenTreeParent = tokenTreeParent;
		this.token = tokensArray[0];
		if (tokensArray.length != 1) { // if it's not the last token
			 // remove the first token
			String[] tokensArrayChild = Arrays.copyOfRange(tokensArray, 1, tokensArray.length);
			// create recursively the child 
			this.tokenTreeChild = new TokenTree(this,tokensArrayChild, code);
			this.code = null; // code is null if it's not the last token
		} else {
			this.tokenTreeChild = null;
			this.code = code; // only the last token get the code
		}
	}
	
	/************************************* Getters *********************************/
	
	/**
	 * Get the depth of the token
	 * @return the depth of the token in the tree (equals the position of the token in the array)
	 */
	public int getDepth() {
		int depth = 0;
		TokenTree tokenTree = this.getTokenTreeParent();
		while (tokenTree !=null) {
			depth = depth + 1;
			tokenTree = tokenTree.getTokenTreeParent();
		}
		return(depth);
	}
	
	/**
	 * 
	 * @return An array of String containing the previous tokens
	 */
	public String[] getPreviousTokens() {
		String[] tokens = new String[getDepth()];
		TokenTree tokenTree = this.getTokenTreeParent();
		while (tokenTree !=null) {
			//System.out.println(tokenTree.getToken());
			tokens[tokenTree.getDepth()] = tokenTree.getToken();
			tokenTree = tokenTree.getTokenTreeParent();
		}
		return(tokens);
	}
	
	/**
	 * 
	 * @return An array of String containing the current token and the previous ones
	 */
	public String[] getCurrentAndPreviousTokens() {
		int depth = getDepth();
		String[] tokens = new String[depth + 1];
		tokens[depth] = this.getToken();
		TokenTree tokenTree = this.getTokenTreeParent();
		while (tokenTree !=null) {
			depth = depth - 1;
			tokens[depth] = tokenTree.getToken();
			tokenTree = tokenTree.getTokenTreeParent();
		}
		return(tokens);
	}

	/**
	 * 
	 * @return The token of this node
	 */
	public String getToken() {
		return(token);
	}

	/**
	 * 
	 * @return The child tree or null
	 */
	public TokenTree getTokenTreeChild() {
		return(tokenTreeChild);
	}

	/**
	 * 
	 * @return The parent tree or null
	 */
	public TokenTree getTokenTreeParent() {
		return(tokenTreeParent);
	}

	/**
	 * 
	 * @return The code/uri or null if the token is not the last one
	 */
	
	public String getCode() {
		return(code);
	}
}
