package fr.erias.IAMsystem.stopwords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.normalizer.INormalizer;

/**
 * Very basic implementation of stopwords
 * @author Cossin Sebastien (cossin.sebastien@gmail.com)
 *
 */
public class StopwordsImpl implements IStopwords {

	final static Logger logger = LoggerFactory.getLogger(StopwordsImpl.class);
	
	/**
	 * A set of stopwords
	 */
	private HashSet<String> stopwordsSet = new HashSet<String>();
	
	/**
	 * Constructor
	 */
	public StopwordsImpl() {
		
	}
	
	public StopwordsImpl(HashSet<String> stopwordsSet) {
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
	 * Add stopwords from a file
	 * @param in stopwordsFile A file containing a list of stopword ; one by line
	 * @param normalizer a {@link INormalizer} to normalize stopword
	 * @throws IOException if the file can't be found
	 */
	public void setStopWords (InputStream in, INormalizer normalizer) throws IOException {
		//ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		//File file = new File(classLoader.getResource(stopwordsFile).getFile());
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = br.readLine()) != null) {
			String normalizedLabel = normalizer.getNormalizedSentence(line);
			stopwordsSet.add(normalizedLabel);
		}
		logger.info("stopwords size : " + stopwordsSet.size());
		br.close();
	}
	
	/**
	 * Add stopwords from a file without normalization
	 * @param in stopwordsFile A file containing a list of stopword ; one by line
	 * @throws IOException can't load the stopwords file
	 */
	public void setStopWords (InputStream in) throws IOException {
		setStopWords(in, new INormalizer() {
			@Override
			public IStopwords getStopwords() {
				return null;
			}
			
			@Override
			public String getNormalizedSentence(String sentence) {
				return sentence;
			}
		});
	}
	
	/**
	 * Add a stopword
	 */
	public void addStopwords(String stopword) {
		stopwordsSet.add(stopword);
	}
}
