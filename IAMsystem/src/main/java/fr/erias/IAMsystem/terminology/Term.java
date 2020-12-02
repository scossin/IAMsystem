package fr.erias.IAMsystem.terminology;

import fr.erias.IAMsystem.normalizer.INormalizer;

/**
 * This class represents a term in a terminology
 * @author Cossin Sebastien
 *
 */
public class Term {
	
	/**
	 * the label (ex: "Insuffisance Cardiaque")
	 */
	private String label;
	
	/**
	 * the normalizedLabel with a {@link INormalizer} or equal to the label (default). Ex: "insuffisance cardiaque")
	 */
	private String normalizedLabel;
	
	/**
	 * the code (ex: I50)
	 */
	private String code;
	
	/**
	 * Create a new Term
	 * @param label the label of a terminology
	 * @param code the code 
	 */
	public Term (String label, String code) {
		this.setLabel(label);
		this.setNormalizedLabel(label); // same by default before normalization
		this.setCode(code);
	}
	
	/**
	 * 
	 * @param label the label of a terminology
	 * @param code the code 
	 * @param normalizer a {@link INormalizer} to normalize the label
	 */
	public Term(String label, String code, INormalizer normalizer) {
		this(label, code);
		setNormalizedLabel(normalizer.getNormalizedSentence(label));
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNormalizedLabel() {
		return normalizedLabel;
	}

	public void setNormalizedLabel(String normalizedLabel) {
		this.normalizedLabel = normalizedLabel;
	}
}
