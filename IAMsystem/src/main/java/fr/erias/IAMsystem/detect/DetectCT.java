package fr.erias.IAMsystem.detect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.synonym.CacheSyn;
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

	/**
	 * The initial terminology in a tree datastructure
	 */
	private final Trie trie;

	/**
	 * Normalization and tokenization of the sentence
	 */
	private final ITokenizerNormalizer tokenizerNormalizer;

	/**
	 * Set of fuzzyAlgorithms: abbreviations, Levenshtein, soundex...
	 */
	private final Set<ISynonym> fuzzyAlgorithms ;
	
	private CacheSyn cacheSyn; // a cache of synonyms
	
	private boolean keepOverlappingTerms; 
	
	/**
	 * Detects the terms of a terminology
	 * @param trie A terminology stored in a tree datastructure. See {@link Trie}
	 * @param tokenizerNormalizer to normalize and tokenize terms in the sentence
	 * @param fuzzyAlgorithms they find a matching token in the terminolog. See the interface : {@link ISynonym}
	 */
	public DetectCT(Trie trie, ITokenizerNormalizer tokenizerNormalizer, Set<ISynonym> fuzzyAlgorithms) {
		this.trie = trie;
		this.tokenizerNormalizer = tokenizerNormalizer ;
		this.fuzzyAlgorithms = fuzzyAlgorithms;
		this.cacheSyn = new CacheSyn(fuzzyAlgorithms);
		this.keepOverlappingTerms = false;
	}
	
	@Override
	public DetectOutput detectCandidateTerm(String sentence) {
		if (sentence.isEmpty()) return emptyOutput();
		
		// re-initialize :
		TreeLocation treeLocation = new TreeLocation(trie.getInitialState(), keepOverlappingTerms);

		// normalize, tokenize and detect :
		TNoutput tnoutput = tokenizerNormalizer.tokenizeNormalize(sentence);
		String[] tokensArray = tnoutput.getTokens();
		IStopwords stopwords = tokenizerNormalizer.getNormalizer().getStopwords();
		while (treeLocation.getCurrentI() != tokensArray.length) {
			int currentI = treeLocation.getCurrentI(); // current ith token 
			String token = tokensArray[currentI];
			if (stopwords.isStopWord(token)) {
				treeLocation.addStopword(token);
			} else {
				treeLocation.searchNextStates(tnoutput, token, cacheSyn); // search synonyms (abbreviations, typos...)
			}
		}
		DetectOutput detectOutput = new DetectOutput(tnoutput, treeLocation.getCandidateTermsCode());
		return(detectOutput);
	}
	
	private DetectOutput emptyOutput() {
		String normalizedSentence = "";
		String originalSentence = normalizedSentence;
		String[] tokens = {};
		String[] tokensOriginal = tokens;
		int [][] tokensStartEnd = {};
		TNoutput tnoutpout = new TNoutput(originalSentence,normalizedSentence,tokens,tokensOriginal,tokensStartEnd);
		ArrayList<CTcode> nocodes = new ArrayList<>(0);
		DetectOutput detectOutput = new DetectOutput(tnoutpout, nocodes);
		return(detectOutput);
	}

	/**
	 * Get the terminology
	 * @return the {@link Trie} containing the terminology
	 */
	public Trie getTrie() {
		return(this.trie);
	}

	/**
	 * Set of fuzzyAlgorithms: abbreviations, soundex, troncation...
	 * @return a set of {@link ISynonym} that will search an alternative for each token
	 */
	public Set<ISynonym> getFuzzyAlgorithms (){
		return(this.fuzzyAlgorithms);
	}
	
	/**
	 * Method renamed 'getFuzzyAlgorithms' ; this method will be removed in future releases
	 * @return set of {@link ISynonym}
	 */
	@Deprecated
	public Set<ISynonym> getSynonyms() {
		return(this.fuzzyAlgorithms);
	}
	
	/**
	 * Change default {@link CacheSyn}
	 * @param cacheSyn to store approximate string in cache
	 */
	public void setCacheSynonyms(CacheSyn cacheSyn) {
		this.cacheSyn = cacheSyn;
	}
	
	/**
	 * Default to false: overlapping terms are not returned
	 * @param keepOverlappingTerms true if you want the algorithm to return overlapping terms
	 */
	public void setKeepOverlappingTerms(boolean keepOverlappingTerms) {
		this.keepOverlappingTerms = keepOverlappingTerms;
	}
	
	/**
	 * get keepOverlappingTerms variable value
	 * @return true if the algorithm returns overlapping terms
	 */
	public boolean getOverlappingTerms() {
		return(this.keepOverlappingTerms);
	}
}


/**
 * A private class to keep track of the currentTreeLocation, candidate terms detected ...
 */

