package fr.erias.IAMsystem.normalizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.exceptions.InvalidCSV;
import fr.erias.IAMsystem.exceptions.ProcessSentenceException;


/**
 * Before indexing, the terms are normalized.
 * Because this process need to be checked, another file (normalized file) is created. <br>
 * So the indexation is done with 2 steps :
 * <ul>
 * <li> normalize each dictionary entry
 * <li> index the normalized entries
 * </ul>
 * @author Cossin Sebastien (cossin.sebastien@gmail.com)
 *
 */

public class NormalizeTerminology {

	final static Logger logger = LoggerFactory.getLogger(NormalizeTerminology.class);
	
	private File outputFile = null;

	private CSVlineHandler csvLineHandler;

	private boolean header;
	
	/**
	 * Normalize the labels of a terminology
	 * @param CSVfileToNormalize A file to normalize
	 * @param outputFile The outputFile
	 * @param header Does the CSV file has a header ?
	 * @throws IOException File is not found
	 * @throws ProcessSentenceException 
	 * @throws InvalidCSV 
	 */
	public NormalizeTerminology(File outputFile, boolean header, CSVlineHandler csvLineHandler) throws IOException, InvalidCSV, ProcessSentenceException{
		this.outputFile = outputFile;
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
		this.header = header;
		this.csvLineHandler = csvLineHandler;
	}
	
	/**
	 * Starts normalizing the labels of a File
	 * @param inputFile The inputFile
	 * @param csvLineHandler a {@link CSVlineHandler} : explain how each line will be processed 
	 * @throws IOException  File is not found
	 * @throws InvalidCSV Incompatible CSV format (if a problem occur with number of columns)
	 * @throws ProcessSentenceException An error occurs in the normalization process 
	 */
	public void normalizeFile(File inputFile) throws IOException, InvalidCSV, ProcessSentenceException {
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(inputFile));
		String line = null;
		if (header) {
			br.readLine();
		}
		int counter = 0;
		while ((line = br.readLine()) != null) {
			csvLineHandler.processLine(line);
			String newLine = csvLineHandler.getNormalizedLine();
			writeLine(newLine);
			counter = counter + 1;
		}
		logger.info("number of lines treated : " + counter);
		br.close();
	}
	
	/**
	 * Write the output to a file
	 * @param newLine A newline to append
	 * @throws IOException If the file is not found
	 */
	private void writeLine(String newLine) throws IOException {
		 Files.write(Paths.get(outputFile.getAbsolutePath()),
				 newLine.getBytes(), StandardOpenOption.APPEND);
	}
}
