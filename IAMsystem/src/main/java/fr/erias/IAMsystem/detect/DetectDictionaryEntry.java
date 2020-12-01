package fr.erias.IAMsystem.detect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.erias.IAMsystem.ct.CT;
import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.tokenizer.TNoutput;
import fr.erias.IAMsystem.tokenizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystem.tree.TokenTree;


/**
 * This class extracts dictionary entries in a sentence and return a TreeSet of {@link CTcode} <br>
 * containing, for each dictionary entry detected, the start and endOffset of the entry in the sentence with its code (or URI)
 * @author Cossin Sebastien
 *
 */
public class DetectDictionaryEntry {

	protected final static Logger logger = LoggerFactory.getLogger(DetectDictionaryEntry.class);

	/**
	 * dictionary entry found = a {@link CandidateTerm} with a code
	 */
	private TreeSet<CTcode> candidateTermsCode = new TreeSet<CTcode>();

	/**
	 * The initial terminology in a tree datastructure
	 */
	protected SetTokenTree setTokenTree = null;

	/**
	 * The current location in the tree - the algorithm explores the tree
	 */
	private SetTokenTree tempSetTokenTree = null;

	/**
	 * A set of synonyms (typos and abbreviations) for the current tokens
	 */
	private HashSet<String[]> currentSynonyms = new HashSet<String[]>();

	/**
	 * Handle how candidate term are found - keep track of codes, length...
	 */
	private MonitorCandidates monitorCandidates = new MonitorCandidates();

	/**
	 * the ith token currently analyzed
	 */
	private int currentI = 0;

	/**
	 * Normalization and tokenization of the sentence
	 */
	private TokenizerNormalizer tokenizerNormalizer;

	/**
	 * Tokenizer normalized output
	 */
	private TNoutput tnoutput;
	
	/**
	 * Set of synonyms: abbreviations, typos...
	 */
	private HashSet<Synonym> synonyms = new HashSet<Synonym>();

	/**
	 * 
	 * @param setTokenTree A terminology stored in a tree datastructure. See {@link SetTokenTree}
	 * @param tokenizer to normalize and tokenize terms in the sentence
	 * @param synonyms For each token, find synonym tokens (ex : abbreviations or typos or real synonym). See the inferface : {@link Synonym}
	 */
	public DetectDictionaryEntry(SetTokenTree setTokenTree,TokenizerNormalizer tokenizer, HashSet<Synonym> synonyms) {
		this.setTokenTree = setTokenTree;
		this.tempSetTokenTree = setTokenTree;
		this.tokenizerNormalizer = tokenizer ;
		this.synonyms = synonyms;
	}
	
	/**
	 * Add a way to detect synonym of a token (abbreviation, normalized token, typo...) See {@link Synonym}
	 * @param synonym A {@link Synonym} instance
	 */
	public void addSynonym(Synonym synonym) {
		synonyms.add(synonym);
	}

	/**
	 * Initialize a new sentence
	 * @param sentence The sentence to analyze
	 * @throws UnfoundTokenInSentence The offsets of the token can't be found
	 */
	public void detectCandidateTerm(String sentence) throws UnfoundTokenInSentence {
		// re-initialize :
		this.currentI = 0;
		candidateTermsCode.clear();
		reset();

		// normalize, tokenize and detect :
		this.tnoutput = tokenizerNormalizer.tokenizeNormalize(sentence);
		String[] tokensArray = tnoutput.getTokens();
		logger.debug(tokensArray.length + " tokens");
		while (currentI != tokensArray.length) {
			logger.debug("current index: " + currentI);
			String token =  tokensArray[currentI];
			logger.debug("current token: " + token);
			setCurrentSynonyms(token); // search synonyms (abbreviations, typos...)
			if (monitorCandidates.isCurrentFound()) {
				changeTreeLocation(token);
			} else {
				setCurrentCandidate(token);
			}
			this.currentI = currentI + 1;
		}

		// for the last token when the end of the sentence is reached
		if (monitorCandidates.isCurrentCandidate()) { // if the algorithm is currently exploring a candidate term :
			addCandidateTerm();
		}
	}

	/**
	 * Get dictionary entries detected
	 * @return A set of dictionary entries detected stored in a {@link CTcode}
	 */
	public TreeSet<CTcode> getCTcode(){
		return(candidateTermsCode);
	}

	/**
	 * Re-initialize the algorithm for the next candidateTerm
	 */
	private void reset() {
		this.monitorCandidates = new MonitorCandidates(); // will handle a new candidate term
		this.tempSetTokenTree = this.setTokenTree; // the current location of the algorithm in the tree 
	}


