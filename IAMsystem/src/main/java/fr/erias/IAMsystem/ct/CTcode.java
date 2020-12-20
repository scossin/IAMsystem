package fr.erias.IAMsystem.ct;

import org.json.JSONObject;

import fr.erias.IAMsystem.terminology.Term;

/**
 * Just a {@link CT} with a code/uri associated
 * 
 * @author Cossin Sebastien
 *
 */
public class CTcode extends CT implements Comparable<CTcode> {
	
	private final Term term;

	private final int tokenStartPosition;
	
	private final int tokenEndPosition;
	
	/**
	 * Create a new candidateTerm with a code associated to it
	 * @param candidateTermString The candidate term string as it is in the sentence
	 * @param candidateTokensArray An array containing each token of candidateTermString
	 * @param startPosition The start position of this candidate term in the sentence
	 * @param endPosition The end position of this candidate term in the sentence
	 * @param term a {@link Term} of a terminology
	 * @param tokenStartPosition the ith start position in the token array (after sentence tokenization)
	 * @param tokenEndPosition the ith end position in the token array (after sentence tokenization)
	 */
	public CTcode(String candidateTermString, String[] candidateTokensArray, 
			int startPosition, int endPosition, Term term, 
			int tokenStartPosition, int tokenEndPosition) {
		super(candidateTermString, candidateTokensArray, startPosition, endPosition);
		this.term = term;
		this.tokenStartPosition = tokenStartPosition;
		this.tokenEndPosition = tokenEndPosition;
	}
	
	/**
	 * Get the code of the {@link CT}
	 * @return the code / uri associated to this candidateTerm in a terminology
	 */
	public String getCode() {
		return(term.getCode());
	}
	
	/**
	 * Get the label of the {@link CT}
	 * @return the label associated to this candidateTerm in a terminology
	 */
	public String getLabel() {
		return(term.getLabel());
	}
	
	/**
	 * Export a candidateTerm to a JsonObject
	 * @return a JSON object representing this candidateTerm
	 */
	public JSONObject getJSONobject() {
		JSONObject json = new JSONObject();
		json.put("term", getCandidateTermString());
		json.put("normalizedTerm", getCandidateTerm());
		json.put("start", getStartPosition());
		json.put("end", getEndPosition());
		json.put("code", getCode());
		json.put("dictLabel", getLabel());
		json.put("termino", getTermino());
		json.put("tokenStartPosition", getTokenStartPosition());
		json.put("tokenEndPosition", getTokenEndPosition());
		return(json);
	}
	
	/**
	 * 
	 * @return the first token number
	 */
	public int getTokenStartPosition() {
		return(this.tokenStartPosition);
	}
	
	/**
	 * 
	 * @return the last token number
	 */
	
	public int getTokenEndPosition() {
		return(this.tokenEndPosition);
	}

	/**
	 * 
	 * @return the name of the terminology
	 */
	public String getTermino() {
		return term.getTermino();
	}

	/**
	 * In order to use a sort CandidateTerm with a TreeSet
	 */
	@Override
	public int compareTo(CTcode otherCandidateTerm) {
		int diffStart = this.getStartPosition() - otherCandidateTerm.getStartPosition();
		if (diffStart != 0) { // order by diffStart if it doesn't start at the same start Position
			return(diffStart); 
		}
		int diffEnd = this.getEndPosition() - otherCandidateTerm.getEndPosition();
		if (diffEnd != 0) { // order by diffStart if it doesn't start at the same start Position
			return(diffEnd); 
		}
		// if same start and end position it means the code is different
		int codeCompare = this.getCode().compareTo(otherCandidateTerm.getCode());
		return (codeCompare);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t label in terminology: '" + this.getLabel() + "'");
		sb.append("\n");
		sb.append("\t written exactly like this in the sentence: '" + this.getCandidateTermString() + "'");
		sb.append("\n");
		sb.append("\t code in terminology: " + this.getCode());
		sb.append("\n");
		sb.append("\t starting at position:" + this.getStartPosition());
		sb.append("\n");
		sb.append("\t end at position:" + this.getEndPosition());
		sb.append("\n");
		sb.append("\t first token number " + this.getTokenStartPosition());
		sb.append("\n");
		sb.append("\t last token number " + this.getTokenEndPosition());
		sb.append("\n");
		return(sb.toString());
	}
}
