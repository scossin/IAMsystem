package fr.erias.IAMsystem.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains all the terms of a terminology. Each term has 1 to n token. Each token is a {@link TokenTree} <br> 
 * The main attribute is mapTokenTree : it contains all the first token (the key of the HashMap) of every terms of the terminology <br>
 * Each token has 0 to many childs (possibilities) or {@link TokenTree}
 * @author Cossin Sebastien
 *
 */
public class SetTokenTree {

	final static Logger logger = LoggerFactory.getLogger(SetTokenTree.class);

	/**
	 * The main attribute : it contains all the first token of every terms of a terminology
	 */
	private HashMap<String, HashSet<TokenTree>> mapTokenTree = new HashMap<String, HashSet<TokenTree>>();

	/**
	 * A HashSet of codes (uri). 
	 * if a tokenTree has no child (null), we retrieve its code and we put it here <br>
	 * For example, if a term is "cancer" with code "xx" ; it has only one token <br>
	 * For every terms beginning with the word "cancer" : 
	 * The next tokens are stored in mapTokenTree 
	 * and for the term "cancer" itself (no child), its code is stored here. So we know the token "cancer" has code "xx"
	 */
	private HashSet<TokenTree> previousTokenTrees = new HashSet<TokenTree>();

	/**
	 * Construct an empty SetTokenTree. 
	 */
	public SetTokenTree() {
		
	}
	
	/**
	 * Add a @link{TokenTree} to this instance
	 * @param tokenTree a @link{TokenTree}
	 */
	public void addTokenTree(TokenTree tokenTree) {
		String term = tokenTree.getToken();
		if (!mapTokenTree.containsKey(term)) {
			mapTokenTree.put(term, new HashSet<TokenTree>());
		} 
		mapTokenTree.get(term).add(tokenTree);
	}
	
	/**
	 * Add a HashSet @link{TokenTree} to this instance
	 * @param tokenTrees a HashSet of @link{TokenTree}
	 */
	public void addTokenTrees(HashSet<TokenTree> tokenTrees) {
		for (TokenTree tokenTree : tokenTrees) {
			addTokenTree(tokenTree);
		}
	}

	/************************************* Entry available  ? ************************************/
	
	/**
	 * Check if mapTokenTree contains this token 
	 * @param token The first or nth token of a term
	 * @return true if the token is an entry available 
	 */
	public boolean containToken(String token) {
		return(mapTokenTree.containsKey(token));
	}

	/**
	 * Check if mapTokenTree contains at least one of these tokens 
	 * @param tokens A HashSet of tokens to test
	 * @return true if at least one token in the set is an entry available 
	 */
	public boolean containToken(HashSet<String> tokens) {
		for (String token : tokens) {
			if (mapTokenTree.containsKey(token)) {
				return(true);
			}
		}
		return(false);
	}
	
	
	/**
	 * Check if mapTokenTree contains at least one of these String array of tokens 
	 * For example we may want to know if "sylvien" followed by "droit" is available after "avc". 
	 * @param synonyms A HashSet
	 * @return true if at least one the string array of tokens is available
	 */
	public boolean containTokenBi(HashSet<String[]> synonyms) {
		for (String[] synonyme : synonyms) {
			SetTokenTree tempSetTokenTree = getSetTokenTree(synonyme);
			int sizeMapToken = tempSetTokenTree.getMapTokenTree().size();
			int sizeCodes = tempSetTokenTree.getPreviousTokenTrees().size();
			// if sizeMapToken is not 0 it means we didn't reach the maximum depth of the tree
			// if sizeMapToken is 0, we may have reach the maximum depth, we may have one code
			// if code is 0, it means we are not in the tree anymore
			if (sizeMapToken !=0 || sizeCodes !=0) {
				return(true);
			}
		}
		return(false);
	}
	
	
	/*********************************** Function to get a SetTokenTree ********************************/
	
	/**
	 * Get a {@link SetTokenTree} for a specific token. 
	 * Go one step deep in the tree by asking all the entries of a specific token. 
	 * @param token A token string
	 * @return a @link{SetTokenTree}
	 */
	public SetTokenTree getSetTokenTree(String token) {
		SetTokenTree newSetTokenTree = new SetTokenTree();
		// return an empty Set if it doesn't contain the token
		if (mapTokenTree.containsKey(token)) {
			for (TokenTree tokenTree : mapTokenTree.get(token)) {
				TokenTree tokenTreeChild = tokenTree.getTokenTreeChild();
				if (tokenTreeChild != null) {
					newSetTokenTree.addTokenTree(tokenTreeChild); // if no child, no further entry available	
				} else {
					newSetTokenTree.addPreviousTokenTree(tokenTree); // if no child, adds the previous tokenTree	
				}
			}
		}
		return(newSetTokenTree);
	}
	
