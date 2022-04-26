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
	private final String label;
	
	/**
	 * the normalizedLabel with a {@link INormalizer} or equal to the label (default). Ex: "insuffisance cardiaque")
	 */
	private String normalizedLabel;
	
	/**
	 * the code (ex: I50)
	 */
	private final String code;
	
	/**
	 * the name of the terminology (ex: ICD-10)
	 * default "" for none
	 */
	private String termino = "";

	/**
	 * Create a new Term
	 * @param label the label of a terminology
	 * @param code the code
	 * @param termino the terminology name
	 * @param normalizer a {@link INormalizer} to normalize the label
	 */
	public Term (String label, String code, String termino, INormalizer normalizer) {
		this.label = label;
		this.code = code;
		this.termino = termino;
		this.normalizedLabel = normalizer.getNormalizedSentence(label);
	}
	
	/**
	 * Create a new Term (no termino / no normalization)
	 * @param label the label of a terminology
	 * @param code the code 
	 */
	public Term (String label, String code) {
		this(label, code, "", INormalizer.noNormalizer);
	}
	
	/**
	 * Create a new Term
	 * @param label the label of a terminology
	 * @param code the code 
	 * @param normalizer a {@link INormalizer} to normalize the label
	 */
	public Term(String label, String code, INormalizer normalizer) {
		this(label, code, "", normalizer);
	}

	public String getLabel() {
		return label;
	}

	public String getCode() {
		return code;
	}

	public String getNormalizedLabel() {
		return normalizedLabel;
	}

	public void setNormalizedLabel(String normalizedLabel) {
		this.normalizedLabel = normalizedLabel;
	}

	public String getTermino() {
		return termino;
	}

	public void setTermino(String termino) {
		this.termino = termino;
	}
}
