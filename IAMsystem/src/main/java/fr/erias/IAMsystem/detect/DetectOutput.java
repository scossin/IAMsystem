package fr.erias.IAMsystem.detect;

import java.util.TreeSet;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.tokenizernormalizer.TNoutput;

public class DetectOutput {
	
	/**
	 * tokenizer normalizer output
	 */
	private TNoutput tnoutput;
	
	
	/**
	 * dictionary entries detected
	 */
	private TreeSet<CTcode> candidateTermsCode;

	public DetectOutput(TNoutput tnoutput, TreeSet<CTcode> candidateTermsCode) {
		this.tnoutput = tnoutput;
		this.candidateTermsCode = candidateTermsCode;
	}

	/**
	 * Get dictionary entries detected
	 * @return A set of dictionary entries detected stored in a {@link CTcode}
	 */
	public TreeSet<CTcode> getCTcodes(){
		return(candidateTermsCode);
	}
	
	/**
	 * Retrieve tokenizer normalizer output
	 * @return
	 */
	public TNoutput getTNoutput() {
		return(this.tnoutput);
	}
	
}