	/**
	 * Add a code to the hashSet of codes
	 * @param code a code or URI
	 */
	private void addPreviousTokenTree(TokenTree previousTokenTree) {
		previousTokenTrees.add(previousTokenTree);
	}
	
	/**
	 * Get a {@link SetTokenTree} for a String array of tokens.
	 * We go deeper in the tree for each token in the arrary
	 * @param tokensArray an array of tokens. 
	 * @return a {@link SetTokenTree}
	 */
	public SetTokenTree getSetTokenTree(String[] tokensArray) {
		logger.debug("\t \t getSetTokenTree by tokensArray");
		SetTokenTree newSetTokenTree = this;
		for (int i = 0 ; i < tokensArray.length; i++) {
			String token = tokensArray[i];
			logger.debug("\t \t token : " + token);
			newSetTokenTree = newSetTokenTree.getSetTokenTree(token);
			logger.debug("\t \t size : " + newSetTokenTree.getMapTokenTree().size());
		}
		return(newSetTokenTree);
	}
	
	/**
	 * Get a {@link SetTokenTree} for multiple  arrays of string (containing tokens).
	 * For example, we may want all childs of "avc" and all childs of {"accident","vasculaire","cerebral"}
	 * @param synonyms A HashSet of arrays of string 
	 * @return a @link{SetTokenTree}
	 */
	public SetTokenTree getSetTokenTree(HashSet<String[]> synonyms) {
		SetTokenTree newSetTokenTree = new SetTokenTree();
		for (String[] synonyme : synonyms) {
			logger.debug("synonyms : " + synonyme[0]);
			SetTokenTree tempSetTokenTree = getSetTokenTree(synonyme);
			for (HashSet<TokenTree> hashsetTokenTree : tempSetTokenTree.getMapTokenTree().values()) {
				for (TokenTree tokenTree : hashsetTokenTree) {
					newSetTokenTree.addTokenTree(tokenTree);
				}
			}
			logger.debug("newSetTokenTreeSize : " + newSetTokenTree.getMapTokenTree().size());
			newSetTokenTree.getPreviousTokenTrees().addAll(tempSetTokenTree.getPreviousTokenTrees());
			logger.debug("number of codes tempSetTokenTree : " + tempSetTokenTree.getPreviousTokenTrees().size());
		}
		return(newSetTokenTree);
	}
	

	/***************************** Getters **************************************/
	/**
	 * 
	 * @return A HashMap containing in keys, all the entries (token). For each entry we may have one or many nodes @link{TokenTree} <br>
	 */
	public HashMap<String, HashSet<TokenTree>> getMapTokenTree(){
		return(mapTokenTree);
	}
	
	/**
	 * Get all available entries of the tree
	 * @return The keys of mapTokenTree
	 */
	public Set<String> getAvailableTokens(){
		return(mapTokenTree.keySet());
	}

	/**
	 * If we reach a node ({@link TokenTree}) that has no child, its code is available in this set
	 * @return A HashSet of codes available
	 */
	public HashSet<TokenTree> getPreviousTokenTrees(){
		return(previousTokenTrees);
	}
	
	/**
	 * Retrieve only one {@link TokenTree}
	 * @return one {@link TokenTree} or null if empty
	 */
	public TokenTree getOneTokenTree() {
		if (previousTokenTrees.isEmpty()) {
			return(null);
		}
		if (previousTokenTrees.size() != 1) {
			logger.debug("warning ! multiples codes available !");
		}
		for (TokenTree tokenTree : previousTokenTrees) {
			return(tokenTree);
		}
		return(null);
	}
	
	/**
	 * Retrieve only one code in {@link getOneTokenTree}. A term may have one or many codes (e.g only one)
	 * Deprecated, use {@link getOneTokenTree} to retrieve a code
	 * @return A string of a code (uri) of one {@link TokenTree}
	 */
	@Deprecated
	 public String getOneCode() {
		if (previousTokenTrees.isEmpty()) {
			return(null);
		}
		if (previousTokenTrees.size() != 1) {
			logger.debug("warning ! multiples codes available !");
		}
		for (TokenTree tokenTree : previousTokenTrees) {
			return(tokenTree.getCode());
		}
		return(null);
	}
}
