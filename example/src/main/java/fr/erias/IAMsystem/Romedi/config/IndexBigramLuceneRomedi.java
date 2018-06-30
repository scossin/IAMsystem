package fr.erias.IAMsystem.Romedi.config;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import fr.erias.IAMsystem.load.Loader;
import fr.erias.IAMsystem.lucene.IndexBigramLucene;

/**
 * Second step : Create a Lucene index to detect typos from normalized labels
 * 
 * @author Cossin Sebastien
 *
 */
public class IndexBigramLuceneRomedi {

	public static void main(String args[]) throws IOException {
		// stopwords
		File stopwordsFile = new File(ConfigRomedi.STOPWORDS_FILE);
		StopwordsRomedi stopwordsClef = new StopwordsRomedi();
		stopwordsClef.setStopWords(stopwordsFile);
		
		// normalized labels in the 4th column
		File CSVFile = new File(ConfigRomedi.romediTermsNormalized);
		HashMap<String,String> uniqueTokensBigram = Loader.getUniqueTokenBigram(stopwordsClef, CSVFile, "\t", 3);
		File indexFolder = new File(ConfigRomedi.INDEX_FOLDER);
		IndexBigramLucene.IndexLuceneUniqueTokensBigram(uniqueTokensBigram, indexFolder, ConfigRomedi.CONCATENATION_FIELD,
				ConfigRomedi.BIGRAM_FIELD);
	}
}
