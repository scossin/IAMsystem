package fr.erias.IAMsystem.detect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;
import fr.erias.IAMsystem.tokenizernormalizer.TNoutput;
import fr.erias.IAMsystem.tree.INode;
import fr.erias.IAMsystem.tree.Trie;


/**
 * This class extracts dictionary entries in a document and return a List of {@link CTcode} <br>
 * containing, for each dictionary entry detected, the start and endOffset of the entry in the sentence with its code (or URI)
 * @author Cossin Sebastien
 *
 */
public class DetectCT implements IDetectCT {

	private final static Logger logger = LoggerFactory.getLogger(DetectCT.class);

	/**
	 * The initial terminology in a tree datastructure
	 */
	private final Trie trie;

	/**
	 * Normalization and tokenization of the sentence
	 */
	private final ITokenizerNormalizer tokenizerNormalizer;

	/**
	 * Set of synonyms: abbreviations, typos...
	 */
	private final Set<ISynonym> synonyms ;

	/**
	 * 
	 * @param setTokenTree A terminology stored in a tree datastructure. See {@link SetTokenTree}
	 * @param tokenizerNormalizer to normalize and tokenize terms in the sentence
	 * @param synonyms For each token, find synonym tokens (ex : abbreviations or typos or real synonym). See the inferface : {@link ISynonym}
	 */
	public DetectCT(Trie trie, ITokenizerNormalizer tokenizerNormalizer, Set<ISynonym> synonyms) {
		this.trie = trie;
		this.tokenizerNormalizer = tokenizerNormalizer ;
		this.synonyms = synonyms;
	}

	@Override
	public DetectOutput detectCandidateTerm(String sentence) {
		// re-initialize :
		TreeLocation treeLocation = new TreeLocation(trie.getInitialState());

		// normalize, tokenize and detect :
		TNoutput tnoutput = tokenizerNormalizer.tokenizeNormalize(sentence);
		String[] tokensArray = tnoutput.getTokens();
		logger.debug(tokensArray.length + " tokens");

		while (treeLocation.getCurrentI() != tokensArray.length) {
			int currentI = treeLocation.getCurrentI(); // current ith token 
			logger.debug("current index: " + currentI);
			String token = tokensArray[currentI];
			logger.debug("current token: " + token);
			if (tokenizerNormalizer.getNormalizer().getStopwords().isStopWord(token)) {
				treeLocation.addStopword(token);
			} else {
				treeLocation.searchNextStates(tnoutput, token, synonyms); // search synonyms (abbreviations, typos...)
			}
		}
		DetectOutput detectOutput = new DetectOutput(tnoutput, treeLocation.getCandidateTermsCode());
		return(detectOutput);
	}

	/**
	 * Get the terminology
	 * @return the {@link SetTokenTree} containing the terminology
	 */
	public Trie getTrie() {
		return(this.trie);
	}

	/**
	 * Set of synonyms: abbreviations, typos...
	 * @return a set of {@link ISynonym} that will search an alternative for each token
	 */
	public Set<ISynonym> getSynonyms (){
		return(this.synonyms);
	}
}


/**
 * A private class to keep track of the currentTreeLocation, candidate terms detected ...
 */

class TreeLocation {

	protected final static Logger logger = LoggerFactory.getLogger(TreeLoc.class);

	/**
	 * dictionary entry found = a {@link CandidateTerm} with a code
	 */
	private List<CTcode> candidateTermsCode = new ArrayList<CTcode>();

	/**
	 * The current location in the tree - the algorithm explores the tree
	 */
	private Set<INode> nodes ;

	private int tokenAtInitialState = 0;

	private ArrayList<String> candidateTokensList = new ArrayList<String>();

	private final INode rootNode;

	/**
	 * the ith token currently analyzed
	 */
	private int currentI = 0;

	public TreeLocation(INode rootNode) {
		this.rootNode = rootNode;
		setInitialState(); // initialize
	}

	/**
	 * Re-initialize the algorithm for the next candidateTerm
	 */
	public void setInitialState() {
		this.nodes = new HashSet<INode>(); // the current location of the algorithm in the tree 
		this.nodes.add(rootNode);
		candidateTokensList = new ArrayList<String>();
	}
	
	private Map<String, Set<List<String>>> synonymsCache = new HashMap<String, Set<List<String>>>();

