package fr.erias.IAMsystem.ct;

import org.json.JSONObject;

/**
 * Just a {@link CT} with a code/uri associated
 * 
 * @author Cossin Sebastien
 *
 */
public class CTcode extends CT {

	private String code ;
	
	private String label;
	
	private String termino = "";
	
	/**
	 * Create a new candidateTerm with a code associated to it
	 * @param candidateTermString The candidate term string as it is in the sentence
	 * @param candidateTokensArray An array containing each token of candidateTermString
	 * @param startPosition The start position of this candidate term in the sentence
	 * @param endPosition The end position of this candidate term in the sentence
	 * @param code the candidateTerm comes from a terminology, it must have a code or uri
	 * @param label the candidateTerm comes from a terminology, it must have a label
	 */
	public CTcode(String candidateTermString, String[] candidateTokensArray, 
			int startPosition, int endPosition, String code, String label) {
		super(candidateTermString, candidateTokensArray, startPosition, endPosition);
		this.code = code;
		this.label = label;
	}
	
	/**
	 * Create a new candidateTerm with a code associated to it
	 * @param candidateTerm A {@link CT}
	 * @param code The candidateTerm comes from a terminology, it must have a code or uri
	 */
	public CTcode(CT candidateTerm, String code) {
		super(candidateTerm);
		this.code = code;
	}
	
	/**
	 * Get the code of the {@link CT}
	 * @return the code / uri associated to this candidateTerm in a terminology
	 */
	public String getCode() {
		return(code);
	}
	
	/**
	 * Get the label of the {@link CT}
	 * @return the label associated to this candidateTerm in a terminology
	 */
	public String getLabel() {
		return(label);
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
		json.put("termino", termino);
		return(json);
	}

	/**
	 * 
	 * @return the name of the terminology
	 */
	public String getTermino() {
		return termino;
	}

	/**
	 * Set the name of the terminology
	 * @param termino name of the terminology
	 */
	public void setTermino(String termino) {
		this.termino = termino;
	}
}
