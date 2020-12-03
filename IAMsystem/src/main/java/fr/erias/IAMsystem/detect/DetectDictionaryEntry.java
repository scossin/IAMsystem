package fr.erias.IAMsystem.detect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;
import fr.erias.IAMsystem.tokenizernormalizer.TNoutput;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystem.tree.TokenTree;


/**
 * This class extracts dictionary entries in a sentence and return a TreeSet of {@link CTcode} <br>
 * containing, for each dictionary entry detected, the start and endOffset of the entry in the sentence with its code (or URI)
 * @author Cossin Sebastien
 *
 */
public class DetectDictionaryEntry {

	private final static Logger logger = LoggerFactory.getLogger(DetectDictionaryEntry.class);

	/**
	 * The initial terminology in a tree datastructure
	 */
	private final SetTokenTree setTokenTree;

	/**
	 * Normalization and tokenization of the sentence
	 */
	private final ITokenizerNormalizer tokenizerNormalizer;

	/**
	 * Set of synonyms: abbreviations, typos...
	 */
	private final HashSet<ISynonym> synonyms ;

	/**
	 * 
	 * @param setTokenTree A terminology stored in a tree datastructure. See {@link SetTokenTree}
	 * @param tokenizerNormalizer to normalize and tokenize terms in the sentence
	 * @param synonyms For each token, find synonym tokens (ex : abbreviations or typos or real synonym). See the inferface : {@link ISynonym}
	 */
	public DetectDictionaryEntry(SetTokenTree setTokenTree, ITokenizerNormalizer tokenizerNormalizer, HashSet<ISynonym> synonyms) {
		this.setTokenTree = setTokenTree;
		this.tokenizerNormalizer = tokenizerNormalizer ;
		this.synonyms = synonyms;
	}
	
	/**
	 * Initialize a new sentence
	 * @param sentence The sentence to analyze
	 * @return an instance of {@link DetectOutput} containing {@link CTcode} and {@link TNoutput}
	 */
	public DetectOutput detectCandidateTerm(String sentence) {
		// re-initialize :
		TreeLocation treeLocation = new TreeLocation(this.setTokenTree);

		// normalize, tokenize and detect :
		TNoutput tnoutput = tokenizerNormalizer.tokenizeNormalize(sentence);
		String[] tokensArray = tnoutput.getTokens();
		logger.debug(tokensArray.length + " tokens");
		
		while (treeLocation.getCurrentI() != tokensArray.length) {
			int currentI = treeLocation.getCurrentI(); // current ith token 
			logger.debug("current index: " + currentI);
			String token =  tokensArray[currentI];
			logger.debug("current token: " + token);
			treeLocation.setCurrentSynonyms(token, synonyms); // search synonyms (abbreviations, typos...)
			if (treeLocation.getMonitorCandidates().isCurrentFound()) { // the token is found in the tree
				changeTreeLocation(token, treeLocation); // move deeper
			} else {
				setCurrentCandidate(token, treeLocation, tnoutput); // add current candidate if this is one or ignore
			}
			treeLocation.setCurrentI(treeLocation.getCurrentI() + 1); // move to next token
		}

		// for the last token when the end of the sentence is reached
		if (treeLocation.getMonitorCandidates().isCurrentCandidate()) { // if the algorithm is currently exploring a candidate term :
			addCandidateTerm(treeLocation, tnoutput);
		}
		
		DetectOutput detectOutput = new DetectOutput(tnoutput, treeLocation.getCandidateTermsCode());
		return(detectOutput);
	}