	/**
	 * Find synonyms (typos or abbreviations) for the current token
	 */
	private Set<INode> nextStates(String token, Set<ISynonym> synonyms) {
		HashSet<INode> nextStates = new HashSet<INode>();
		Set<List<String>> currentSynonyms = getSynonymsOfToken(token, synonyms);
		for (INode currentState : nodes) {
			nextStates.addAll(currentState.gotoNodes(currentSynonyms));
		}
		return(nextStates);
	}
	
	private Set<List<String>> getSynonymsOfToken(String token, Set<ISynonym> synonyms) {
		if (synonymsCache.containsKey(token)) {
			return(synonymsCache.get(token));
		} else {
			// find synonyms (typos and abbreviations) :
			Set<List<String>> currentSynonyms = new HashSet<List<String>>(); // reinitializing synonyms
			// add the current token to currentSynonyms (will be used later)
			String[] tokenInArray = {token};
			currentSynonyms.add(Arrays.asList(tokenInArray));
			// find synonyms: 
			for (ISynonym synonym : synonyms) {
				currentSynonyms.addAll(synonym.getSynonyms(token)); // ex : typos and abbreviations
			}
			synonymsCache.put(token, currentSynonyms);
			return(currentSynonyms);
		}
	}

	private void saveTermIfAnyFinalState(TNoutput tnoutput, Set<INode> nextStates) {
		for (INode state : nextStates) {
			if (!state.isAfinalState()) {
				continue;
			}
			Term term =  state.getTerm();
			int tokenStartPosition = tokenAtInitialState;
			int tokenEndPosition = getCurrentI();
			int startPosition = tnoutput.getTokenStartEndInSentence()[tokenStartPosition][0];
			int endPosition = tnoutput.getTokenStartEndInSentence()[tokenEndPosition][1]; // 
			String candidateTermString = tnoutput.getOriginalSentence().substring(startPosition, endPosition + 1); 

			logger.debug("CandidateTermString : " + candidateTermString);

			String[] candidateTokenArray = candidateTokensList.toArray(new String[candidateTokensList.size()]);
			// create it
			logger.debug("code is : " + term.getCode());
			// String label = ITokenizer.arrayToString(tokenTree.getCurrentAndPreviousTokens()," ".charAt(0));
			CTcode candidateTerm = new CTcode(candidateTermString, 
					candidateTokenArray,
					startPosition, 
					endPosition,
					term,
					tokenStartPosition,
					tokenEndPosition);
			// finally add it
			addCandidateTerm(candidateTerm);
		}
	}
	
	private void addCandidateTerm(CTcode candidateTerm) {
		while (iNeed2removeLastCT(candidateTerm)) {
			candidateTermsCode.remove(candidateTermsCode.size() - 1);
		}
		candidateTermsCode.add(candidateTerm);
	}
	
	private boolean iNeed2removeLastCT(CTcode candidateTerm) {
		if (candidateTermsCode.isEmpty()) {
			return(false);
		}
		CTcode lastCT = candidateTermsCode.get(candidateTermsCode.size() - 1);
		if (lastCT.getStartPosition() == candidateTerm.getStartPosition() && 
				lastCT.getEndPosition() < candidateTerm.getEndPosition()) {
			return true;
		} else {
			return(false);
		}
	}
	
	public void searchNextStates(TNoutput tnoutput, String token, Set<ISynonym> synonyms) {
		nodes = nextStates(token, synonyms);
		logger.debug(" \t nextStates size: " + nodes.size());
		if (pathFound(nodes)) {
			candidateTokensList.add(token);
			saveTermIfAnyFinalState(tnoutput, nodes);
			nextToken();
			return;
		} 
		// no path found at initial state => try to find a path at the next token
		if (isTheInitialState()) {
			nextToken();
		} 
		// no path at another state => go to the initial state and try to find a path with the same token
		setInitialState();
		tokenAtInitialState = getCurrentI();
	}
	
	public void addStopword(String token) {
		if (isTheInitialState()) {
			nextToken();
			tokenAtInitialState = getCurrentI();
		} else {
			candidateTokensList.add(token);
			nextToken();
		}
	}
	
	public boolean isTheInitialState() {
		return getCurrentI() == tokenAtInitialState;
	}
	
	public void nextToken() {
		this.currentI = this.currentI + 1;
	}

	private boolean pathFound(Set<INode> nextStates) {
		return nextStates.size() != 0;
	}

	public List<CTcode> getCandidateTermsCode() {
		return candidateTermsCode;
	}

	public int getCurrentI() {
		return currentI;
	}

	public void setCurrentI(int currentI) {
		this.currentI = currentI;
	}
}
