package fr.erias.IAMsystemFR.synonyms;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;

/**
 * Load a sense inventory of French Abbreviations
 * Abbreviations extracted from: https://github.com/scossin/abbreviations
 * 
 * @author Sebastien Cossin
 *
 */
public class MedicalAbbsFR {
	private static final String FILENAME = "sense_inventory.tsv";
	private static final String SEP = "\t";
	private static final int SHORT_FORM_COL = 0;
	private static final int LONG_FORM_COL = 1;
	
	/**
	 * Load the sense inventory of French Medical Abbreviations
	 * @param shortForm2ignore a Set of shortForms (string) that you don't want to load
	 * @return a {@link ISynonym} that uses the Abbreviations method
	 * @throws IOException
	 */
	public static Abbreviations getSenseInventory(Set<String> shortForm2ignore) throws IOException {
		Abbreviations abbreviations = new Abbreviations();
		
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILENAME);
		Terminology terminology = new Terminology(in, SEP, SHORT_FORM_COL, LONG_FORM_COL);
		for (Term term : terminology.getTerms()) {
			String shortForm = term.getLabel();
			if (shortForm2ignore.contains(shortForm)) {
				continue;
			}
			String longForm = term.getCode();
			abbreviations.addAbbreviation(longForm, shortForm);
		}
		System.out.println(terminology.getTerms().size());
		in.close();
		return(abbreviations);
	}
}
