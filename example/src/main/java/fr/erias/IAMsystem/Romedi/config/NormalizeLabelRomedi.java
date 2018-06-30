package fr.erias.IAMsystem.Romedi.config;

import java.io.File;
import java.io.IOException;

import fr.erias.IAMsystem.exceptions.InvalidCSV;
import fr.erias.IAMsystem.exceptions.ProcessSentenceException;
import fr.erias.IAMsystem.normalizer.CSVlineHandlerImpl;
import fr.erias.IAMsystem.normalizer.NormalizeTerminology;

/**
 * First step : normalize the labels of the dictionary
 * 
 * @author Cossin Sebastien (cossin.sebastien@gmail.com)
 *
 */
public class NormalizeLabelRomedi {
	
	public static void normalizeFile() throws IOException, InvalidCSV, ProcessSentenceException {
		// stopwords file : 
		File stopwordsFile = new File(ConfigRomedi.STOPWORDS_FILE);
		StopwordsRomedi stopwordsRomedi = new StopwordsRomedi();
		stopwordsRomedi.setStopWords(stopwordsFile);
		
		// dictionary file : 
		String sep = "\t";
		short positionLabel = 2;
		CSVlineHandlerImpl csvLineHandler = new CSVlineHandlerImpl(stopwordsRomedi,sep,positionLabel);
		
		// create a new file with normalized labels : 
		NormalizeTerminology normalizeCSV = new NormalizeTerminology(new File(ConfigRomedi.romediTermsNormalized),
				true,csvLineHandler);
		normalizeCSV.normalizeFile(new File(ConfigRomedi.romediTerms));
	}
	
	public static void main(String[] args) throws IOException, InvalidCSV, ProcessSentenceException {
		normalizeFile();
	}
}
