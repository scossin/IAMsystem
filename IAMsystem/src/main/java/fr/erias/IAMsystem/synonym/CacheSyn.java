package fr.erias.IAMsystem.synonym;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class stores in cache, for every unique token of a document, the matching tokens of the terminology
 * Maximum maxNumberOfwords in cache set to 100000
 * 
 * @author Sebastien Cossin
 *
 */
public class CacheSyn implements ISynonym {

	private final Set<ISynonym> fuzzyAlgorithms ;
	
	private final Map<String, Set<List<String>>> synonymsCache;
	
	private final int maxNumberOfwords;
	
	/**
	 * Construct a CacheSyn with default number of words (100000)
	 * @param fuzzyAlgorithms a Set of {@link ISynonym} to find 'synonyms' of a word
	 */
	public CacheSyn(Set<ISynonym> fuzzyAlgorithms) {
		this(fuzzyAlgorithms, 100000);
	}
	
	/**
	 * Construct a CacheSyn with a limited number of words
	 * It is supposed to avoid memory to grow too large
	 * @param fuzzyAlgorithms a Set of {@link ISynonym} to find approximate matches of a token
	 * @param maxNumberOfwords maximum number of words in cache
	 */
	public CacheSyn(Set<ISynonym> fuzzyAlgorithms, int maxNumberOfwords) {
		this.maxNumberOfwords = maxNumberOfwords;
		int initialCapacity = maxNumberOfwords;
		float loadFactor = 1.0F;
		this.synonymsCache = new HashMap<String, Set<List<String>>>(initialCapacity, loadFactor);
		this.fuzzyAlgorithms = fuzzyAlgorithms;
	}
	
	@Override
	public Set<List<String>> getSynonyms(String token) {
		if (synonymsCache.containsKey(token)) {
			return(synonymsCache.get(token));
		} else {
			// find synonyms (typos, abbreviations...):
			int initialCapacity = 8; // Default to 16 but the mean is about 3 sequences
			Set<List<String>> currentSynonyms = new HashSet<List<String>>(initialCapacity);
			String[] tokenInArray = {token};
			currentSynonyms.add(Arrays.asList(tokenInArray));
			// find synonyms: 
			for (ISynonym fuzzyAlgorithm : fuzzyAlgorithms) {
				currentSynonyms.addAll(fuzzyAlgorithm.getSynonyms(token));
			}
			if (synonymsCache.size() < maxNumberOfwords) {
				synonymsCache.put(token, currentSynonyms);
			}
			return(currentSynonyms);
		}
	}
}
