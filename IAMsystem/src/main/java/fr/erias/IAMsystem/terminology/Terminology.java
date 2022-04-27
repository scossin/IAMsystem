package fr.erias.IAMsystem.terminology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.normalizer.NoNormalizer;

/**
 * This class represents a terminology (a set of terms)
 * 
 * @author Cossin Sebastien
 */
public class Terminology {
	
	private Set<Term> terms = new HashSet<Term>();
	
	public Terminology() {};
	
	/**
	 * Add a term to the terminology
	 * @param label  the label (will be normalized)
	 * @param code the code
	 * @param normalizer the normalize to normalize the label
	 */
	public void addTerm(String label, String code, INormalizer normalizer) {
		Term term = new Term(label, code, normalizer);
		terms.add(term);
	}
	
	/**
	 * Add a term to the terminology
	 * @param term a {@link Term}
	 */
	public void addTerm(Term term) {
		terms.add(term);
	}
	
	/**
	 * Add a term to the terminology
	 * @param label the label (not normalized, see other function signatures to normalize it)
	 * @param code the code of a concept in a terminology
	 */
	public void addTerm(String label, String code) {
		Term term = new Term(label, code);
		terms.add(term);
	}
	
	/**
	 * Load a terminology from a CSV file
	 * @param in inputstream of a CSV file
	 * @param sep the separator of the CSV file (ex : "\t")
	 * @param colLabel the ith column containing the libnormal (normalized label of the term)
	 * @param colCode the ith column containing the terminology code
	 * @param colTermino the ith column containing the terminology name
	 * @param normalizer a {@link INormalizer} to normalize the terms of the terminology
	 * @param header whether the file contains a header
	 * @throws IOException inputstream error
	 */
	public Terminology(InputStream in, String sep, int colLabel, int colCode, int colTermino, INormalizer normalizer, boolean header) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		if (header) br.readLine();
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] columns = line.split(sep);
			String label = removeQuotes(columns[colLabel]);
			String code = removeQuotes(columns[colCode]);
			String termino = getTerminoValue(columns,colTermino);
			Term term = new Term(label, code, termino, normalizer);
			addTerm(term);
		}
		br.close();
	}
	
	private String removeQuotes(String label) {
		label = label.replaceAll("^\"|\"$", "");
		return(label);
	}
	
	/**
	 * Load a terminology from a CSV file
	 * @param in The inputstream of the CSV file
	 * @param sep the separator of the CSV file (ex : "\t")
	 * @param colLabel the ith column containing the libnormal (normalized label of the term)
	 * @param colCode the ith column containing the terminology code
	 * @param normalizer a {@link INormalizer} to normalize the terms of the terminology
	 * @param header whether the file contains a header
	 * @throws IOException inputstream error
	 */
	public Terminology(InputStream in, String sep, int colLabel, int colCode, INormalizer normalizer, boolean header) throws IOException {
		// colTermino = -1 => no termino
		this(in, sep, colLabel, colCode, -1, normalizer, header);
	}
	
	private String getTerminoValue(String[] columns, int colTermino) {
		if (colTermino > 0) {
			return removeQuotes(columns[colTermino]);
		} else {
			return("");
		}
	}
	
	/**
	 * Create a terminology object from a CSV file
	 * @param csvFile A CSV {@link java.io.File}  
	 * @param sep the separator of the CSV file (ex : "\t")
	 * @param colLabel the ith column containing the libnormal (normalized label of the term)
	 * @param colCode the ith column containing the terminology code
	 * @param normalizer a {@link INormalizer} to normalize the terms of the terminology
	 * @param header whether the file contains a header
	 * @throws IOException If the file doesn't exist
	 */
	public Terminology(File csvFile, String sep, int colLabel, int colCode, INormalizer normalizer, boolean header) throws IOException {
		this(new FileInputStream(csvFile), sep, colLabel, colCode,normalizer, header);
	}
	
	/**
	 * Create a terminology object from a CSV file without normalizing
	 * @param in The inputstream of the CSV file
	 * @param sep the separator of the CSV file (ex : "\t")
	 * @param colLabel the ith column containing the libnormal (normalized label of the term)
	 * @param colCode the ith column containing the terminology code
	 * @param header whether the file contains a header
	 * @throws IOException inputstream error
	 */
	public Terminology(InputStream in, String sep, int colLabel, int colCode, boolean header) throws IOException {
		this(in, sep, colLabel, colCode, new NoNormalizer(), header);	
	}
		
	/**
	 * Normalize all the labels of the terms of this terminology 
	 * @param normalizer a {@link INormalizer} to normalize the terminology
	 */
	public void normalizeTerminology(INormalizer normalizer) {
		for (Term term : terms) {
			String label = term.getLabel();
			String normalLabel = normalizer.getNormalizedSentence(label);
			term.setNormalizedLabel(normalLabel);
		}
	}
	
	/**
	 * Retrieve all the terms of a terminology
	 * @return the set of terms
	 */
	public Set<Term> getTerms() {
		return(this.terms);
	}
	
	/**
	 * write the terminology to a file (APPEND to it)
	 * @param outputFile the output CSV file
	 * @param sep file separator
	 * @throws IOException fail to write to file
	 */
	public void writeTerminology2file(File outputFile, String sep) throws IOException {
		for (Term term : terms) {
			String newLine = term.getCode() + sep + term.getLabel() + sep + term.getNormalizedLabel();
			Files.write(Paths.get(outputFile.getAbsolutePath()),
					 newLine.getBytes(), StandardOpenOption.APPEND);
		}
	}
}