	/**
	 * Find synonyms (typos or abbreviations) for the current token
	 */
	private void setCurrentSynonyms(String token) {
		// find synonyms (typos and abbreviations) :
		currentSynonyms = new HashSet<String[]>(); // reinitializing synonyms

		// add the current token to currentSynonyms (will be used later)
		String[] tokenInArray = {token};
		currentSynonyms.add(tokenInArray);
		// find synonyms: 
		for (Synonym synonym : synonyms) {
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

	/**
	 * When a term is available in the tree, make a step deeper and change the current location
	 * @param token an available token at the current location
	 */
	private void changeTreeLocation(String token) {
		// add the token to the array of tokens :
		monitorCandidates.addToken(token, tempSetTokenTree); // important! : tempSetTokenTree is passed here before modification below: (to keep track of previous tempSetToken with codes)
		// the algorithm change its current location in the tree : 
		tempSetTokenTree = tempSetTokenTree.getSetTokenTree(currentSynonyms); // currentSynonyms may contain : token (perfect match), abbreviations and corrected typos
		logger.debug("size of tempSetTokens : " + tempSetTokenTree.getMapTokenTree().size());
	}
	/**
	 * Add a dictionary entry
	 * @throws UnfoundTokenInSentence
	 */
	private void setCurrentCandidate(String token) throws UnfoundTokenInSentence {
		// case not currently exploring the tree, no previous token was detected : nothing to do. => Next token
		if (!monitorCandidates.isCurrentCandidate()) {
			logger.debug("\t not a currentCandidate, go to next token");
			return; // 
		}

		// case stopwords : add and continue 
		if (tokenizerNormalizer.getNormalizerTerm().getStopwords().isStopWord(token)) {
			logger.debug(" \t stopword detected");
			// add the stopword to the array of tokens :
			monitorCandidates.addToken(token);
			return;
		}

		// case end of a candidate term : 
		logger.debug("\t it's a current candidate, add it");
		addCandidateTerm();

		// reset the algorithm to detect the next candidate term : 
		reset();
		// restart with the current token at the root of the tree 
		this.currentI = this.currentI - 1; 
	}

	/**
	 * Add a candidateTerm to the set of CT detected if a code is found
	 * @throws UnfoundTokenInSentence 
	 */
	private void addCandidateTerm() throws UnfoundTokenInSentence {
		// current position 
		// last token can be a stopword. We need to remove it : 

		// is a code associated to this candidateTerm ?
		monitorCandidates.setLastTokenTree(tempSetTokenTree);
		TokenTree oneTokenTree = monitorCandidates.getLastTokenTree();
		if (oneTokenTree == null) {
			logger.debug("no previous token, code is null again");
			this.currentI = (currentI - monitorCandidates.getCandidateTokensList().size()) + 1 ; // i => i + 1 ; without the first token
			return;
		}
		logger.debug("code was found");

		String[] candidateTokensArray = monitorCandidates.getCandidateTokenArray(tokenizerNormalizer);
		for (String candidate : candidateTokensArray) {
			logger.debug("new candidate is " + candidate);
		}

		// startPosition and endPosition in the sentence :
		int tokenStartPosition = currentI - monitorCandidates.getCandidateTokensList().size();
		int numberOfTokensRemoved = monitorCandidates.getCandidateTokensList().size() - candidateTokensArray.length;
		int tokenEndPosition = currentI-(numberOfTokensRemoved + 1) ; //-1 : previous one
		int startPosition = this.tnoutput.getTokenStartEndInSentence()[tokenStartPosition][0];
		int endPosition = this.tnoutput.getTokenStartEndInSentence()[tokenEndPosition][1]; // 
		String candidateTermString = this.tnoutput.getOriginalSentence().substring(startPosition, endPosition + 1); 

		String code =  oneTokenTree.getCode();
		String label = CT.arrayToString(oneTokenTree.getCurrentAndPreviousTokens()," ".charAt(0));
		logger.debug("code is : " + code);

		logger.debug("CandidateTermString : " + candidateTermString);
		CTcode candidateTerm = new CTcode(candidateTermString, 
				candidateTokensArray, 
				startPosition, 
				endPosition,
				code,
				label);
		candidateTermsCode.add(candidateTerm);

		// this.currentI = (currentI - monitorCandidates.getCandidateTokensArray().size()) + 1 ; // i => i + 1 ; without the first token
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
	 * @return a set of {@link Synonym} that will search an alternative for each token
	 */
	public HashSet<Synonym> getSynonyms (){
		return(this.synonyms);
	}
}

/**
 * A private class to keep track of everything when search for a candidate term in the tree
 * @author cossin
 *
 */
class MonitorCandidates{
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
	public String[] getCandidateTokenArray(TokenizerNormalizer tokenizerNormalizer) {
		// sometimes we must go backward to retrieve a code
		ArrayList<String> tempCandidateTokenList = null;
		if (candidateTokensList.size()!= lastTokenTreePosition) {
			DetectDictionaryEntry.logger.debug("Removing some tokens because we need to go backward");
			tempCandidateTokenList = new ArrayList<String>(candidateTokensList.subList(0,lastTokenTreePosition));
		} else {
			tempCandidateTokenList = candidateTokensList; // if not, it's just equal to candidateTokensList
		}
		
		// number of stopwords to remove:
		int numberOfTokens2remove = 0;
		for (int y = tempCandidateTokenList.size() -1; y>0;y--) {
			String lastToken = tempCandidateTokenList.get(y);
			if (tokenizerNormalizer.getNormalizerTerm().getStopwords().isStopWord(lastToken)){
				DetectDictionaryEntry.logger.debug("last token is a stopword : " + lastToken);
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
