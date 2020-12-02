package fr.erias.IAMsystem.terminology;

import fr.erias.IAMsystem.exceptions.InvalidCSV;
import fr.erias.IAMsystem.exceptions.ProcessSentenceException;

/**
 * An interface to normalize a terminology in a CSV input format
 * @author Cossin Sebastien (cossin.sebastien@gmail.com)
 *
 */
public interface ICSVlineHandler {

	/**
	 * A method to process a line in the CSV to get a Normalized Line
	 * @param line A line in the CSV to handle
	 * @throws InvalidCSV If the number of columns is unexpected
	 * @throws ProcessSentenceException If the normalization process fails
	 */
	public void processLine(String line) throws InvalidCSV, ProcessSentenceException;
	
	/**
	 * Get normalized terms of the label added to the line in input
	 * @return normalized line : the same line in input + normalized forms of the label
	 */
	public String getNormalizedLine();
	
}
