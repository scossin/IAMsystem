package fr.erias.IAMsystem.Romedi.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.normalizer.Stopwords;
import fr.erias.IAMsystem.tokenizer.TokenizerNormalizer;

/**
 * Stopwords from a stopword file
 * 
 * @author Cossin Sebastien
 *
 */
public class StopwordsRomedi implements Stopwords {
	
	final static Logger logger = LoggerFactory.getLogger(TokenizerNormalizer.class);
	
	/**
	 * A set of stopwords
	 */
	private static HashSet<String> stopwordsSet = new HashSet<String>();
	
	/**
	 * Interface method
	 */
	public boolean isStopWord(String token) {
		// if the set of stopwords contains it
		if (stopwordsSet.contains(token.toLowerCase())) {
			return(true);
		}
		return(false);
	}
	
	/**
	 * Change the set of stopwords
	 * @param stopwordsFile A file containing a list of stopword ; one by line
	 * @throws IOException if the file can't be found
	 */
	public void setStopWords (File stopwordsFile) throws IOException {
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(stopwordsFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			//String normalizedLabel = normalizedSentence(line);
			stopwordsSet.add(line);
		}
		logger.info("stopwords size : " + stopwordsSet.size());
		br.close();
	}
	
	/**
	 * Get the set of stopwords
	 * @return A set of stopwords
	 */
	public HashSet<String> getStopWords(){
		return(stopwordsSet);
	}
}
