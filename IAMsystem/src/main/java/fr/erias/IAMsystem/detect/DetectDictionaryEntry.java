package fr.erias.IAMsystem.detect;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
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

	final static Logger logger = LoggerFactory.getLogger(DetectDictionaryEntry.class);

	/**
	 * dictionary entry found = a {@link CandidateTerm} with a code
	 */
	private TreeSet<CTcode> candidateTermsCode = new TreeSet<CTcode>();

	/**
	 * The initial terminology in a tree datastructure
	 */
	private SetTokenTree setTokenTree = null;

	/**
	 * The current location in the tree - the algorithm explores the tree
	 */
	private SetTokenTree tempSetTokenTree = null;

	/**
	 * The current candidate - an array of tokens
	 * Initialization : array of 50 tokens maximum ! No dictionary entry can have more than 50 tokens 
	 * Improvement : set to the max depth of the tree
	 */
	private String[] candidateTokensArray = new String[50];

	/**
	 * Current number of tokens 
	 */
	private int candidateTokenLength = 0;

	/**
	 * The algorithm is currently checking a candidate
	 */
	private boolean currentCandidate = false;

	/**
	 * A set of synonyms (typos and abbreviations) for the current tokens
	 */
	private HashSet<String[]> currentSynonyms = new HashSet<String[]>();

	/**
	 * If the current token is found in the dictionary
	 */
	private boolean currentFound = false;

	/**
	 * the ith token currently analyzed
	 */
	private int currentI = 0;

	/**
	 * Normalization and tokenization of the sentence
	 */
	private TokenizerNormalizer tokenizerNormalizer;

	/**
	 * Set of abbreviations
	 */
	private HashSet<Synonym> synonyms = new HashSet<Synonym>();

	/**
	 * 
	 * @param setTokenTree A terminology stored in a tree datastructure. See {@link SetTokenTree}
	 * @param tokenizer to normalize and tokenize terms in the sentence
	 * @param synonyms For each token, find synonym tokens (ex : abbreviations or typos or real synonym). See the inferface : {@link Synonym}
	 * @throws IOException
	 */
	public DetectDictionaryEntry(SetTokenTree setTokenTree,TokenizerNormalizer tokenizer, HashSet<Synonym> synonyms) throws IOException {
		this.setTokenTree = setTokenTree;
		this.tempSetTokenTree = setTokenTree;
		this.tokenizerNormalizer = tokenizer ;
		this.synonyms = synonyms;
	}

	/**
	 * Initialize a new sentence
	 * @param sentence The sentence to analyze
	 * @throws UnfoundTokenInSentence 
	 */
	public void detectCandidateTerm(String sentence) throws IOException, ParseException, UnfoundTokenInSentence {
		// re-initialize :
		this.currentI = 0;
		candidateTermsCode.clear();
		reset();

		// normalize, tokenize and detect :
		tokenizerNormalizer.tokenize(sentence);
		String[] tokensArray = tokenizerNormalizer.getTokens();
		while (currentI != tokensArray.length) {
			logger.debug(" i : " + currentI);
			String token =  tokensArray[currentI];
			logger.debug("token = " + token);
			setCurrentSynonyms(); // search synonyms (abbreviations, typos...)
			setCurrentCandidate();
			this.currentI = currentI + 1;
		}
		
		// for the last token when the end of the sentence is reached
		if (currentCandidate) { // if the algorithm is currently exploring a candidate term :
			addCandidateTerm();
		}
	}

	/**
	 * Get dictionary entries detected
	 * @return A set of dictionary entries detected stored in a {@link CandidateTerm}
	 */
	public TreeSet<CTcode> getCTcode(){
		return(candidateTermsCode);
	}

	/**
	 * Re-initialize the algorithm for the next candidateTerm
	 */
	private void reset() {
		this.candidateTokensArray = new String[50]; // empty the array of tokens
		this.candidateTokenLength = 0; // Number of current tokens in the current candidate
		this.currentCandidate = false; // the algorithm is not checking a current candidate
		this.tempSetTokenTree = this.setTokenTree; // the current location of the algorithm in the tree 
	}

	
	/**
	 * Find synonyms (typos or abbreviations) for the current token
	 * @throws IOException Error with the Lucene Index
	 * @throws ParseException Error with the Lucene Index
	 */
	private void setCurrentSynonyms() throws IOException, ParseException {
		String token = getCurrentToken();
		logger.debug(" token : " + token);

		// perfect match :
		currentFound = tempSetTokenTree.containToken(token); 

		// find synonyms (typos and abbreviations) :
		currentSynonyms = new HashSet<String[]>(); // reinitializing synonyms
		// add the token to currentSynonyms (will be used later)
		String[] tokenInArray = {token};
		currentSynonyms.add(tokenInArray);
		
		for (Synonym synonym : synonyms) {
			currentSynonyms.addAll(synonym.getSynonyms(token)); // ex : typos and abbreviations
		}
		
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
	}

	/**
	 * Add a dictionary entry
	 * @throws IOException
	 * @throws ParseException
	 * @throws UnfoundTokenInSentence
	 */
	private void setCurrentCandidate() throws IOException, ParseException, UnfoundTokenInSentence {
		logger.debug("setCurrentCandidate function");
		// case token is found
		if (currentFound) { 
			// add the token
			logger.debug(" \t token found");
			// the algorithm change its current location in the tree : 
			tempSetTokenTree = tempSetTokenTree.getSetTokenTree(currentSynonyms); // currentSynonyms may contain : token (perfect match), abbreviations and corrected typos
			logger.debug("size of tempSetTokens : " + tempSetTokenTree.getMapTokenTree().size());
			// add the token to the array of tokens :
			candidateTokensArray[candidateTokenLength] = tokenizerNormalizer.getTokens()[currentI];
			candidateTokenLength = candidateTokenLength + 1 ; // a new token was added, add 1
			// the algorithm is currently exploring the tree to find a dictionary entry
			currentCandidate = true;
		}

		// case token is not found
		else {
			logger.debug(" \t token not found");
			
			// case not currently exploring the tree, no previous token was detected : nothing to do. => Next token
			if (!currentCandidate) {
				return; // 
			}
			
			// case exploring the tree :
			
			    // case stopwords : add and continue 
			if (tokenizerNormalizer.getNormalizerTerm().isStopWord((getCurrentToken()))) {
				logger.debug(" \t stopword detected");
				// add the stopword to the array of tokens :
				candidateTokensArray[candidateTokenLength] = tokenizerNormalizer.getTokens()[currentI];
				candidateTokenLength = candidateTokenLength + 1 ;
				return;
			}
			
			// case end of a candidate term : 

			logger.debug(" \t adding CandidateTerm");
			addCandidateTerm();
			
			// reset the algorithm to detect the next candidate term : 
			reset();
			// restart with the current token at the root of the tree 
			this.currentI = this.currentI - 1; 
		}
	}

	/**
	 * @return The current token to analyze
	 */
	private String getCurrentToken() {
		return(tokenizerNormalizer.getTokens()[currentI]);
	}

	/**
	 * Get one code for an array of tokens. The algorithm will search into the tree this sequence of tokens <br>
	 * A code is saved each time a sequence is matched in the tree
	 * @param candidateTokensArray A sequence of tokens in an array of String. First index = root of the tree
	 * @return null if no code is found for any sequence (one to the number of tokens given)
	 */
	public String getPreviousCode(String[] candidateTokensArray) {
		String code = null;
		SetTokenTree tempTokenTree = this.setTokenTree;
		for (String candidate : candidateTokensArray) {
			logger.debug("candidate : " + candidate);
			tempTokenTree = tempTokenTree.getSetTokenTree(candidate);
			logger.debug("number of possibilities : " + tempTokenTree.getMapTokenTree().size());
			String codetemp = tempTokenTree.getOneCode();
			if (codetemp != null) {
				logger.debug("The following code was detected for " + candidate + ": " + codetemp);
				code = codetemp;
			}
		}
		return(code);
	}

	/**
	 * Add a candidateTerm to the set of CT detected if a code is found
	 * @throws UnfoundTokenInSentence 
	 */
	private void addCandidateTerm() throws UnfoundTokenInSentence {

		// current position 
		int iter = currentI ;

		// initialization is 50
		// current candidateTokensArray goes from 0 to the candidateToken length
		candidateTokensArray = Arrays.copyOfRange(candidateTokensArray, 0, 
				candidateTokenLength);

		// debugging purpose only
		for (String candidate : candidateTokensArray) {
			logger.debug("candidate : " + candidate);
		}


		// is a code associated to this candidateTerm ?
		String code = tempSetTokenTree.getOneCode();
		if (code == null) {
			// code is null but previous tokens of this candidateTerm may have a code
			// for Example : Abces chambre anterieure oeil gauche ; abces has a code, but abces chambre doesn't
			// so we go back to abces
			logger.debug("code is null : ");
			logger.debug("Trying with previous code : ");

			// if no previous token
			if (!tempSetTokenTree.getMapTokenTree().values().iterator().hasNext()) {
				logger.debug("no previous token, code is null again");
				return;
			}

			// retrieve the previous tokens and search, for each previous sequence, a code 
			TokenTree oneTokenTree = tempSetTokenTree.getMapTokenTree().values().iterator().next().iterator().next();
			logger.debug("depth : " + oneTokenTree.getDepth());
			logger.debug("token : " + oneTokenTree.getToken());
			code = getPreviousCode(oneTokenTree.getPreviousTokens());
			if (code == null) {
				logger.debug("code is null again");
				this.currentI = (currentI - candidateTokenLength) + 1 ; // i => i + 1 ; without the first token
				return; // return
			} else {
				logger.debug("a previous code was found");
				this.currentI = (currentI - candidateTokenLength) + 1 ; // i => i + 1 ; without the first token
			}
			// restart after the first token of this candidate
		}

		// A code is found ! 
		logger.debug("code is : " + code);
		int numberOfTokens2remove = 0 ; // last token can be a stopword. We need to remove it : 
		for (int y = candidateTokenLength -1; y>0;y--) {
			String lastToken = candidateTokensArray[y];
			if (tokenizerNormalizer.getNormalizerTerm().isStopWord(lastToken)){
				logger.debug("last token is a stopword : " + lastToken);
				numberOfTokens2remove = numberOfTokens2remove + 1;
			} else {
				break;
			}
		}

		candidateTokensArray = Arrays.copyOfRange(candidateTokensArray, 0, 
				candidateTokenLength - numberOfTokens2remove);

		for (String candidate : candidateTokensArray) {
			logger.debug("new candidate is " + candidate);
		}

		// startPosition and endPosition in the sentence :
		int tokenStartPosition = iter - candidateTokenLength;
		int startPosition = tokenizerNormalizer.getTokenStartEndInSentence()[tokenStartPosition][0];
		int endPosition = tokenizerNormalizer.getTokenStartEndInSentence()[iter-(numberOfTokens2remove + 1 )][1]; // -1 : previous one
		String candidateTermString = tokenizerNormalizer.getNormalizerTerm().getOriginalSentence().substring(startPosition, endPosition + 1); 

		logger.debug("CandidateTermString : " + candidateTermString);
		CTcode candidateTerm = new CTcode(candidateTermString, 
				candidateTokensArray, 
				startPosition, 
				endPosition,
				code);
		candidateTermsCode.add(candidateTerm);
	}
}
