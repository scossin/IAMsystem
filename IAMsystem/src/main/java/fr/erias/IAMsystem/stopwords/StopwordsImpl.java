package fr.erias.IAMsystem.stopwords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import fr.erias.IAMsystem.normalizer.INormalizer;

/**
 * Very basic implementation of stopwords
 * @author Cossin Sebastien (cossin.sebastien@gmail.com)
 *
 */
public class StopwordsImpl implements IStopwords {

	/**
	 * A set of stopwords
	 */
	private Set<String> stopwordsSet = new HashSet<String>();

	/**
	 * Constructor
	 */
	public StopwordsImpl() {

	}

	public StopwordsImpl(Set<String> stopwordsSet) {
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
		br.close();
	}

	/**
	 * Add stopwords from a file without normalization
	 * @param in stopwordsFile A file containing a list of stopword ; one by line
	 * @throws IOException can't load the stopwords file
	 */
	public void setStopWords (InputStream in) throws IOException {
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = br.readLine()) != null) {
			stopwordsSet.add(line);
		}
		br.close();
	}

	/**
	 * Add a stopword
	 * @param stopword a normalized (or not) stopword. Diacritics matter
	 */
	public void addStopwords(String stopword) {
		stopwordsSet.add(stopword);
	}
}
