package fr.erias.IAMsystem.terminology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.ct.CT;
import fr.erias.IAMsystem.exceptions.InvalidCSV;
import fr.erias.IAMsystem.exceptions.ProcessSentenceException;
import fr.erias.IAMsystem.load.Loader;
import fr.erias.IAMsystem.normalizer.IStopwords;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;

/**
 * An instance to normalize a terminology in a CSV format
 * @author Cossin Sebastien (cossin.sebastien@gmail.com)
 *
 */
public class CSVlineHandlerImpl implements ICSVlineHandler {

	final static Logger logger = LoggerFactory.getLogger(CSVlineHandlerImpl.class);

	private TokenizerNormalizer tokenizerNormalizer;

	private short positionOfLabelInColumn;

	private String sep;

	private String normalizedLine ;

	/**
	 * Create an instance to normalize a terminology in a CSV format
	 * @param stopwords An instance of {@link IStopwords}
	 * @param sep CSV column separator
	 * @param positionOfLabelInColumn the position of the column containing the label to normalise
	 */
	public CSVlineHandlerImpl (IStopwords stopwords, String sep, short positionOfLabelInColumn) {
		this.sep = sep;
		this.positionOfLabelInColumn = positionOfLabelInColumn;
		this.tokenizerNormalizer = Loader.getTokenizerNormalizer(stopwords);
	}

	/**
	 * See the interface
	 */
	public void processLine(String line) throws InvalidCSV, ProcessSentenceException {
		String[] columns = line.split(this.sep);
		if (columns.length < positionOfLabelInColumn) {
			throw new InvalidCSV(logger,"Unexpected number of columns at line");
		}

		// the term to process
		String label = columns[positionOfLabelInColumn];

		StringBuilder sb = new StringBuilder();
		sb.append(line);

		// normalizedTerm :
		
		// remove first and last quote
		label = label.replaceAll("^\"", "");
		label = label.replaceAll("\"$", "");
		String normalizedTerm = this.tokenizerNormalizer.getNormalizer().getNormalizedSentence(label);
		String[] tokensArray = this.tokenizerNormalizer.getTokenizer().tokenize(normalizedTerm);
		String[] newTokensArray = Loader.removeStopWords(this.tokenizerNormalizer.getNormalizer().getStopwords(), tokensArray);
		normalizedTerm = CT.arrayToString(newTokensArray, " ".charAt(0));
		normalizedTerm = normalizedTerm.trim();
		if (normalizedTerm.equals("")) {
			normalizedTerm = "nothingRemains";
			logger.info(label + " \t is a stopword - nothing remains of this label");
		}
		
		sb.append(sep);
		sb.append(normalizedTerm);
		sb.append("\n");
		this.normalizedLine = sb.toString();
	}

	/**
	 * See the interface
	 */
	public String getNormalizedLine() {
		return(this.normalizedLine);
	}
}
