package fr.erias.IAMsystem.normalizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Very basic implementation of stopwords
 * @author Cossin Sebastien (cossin.sebastien@gmail.com)
 *
 */
public class StopwordsImpl implements Stopwords {

	final static Logger logger = LoggerFactory.getLogger(StopwordsImpl.class);
	
	/**
	 * A set of stopwords
	 */
	private HashSet<String> stopwordsSet = new HashSet<String>();
	
	public StopwordsImpl() {}
	
	public StopwordsImpl( HashSet<String> stopwordsSet) {
		this.stopwordsSet = stopwordsSet;
	}
	
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
		//ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		//File file = new File(classLoader.getResource(stopwordsFile).getFile());
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
	
	public void addStopwords(String stopword) {
		stopwordsSet.add(stopword);
	}
}
