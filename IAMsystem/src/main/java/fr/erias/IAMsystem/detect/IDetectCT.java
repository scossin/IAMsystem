package fr.erias.IAMsystem.detect;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.tokenizernormalizer.TNoutput;

public interface IDetectCT {
	
	/**
	 * Initialize a new sentence
	 * @param document The document to analyze
	 * @return an instance of {@link DetectOutput} containing {@link CTcode} and {@link TNoutput}
	 */
	public DetectOutput detectCandidateTerm(String document);
	
}