	/**
	 * When a term is available in the tree, make a step deeper and change the current location
	 * @param token an available token at the current location
	 * @param treeLocation currentTreeLocation
	 */
	private void changeTreeLocation(String token, TreeLocation treeLocation) {
		// add the token to the array of tokens :
		treeLocation.getMonitorCandidates().addToken(token, treeLocation.getTempSetTokenTree()); // important! : tempSetTokenTree is passed here before modification below: (to keep track of previous tempSetToken with codes)
		// the algorithm change its current location in the tree : 
		SetTokenTree tempSetTokenTree = treeLocation.getTempSetTokenTree().getSetTokenTree(treeLocation.getCurrentSynonyms()); // currentSynonyms may contain : token (perfect match), abbreviations and corrected typos
		treeLocation.setTempSetTokenTree(tempSetTokenTree);
		logger.debug("size of tempSetTokens : " + tempSetTokenTree.getMapTokenTree().size());
	}
	/**
	 * Add a dictionary entry
	 * @param treeLocation currentTreeLocation
	 * @param TNoutput tnoutput: needed for private function addCandidateTerm
	 * @throws UnfoundTokenInSentence
	 */
	private void setCurrentCandidate(String token, TreeLocation treeLocation, TNoutput tnoutput) {
		// case not currently exploring the tree, no previous token was detected : nothing to do. => Next token
		if (!treeLocation.getMonitorCandidates().isCurrentCandidate()) {
			logger.debug("\t not a currentCandidate, go to next token");
			return; // 
		}

		// case stopwords : add and continue 
		if (tokenizerNormalizer.getNormalizer().getStopwords().isStopWord(token)) {
			logger.debug(" \t stopword detected");
			// add the stopword to the array of tokens :
			treeLocation.getMonitorCandidates().addToken(token);
			return;
		}

		// case end of a candidate term (isCurrentCandidate() is true)
		logger.debug("\t it's a current candidate, add it");
		addCandidateTerm(treeLocation, tnoutput);

		// reset the algorithm to detect the next candidate term : 
		treeLocation.reset(this.setTokenTree);
		// restart with the current token at the root of the tree 
		int currentI = treeLocation.getCurrentI() - 1; // -1 because +1 is added so it would be the current token
		treeLocation.setCurrentI(currentI);
	}

	/**
	 * Add a candidateTerm to the set of CT detected if a code is found
	 * @param treeLocation currentTreeLocation
	 * @throws UnfoundTokenInSentence 
	 */
	private void addCandidateTerm(TreeLocation treeLocation, TNoutput tnoutput) {
		// current position 
		// last token can be a stopword. We need to remove it : 

		// is a code associated to this candidateTerm ? find the last tokenTree with a code going backward
		treeLocation.getMonitorCandidates().setLastTokenTree(treeLocation.getTempSetTokenTree());
		TokenTree oneTokenTree = treeLocation.getMonitorCandidates().getLastTokenTree();
		if (oneTokenTree == null) { // for example "insuffisance" was matched but no code for this word (no term in the terminology)
			logger.debug("no previous token, code is null again. no term found");
			int currentI = (treeLocation.getCurrentI() - treeLocation.getMonitorCandidates().getCandidateTokensList().size()) + 1 ; // i => i + 1 ; without the first token
			treeLocation.setCurrentI(currentI);
			return;
		}
		logger.debug("term was found"); // a term with a code is found

		String[] candidateTokensArray = treeLocation.getMonitorCandidates().getCandidateTokenArray(tokenizerNormalizer);
		for (String candidate : candidateTokensArray) {
			logger.debug("new candidate is " + candidate);
		}

		// recalculate start/end position in sentence - substring to have the term ...
		int tokenStartPosition = treeLocation.getCurrentI() - treeLocation.getMonitorCandidates().getCandidateTokensList().size();
		int numberOfTokensRemoved = treeLocation.getMonitorCandidates().getCandidateTokensList().size() - candidateTokensArray.length;
		int tokenEndPosition = treeLocation.getCurrentI()-(numberOfTokensRemoved + 1) ; //-1 : previous one
		int startPosition = tnoutput.getTokenStartEndInSentence()[tokenStartPosition][0];
		int endPosition = tnoutput.getTokenStartEndInSentence()[tokenEndPosition][1]; // 
		String candidateTermString = tnoutput.getOriginalSentence().substring(startPosition, endPosition + 1); 
		String code =  oneTokenTree.getCode();
		String label = ITokenizer.arrayToString(oneTokenTree.getCurrentAndPreviousTokens()," ".charAt(0));
		logger.debug("code is : " + code);
		
		logger.debug("CandidateTermString : " + candidateTermString);
		
		// create it
		CTcode candidateTerm = new CTcode(candidateTermString, 
				candidateTokensArray, 
				startPosition, 
				endPosition,
				code,
				label,
				tokenStartPosition,
				tokenEndPosition);
		// finally add it
		treeLocation.getCandidateTermsCode().add(candidateTerm);
	}
	
	
	/**
	 * Get the terminology
	 * @return the {@link SetTokenTree} containing the terminology
	 */
	public SetTokenTree getSetTokenTree() {
		return(this.setTokenTree);
	}
	
