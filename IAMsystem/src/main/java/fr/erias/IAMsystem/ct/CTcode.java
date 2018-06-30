package fr.erias.IAMsystem.ct;

import org.json.JSONObject;

/**
 * Just a {@link CandidateTerm} with a code/uri associated
 * @author Cossin Sebastien
 *
 */
public class CTcode extends CT {

	private String code ;
	
	/**
	 * Create a new candidateTerm with a code associated to it
	 * @param candidateTermString The candidate term string as it is in the sentence
	 * @param candidateTokensArray An array containing each token of candidateTermString
	 * @param startPosition The start position of this candidate term in the sentence
	 * @param endPosition The end position of this candidate term in the sentence
	 * @param code the candidateTerm comes from a terminology, it must have a code or uri
	 */
	public CTcode(String candidateTermString, String[] candidateTokensArray, 
			int startPosition, int endPosition, String code) {
		super(candidateTermString, candidateTokensArray, startPosition, endPosition);
		this.code = code;
	}
	
	/**
	 * Create a new candidateTerm with a code associated to it
	 * @param candidateTerm A {@link CandidateTerm}
	 * @param code The candidateTerm comes from a terminology, it must have a code or uri
	 */
	public CTcode(CT candidateTerm, String code) {
		super(candidateTerm);
		this.code = code;
	}
	
	/**
	 * Get the code of the {@link CandidateTerm}
	 * @return the code / uri associated to this candidateTerm in a terminology
	 */
	public String getCode() {
		return(code);
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
		return(json);
	}
}
