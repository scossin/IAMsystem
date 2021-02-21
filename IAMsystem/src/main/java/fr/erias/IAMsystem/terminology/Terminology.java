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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;

/**
 * This class represents a terminology (a set of terms)
 * The terminology is then compressed in a tree data structure with SetTokenTree
 * 
 * @author Cossin Sebastien
 */
public class Terminology {
	
	final static Logger logger = LoggerFactory.getLogger(Terminology.class);
	
	/**
	 * HashSet of terms
	 */
	private HashSet<Term> terms = new HashSet<Term>();
	

	/**
	 * Default constructor
	 */
	public Terminology() {
		
	}
	
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
	 * @param label the label (not normalized, see other function signature to normalize it)
	 * @param code the code
	 */
	public void addTerm(String label, String code) {
		Term term = new Term(label, code);
		terms.add(term);
	}
	
	/**
	 * Create a terminology object from a CSV file
	 * @param in The inputstream of the CSV file
	 * @param sep the separator of the CSV file (ex : "\t")
	 * @param colLabel the ith column containing the libnormal (normalized label of the term)
	 * @param colCode the ith column containing the terminology code
	 * @param normalizer a {@link INormalizer} to normalize the terms of the terminology
	 * @throws IOException inputstream error
	 */
	public Terminology(InputStream in, String sep, int colLabel, int colCode, INormalizer normalizer) throws IOException {
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		while ((line = br.readLine()) != null) {
			String[] columns = line.split(sep);
			String label = columns[colLabel];
			String code = columns[colCode];
			addTerm(label, code, normalizer);
		}
		br.close();
		logger.info("terminology size : " + terms.size());
	}
	
	/**
	 * Create a terminology object from a CSV file
	 * @param csvFile A CSV {@link java.io.File}  
	 * @param sep the separator of the CSV file (ex : "\t")
	 * @param colLabel the ith column containing the libnormal (normalized label of the term)
	 * @param colCode the ith column containing the terminology code
	 * @param normalizer a {@link INormalizer} to normalize the terms of the terminology
	 * @throws IOException If the file doesn't exist
	 */
	public Terminology(File csvFile, String sep, int colLabel, int colCode, INormalizer normalizer) throws IOException {
		this(new FileInputStream(csvFile), sep, colLabel, colCode,normalizer);
	}
	
	/**
	 * Create a terminology object from a CSV file without normalizing
	 * @param in The inputstream of the CSV file
	 * @param sep the separator of the CSV file (ex : "\t")
	 * @param colLabel the ith column containing the libnormal (normalized label of the term)
	 * @param colCode the ith column containing the terminology code
	 * @throws IOException inputstream error
	 */
	public Terminology(InputStream in, String sep, int colLabel, int colCode) throws IOException {
		this(in, sep, colLabel, colCode, new INormalizer() {
			
			@Override
			public IStopwords getStopwords() {
				return new StopwordsImpl();
			}
			
			@Override
			public String getNormalizedSentence(String sentence) { // don't normalize
				return sentence;
			}

			@Override
			public void setStopwords(IStopwords stopwords) {
				// TODO Auto-generated method stub
				
			}
		});
	}
		
	/**
	 * Normalize the terminology 
	 * @param normalizer a {@link INormalizer} to normalize the terminology
	 */
	public void normalizeTerminology(INormalizer normalizer) {
		for (Term term : terms) {
			String label = term.getLabel();
			String normalLabel = normalizer.getNormalizedSentence(label);
			term.setLabel(normalLabel);
		}
	}
	
	/**
	 * Retrieve all the terms of a terminology
	 * @return the set of terms
	 */
	public HashSet<Term> getTerms() {
		return(this.terms);
	}
	
	/**
	 * write the terminology to a file
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
