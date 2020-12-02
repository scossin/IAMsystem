package fr.erias.IAMsystem.ct;

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.tokenizer.ITokenizer;

/**
 * This class represents a candidate term : a substring in a sentence with a start and endposition
 * @author Cossin Sebastien (cossin.sebastien@gmail.com)
 *
 */

public class CT implements Comparable<CT> {

	final static Logger logger = LoggerFactory.getLogger(CT.class);
	
	/**
	 * The candidate term as it is in the sentence 
	 */
	private String candidateTermString;
	
	/**
	 * startPosition in the sentence
	 */
	private int startPosition;

	/**
	 * endPosition in the sentence
	 */
	private int endPosition;

	/**
	 * An array of tokens of the candidateTerm
	 */
	private String[] candidateTokensArray;

	/**
	 * Constructor. This one is used after processing a sentence
	 * @param candidateTermString The candidate term string as it is in the sentence
	 * @param candidateTokensArray An array containing each token of candidateTermString
	 * @param startPosition The start position of this candidate term in the sentence
	 * @param endPosition The end position of this candidate term in the sentence
	 */
	public CT(String candidateTermString, String[] candidateTokensArray, int startPosition, int endPosition){
		this.candidateTermString = candidateTermString;
		this.candidateTokensArray = candidateTokensArray;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		//debugInitialization();
	}

	/**
	 * Constructor. This one is used for subclasses
	 * @param candidateTerm A candidateTerm
	 */
	public CT(CT candidateTerm) {
		this.candidateTermString = candidateTerm.getCandidateTermString();
		this.candidateTokensArray = candidateTerm.getCandidateTokensArray();
		this.startPosition = candidateTerm.getStartPosition();
		this.endPosition = candidateTerm.getEndPosition();
	}
	
	/******************************************** Getters **************************************/
	
	/**
	 * 
	 * @return The CandidateTerm as it is in the sentence/string  (ex : ulceres  (multispace) gastriques)
	 */
	public String getCandidateTermString(){
		return(candidateTermString);
	}
	
	/**
	 * 
	 * @return The candidate term after tokenization (ex : ulceres gastriques)
	 */
	public String getCandidateTerm(){
		return(ITokenizer.arrayToString(candidateTokensArray, " ".charAt(0)));
	}
	
	/**
	 * Candidate term is a set of token initially
	 * @return The candidate term in an array
	 */
	public String[] getCandidateTokensArray(){
		return(candidateTokensArray);
	}
	
	/**
	 * 
	 * @return startPosition in the sentence
	 */
	public int getStartPosition() {
		return(startPosition);
	}
	
	/**
	 * 
	 * @return endPosition in the sentence
	 */
	public int getEndPosition() {
		return(endPosition);
	}
	
	
	/**
	 * In order to use a sort CandidateTerm with a TreeSet
	 */
	@Override
	public int compareTo(CT otherCandidateTerm) {
		int diffStart = this.startPosition - otherCandidateTerm.startPosition;
		if (diffStart != 0) { // order by diffStart if it doesn't start at the same start Position
			return(diffStart); 
		}
		int endStart = this.endPosition - otherCandidateTerm.endPosition; // otherwise order by endPosition
		return (endStart);
	}
	
	/**
	 * The number of char of the candidateTerm (as it is in the sentence)
	 * @return The differennce between endPosition and startPosition
	 */
	public int getDiffEndStart() {
		return(this.endPosition - this.startPosition);
	}
	
	
	/**
	 * Iterate over an ordered list of candidateTerms and keep the longest ones
	 * @param candidateTermsOrdered An ordered List of candidateTerms
	 * @return a subset of the initial set ; with overlap removed
	 */
	public static HashSet<CT> removeOverlap(TreeSet<CT> candidateTermsOrdered){
		// only potentialCandidate can move to outputSet
		// if a potentialCandidate "survives" test with currentCandidate, it moves to the outputSet
		
		HashSet<CT> outputSet = new HashSet<CT>();
		
		Iterator<CT> iter = candidateTermsOrdered.iterator();
		
		CT potentialCandidate = null;
		if (iter.hasNext()) {
			potentialCandidate = iter.next(); // first iteration
		}
		
		while(iter.hasNext()) {
			CT currentCandidateTerm = iter.next();
			
			// case same startPosition
			if (currentCandidateTerm.getStartPosition() == potentialCandidate.getStartPosition()) {
				potentialCandidate = currentCandidateTerm ; 
				// because the order is based on endPosition, the current has endPosition > than the previous one.
				// Keep the longest CandidateTerm. The potentialCandidate looses its place and will not be in the outputSet
				continue;
			}
			
			// case no overlap
			if (currentCandidateTerm.getStartPosition() > potentialCandidate.getEndPosition()) {
				// potentialCandidate survived previous test !
				// it deserves the right to go to the outputSet
				outputSet.add(potentialCandidate);
				potentialCandidate = currentCandidateTerm;
			}
			
			// overlap case
			if (currentCandidateTerm.getDiffEndStart() > potentialCandidate.getDiffEndStart()) {
				// well... the currentCandidateTerm can take the place only if it's longer
				// same length : sorry, it's not fair but we don't want overlap so, only one can survive
				potentialCandidate = currentCandidateTerm;
			}
		}
		
		// the last potential candidate "survives" and deserves the right to go to the outputSet :
		if (potentialCandidate != null) {
			outputSet.add(potentialCandidate);
		}
		return(outputSet);
	}
	
	/**
	 * Debugging purpose only
	 */
	public void debugInitialization(){
		logger.debug("New candidateTerm");
		logger.debug("\t New candidate term : " + candidateTermString);
		logger.debug("\t Starting at : " + startPosition);
		logger.debug("\t Ending at : " + endPosition);
		logger.debug("\t Tokens : " + getCandidateTerm());
	}
}