class TreeLocation {

	private final INode rootNode; // = the initial state. 

	private int tokenAtInitialState = 0; // token 0 is in the initial state. 
	// this number is set to the ith token if this token (re)starts at the initial state 

	/**
	 * Current locations in the trie.
	 * Initially it contains only the rootNode. 
	 * Then it explores the trie given a token in input and its states change
	 */
	private Set<INode> states ;

	// stores all the CT detected
	private List<CTcode> ctDetected = new ArrayList<CTcode>(); 

	// keep track of the tokens of the document that were detected
	private ArrayList<String> candidateTokensList = new ArrayList<String>();

	// the ith token currently analyzed
	private int currentI = 0;
	
	private final boolean keepOverlappingTerms;
	
	public TreeLocation(INode rootNode, boolean keepOverlappingTerms) {
		this.rootNode = rootNode;
		this.keepOverlappingTerms = keepOverlappingTerms;
		setInitialState();
	}

	public void setInitialState() {
		this.states = new HashSet<INode>();
		this.states.add(rootNode);
		candidateTokensList = new ArrayList<String>();
	}

	public void searchNextStates(TNoutput tnoutput, String token, ISynonym fuzzyAlgorithms) {
		states = nextStates(token, fuzzyAlgorithms);
		if (pathFound(states)) {
			candidateTokensList.add(token);
			saveTermIfAnyFinalState(tnoutput, states);
			nextToken();
			return;
		} 
		
		if (isTheInitialState()) {
			nextToken(); // no path found at initial state => try to find a path at the next token
		} 
		
		// currently in another state=> go to the initial state and 
		setInitialState();
		tokenAtInitialState = getCurrentI();
		// nextToken() is not called to try to find a path with the same token at the initial state
	}
	
	// stopword: if the algorithm is in the initial state ignores it, otherwise add it.  
	public void addStopword(String token) {
		if (isTheInitialState()) {
			nextToken();
			tokenAtInitialState = getCurrentI();
		} else {
			candidateTokensList.add(token);
			nextToken();
		}
	}
	
	// From the current states, try to find new states given a token and a set of string mapping functions
	private Set<INode> nextStates(String token, ISynonym fuzzyAlgorithms) {
		Set<INode> nextStates = new HashSet<INode>();
		Set<List<String>> currentSynonyms = fuzzyAlgorithms.getSynonyms(token);
		for (INode currentState : states) {
			nextStates.addAll(currentState.gotoNodes(currentSynonyms));
		}
		return(nextStates);
	}

	private void saveTermIfAnyFinalState(TNoutput tnoutput, Set<INode> nextStates) {
		for (INode state : nextStates) {
			if (!state.isAfinalState()) { // only final states contain a term of the terminology
				continue;
			}
			Term term =  state.getTerm();
			int tokenStartPosition = tokenAtInitialState;
			int tokenEndPosition = getCurrentI();
			int startPosition = tnoutput.getTokenStartEndInSentence()[tokenStartPosition][0];
			int endPosition = tnoutput.getTokenStartEndInSentence()[tokenEndPosition][1]; // 
			String candidateTermString = tnoutput.getOriginalSentence().substring(startPosition, endPosition + 1); 

			String[] candidateTokenArray = candidateTokensList.toArray(new String[candidateTokensList.size()]);
			CTcode candidateTerm = new CTcode(candidateTermString, 
					candidateTokenArray,
					startPosition, 
					endPosition,
					term,
					tokenStartPosition,
					tokenEndPosition);
			addCandidateTerm(candidateTerm);
		}
	}

	private void addCandidateTerm(CTcode candidateTerm) {
		while (iNeed2removeLastCT(candidateTerm)) {
			ctDetected.remove(ctDetected.size() - 1); // remove the latest
		}
		ctDetected.add(candidateTerm);
	}
	
	private boolean iNeed2removeLastCT(CTcode candidateTerm) {
		if (keepOverlappingTerms) {
			return(false);
		}
		// We eventually need to remove the lastCT because IAMsystem keeps only the longest CT
		// from a CT, it's possible to retrieve all the terms that are prefix of this CT
		// We check if the previous CT has the same start but not the same end position
		if (ctDetected.isEmpty()) {
			return(false);
		}
		CTcode lastCT = ctDetected.get(ctDetected.size() - 1);
		if (lastCT.getStartPosition() == candidateTerm.getStartPosition() && 
				lastCT.getEndPosition() < candidateTerm.getEndPosition()) {
			return true;
		} else {
			return(false);
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
		return ctDetected;
	}

	public int getCurrentI() {
		return currentI;
	}

	public void setCurrentI(int currentI) {
		this.currentI = currentI;
	}
}