	/**
	 * Set of synonyms: abbreviations, typos...
	 * @return a set of {@link ISynonym} that will search an alternative for each token
	 */
	public HashSet<ISynonym> getSynonyms (){
		return(this.synonyms);
	}
}


/**
 * A private class to keep track of the currentTreeLocation, candidate terms detected ...
 */

class TreeLocation {
	
	protected final static Logger logger = LoggerFactory.getLogger(TreeLocation.class);
	
	/**
	 * dictionary entry found = a {@link CandidateTerm} with a code
	 */
	private TreeSet<CTcode> candidateTermsCode = new TreeSet<CTcode>();
	
	/**
	 * A set of synonyms (typos and abbreviations) for the current tokens
	 */
	private HashSet<String[]> currentSynonyms = new HashSet<String[]>();
	
	/**
	 * The current location in the tree - the algorithm explores the tree
	 */
	private SetTokenTree tempSetTokenTree = null;
	
	/**
	 * the ith token currently analyzed
	 */
	private int currentI = 0;
	
	/**
	 * Handle how candidate term are found - keep track of codes, length...
	 */
	private MonitorCandidates monitorCandidates = new MonitorCandidates();

	public TreeLocation(SetTokenTree setTokenTree) {
		reset(setTokenTree); // initialize
	}
	
	/**
	 * Re-initialize the algorithm for the next candidateTerm
	 */
	public void reset(SetTokenTree setTokenTree) {
		this.setMonitorCandidates(new MonitorCandidates()); // will handle a new candidate term
		this.tempSetTokenTree = setTokenTree; // the current location of the algorithm in the tree 
	}
	
	/**
	 * Find synonyms (typos or abbreviations) for the current token
	 */
	public void setCurrentSynonyms(String token, HashSet<ISynonym> synonyms) {
		// find synonyms (typos and abbreviations) :
		currentSynonyms = new HashSet<String[]>(); // reinitializing synonyms

		// add the current token to currentSynonyms (will be used later)
		String[] tokenInArray = {token};
		currentSynonyms.add(tokenInArray);
		// find synonyms: 
		for (ISynonym synonym : synonyms) {
			currentSynonyms.addAll(synonym.getSynonyms(token)); // ex : typos and abbreviations
		}

		// if found by perfect match :
		boolean currentFound = tempSetTokenTree.containToken(token); 
		// if not found by perfect match, is it found with synonyms ?
		if (currentFound) {
			logger.debug(" \t token found by perfect match");
		} else {
			currentFound = tempSetTokenTree.containTokenBi(currentSynonyms);
			if (currentFound) {
				logger.debug(" \t token found by synonyms (typos or abbreviations)");
			} else {
				logger.debug(" \t token not found at all");
			}
		}
		monitorCandidates.setCurrentFound(currentFound);
	}
	
	public TreeSet<CTcode> getCandidateTermsCode() {
		return candidateTermsCode;
	}

	public void setCandidateTermsCode(TreeSet<CTcode> candidateTermsCode) {
		this.candidateTermsCode = candidateTermsCode;
	}

	public SetTokenTree getTempSetTokenTree() {
		return tempSetTokenTree;
	}

	public void setTempSetTokenTree(SetTokenTree tempSetTokenTree) {
		this.tempSetTokenTree = tempSetTokenTree;
	}

	public int getCurrentI() {
		return currentI;
	}

	public void setCurrentI(int currentI) {
		this.currentI = currentI;
	}

	public MonitorCandidates getMonitorCandidates() {
		return monitorCandidates;
	}

	public void setMonitorCandidates(MonitorCandidates monitorCandidates) {
		this.monitorCandidates = monitorCandidates;
	}

	public HashSet<String[]> getCurrentSynonyms() {
		return currentSynonyms;
	}

	public void setCurrentSynonyms(HashSet<String[]> currentSynonyms) {
		this.currentSynonyms = currentSynonyms;
	}
}

/**
 * A private class to keep track of everything when search for a candidate term in the tree
 * @author cossin
 *
 */
class MonitorCandidates{
	
	private final static Logger logger = LoggerFactory.getLogger(MonitorCandidates.class);
	
	/**
	 * If the current token is found in the dictionary
	 */
	private boolean currentFound = false;

