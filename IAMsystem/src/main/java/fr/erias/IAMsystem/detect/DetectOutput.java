package fr.erias.IAMsystem.detect;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.tokenizernormalizer.TNoutput;

/**
 * The output class of {@link DetectDictionaryEntry}
 * @author Cossin Sebastien
 *
 */
public class DetectOutput {
	
	/**
	 * tokenizer normalizer output
	 */
	private TNoutput tnoutput;
	
	
	/**
	 * dictionary entries detected
	 */
	private Collection<CTcode> candidateTermsCode;

	public DetectOutput(TNoutput tnoutput, Collection<CTcode> candidateTermsCode) {
		this.tnoutput = tnoutput;
		this.candidateTermsCode = candidateTermsCode;
	}

	/**
	 * Get dictionary entries detected
	 * @return A set of dictionary entries detected stored in a {@link CTcode}
	 */
	public Collection<CTcode> getCTcodes(){
		return(candidateTermsCode);
	}
	
	/**
	 * Retrieve tokenizer normalizer output
	 * @return {@link TNoutput} tokenizer/normalizer output
	 */
	public TNoutput getTNoutput() {
		return(this.tnoutput);
	}
	
	/**
	 * A JSON representation of the detection
	 * @return a JSONobject
	 */
	public JSONObject getJSONObject() {
		JSONObject output = new JSONObject();
		output.put("tnoutut", tnoutput.getJSONobject());
		JSONArray jsonArray = new JSONArray();
		for (CTcode ct : getCTcodes()) {
			jsonArray.put(ct.getJSONobject());
		}
		output.put("ct", jsonArray);
		return(output);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getCTcodes().size() + " terms detected.");
		sb.append("\n");
		int count = 1;
		for (CTcode ct : this.getCTcodes()) {
			sb.append("term number " + count + ":");
			sb.append("\n");
			sb.append(ct.toString());
			count = count + 1;
		}
		return(sb.toString());
	}
}