	/**
	 * The algorithm is currently checking a candidate (we are deeper in the tree)
	 */
	private boolean currentCandidate = false;

	/**
	 * Sometimes we need to go backward, as we go deeper in the tree, if no code is found, we go up to find a previous code
	 * lastTokenTree saves the last {@link TokenTree} or null if no code was found so far
	 */
	private TokenTree lastTokenTree = null;
	int lastTokenTreePosition = 0; // why not simply use the depth ? because => 
	// term "avc" is length 1 but "accident vasculaire cerebral" is depth 3 in the tree ; depth in the tree is not equal to the candidateTokensArray length

	/**
	 * The current candidate : an array of tokens
	 */
	private ArrayList<String> candidateTokensList = new ArrayList<String>();

	/**
	 * add a token 
	 * @param token save a token that was found in the current tree (could be a stopword too)
	 */
	public void addToken(String token) {
		candidateTokensList.add(token);
		currentCandidate = true;
	}

	/**
	 * 
	 * @param token a token that was found in the current tree
	 * @param tempSetTokenTree the state of the tree BEFORE adding the current token (to save previous code)
	 */
	public void addToken(String token, SetTokenTree tempSetTokenTree) {
		currentCandidate = true;
		setLastTokenTree(tempSetTokenTree);
		candidateTokensList.add(token); // NB ++ : after to setLastTokenTree
	}

	/**
	 * Keep track of the path when we go deeper in the tree
	 * @param tempSetTokenTree Search in the tempSetTokenTree a code to save for backward search if finally no code is found
	 */
	public void setLastTokenTree(SetTokenTree tempSetTokenTree) {
		if (tempSetTokenTree.getPreviousTokenTrees().size() != 0) {
			TokenTree oneTokenTree = tempSetTokenTree.getOneTokenTree();
			this.lastTokenTree = oneTokenTree;
			this.lastTokenTreePosition = candidateTokensList.size();
		}
	}

	/**
	 * Get all the tokens to be saved
	 * @param tokenizerNormalizer to check if the last tokens are stopwords or not
	 * @return An array of candidateToken
	 */
	public String[] getCandidateTokenArray(ITokenizerNormalizer tokenizerNormalizer) {
		// sometimes we must go backward to retrieve a code
		ArrayList<String> tempCandidateTokenList = null;
		if (candidateTokensList.size()!= lastTokenTreePosition) {
			logger.debug("Removing some tokens because we need to go backward");
			tempCandidateTokenList = new ArrayList<String>(candidateTokensList.subList(0,lastTokenTreePosition));
		} else {
			tempCandidateTokenList = candidateTokensList; // if not, it's just equal to candidateTokensList
		}
		
		// number of stopwords to remove:
		int numberOfTokens2remove = 0;
		for (int y = tempCandidateTokenList.size() -1; y>0;y--) {
			String lastToken = tempCandidateTokenList.get(y);
			if (tokenizerNormalizer.getNormalizer().getStopwords().isStopWord(lastToken)){
				logger.debug("last token is a stopword : " + lastToken);
				numberOfTokens2remove = numberOfTokens2remove + 1;
			} else {
				break;
			}
		}
		String[] tokensArray = tempCandidateTokenList.toArray(new String[0]);
		tokensArray = Arrays.copyOfRange(tokensArray, 0, tokensArray.length - numberOfTokens2remove);
		return(tokensArray);
	}

	/**
	 * 
	 * @return the last TokenTree or null if no code so far
	 */
	public TokenTree getLastTokenTree() {
		return(lastTokenTree);
	}

	/**
	 * 
	 * @return If the current token is found in the dictionary
	 */
	public boolean isCurrentFound() {
		return currentFound;
	}

	public void setCurrentFound(boolean currentFound) {
		this.currentFound = currentFound;
	}

	/**
	 * @return true if the algorithm is currently checking a candidate (we are deeper in the tree)
	 */
	public boolean isCurrentCandidate() {
		return currentCandidate;
	}

	public void setCurrentCandidate(boolean currentCandidate) {
		this.currentCandidate = currentCandidate;
	}

	public ArrayList<String> getCandidateTokensList() {
		return candidateTokensList;
	}

	public void setCandidateTokensArray(ArrayList<String> candidateTokensArray) {
		this.candidateTokensList = candidateTokensArray;
	}
}
