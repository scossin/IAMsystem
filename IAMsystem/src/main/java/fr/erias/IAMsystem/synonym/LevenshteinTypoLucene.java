package fr.erias.IAMsystem.synonym;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.detect.HashSetStringArray;
import fr.erias.IAMsystem.exceptions.MyExceptions;
import fr.erias.IAMsystem.lucene.IndexBigramLucene;
import fr.erias.IAMsystem.lucene.SearchIndex;

/**
 * This class uses Lucene to perform a Levenshtein distance
 * @author Cossin Sebastien
 *
 */
public class LevenshteinTypoLucene implements ISynonym {
	
	final static Logger logger = LoggerFactory.getLogger(LevenshteinTypoLucene.class);
	
	/**
	 * A lucene search engine to perform Levenshtein distance
	 */
	private SearchIndex searchIndex = null;
	
	/**
	 * Save unmatched, no need to search multiples times the same token that had no match
	 */
	private HashSet<String> unmatched = new HashSet<String>() ;
	
	/**
	 * Save matched, no need to search multiples times the same token that had a match 
	 */
	private HashMap<String, HashSet<String[]>> matched = new HashMap<String, HashSet<String[]>>();
	
	/**
	 * Number of insertion, deletion of the Levenshtein distance. Max 2
	 */
	private int maxEdits = 1; 
	
	/**
	 * don't search anything in the index if number of character is below this number
	 */
	private int minNchar = 5;
		
	/**
	 * Constructor 
	 * @param indexFolder The indexFolder of the Lucene Index to perform fuzzy queries (Levenshtein distance)
	 * @throws IOException If the Lucene index can't be found
	 */
	public LevenshteinTypoLucene(File indexFolder) throws IOException {
		this.searchIndex = new SearchIndex(indexFolder);
	}
	
	/**
	 * Constructor with default LUCENE_INDEX_FOLDER filename
	 * @throws IOException can't open the Lucene index
	 */
	public LevenshteinTypoLucene() throws IOException {
		this(new File(IndexBigramLucene.LUCENE_INDEX_FOLDER));
	}
	
	/**
	 * Create an instance to connect to the Lucene Index
	 * @return the searchIndex instance
	 */
	public SearchIndex getSearchIndex() {
		return(searchIndex);
	}
	
	/**
	 * setMaxEdits value in Lucene: number of insertion, deletion of the Levenshtein distance.
	 * @param maxEdits 1 or 2 (maximum)
	 */
	
	public void setMaxEdits(int maxEdits) {
		if (maxEdits > 2) {
			logger.info("impossible to set maxEdits greater than 2");
			this.maxEdits = 2;
		} else if (maxEdits < 1) {
			logger.info("impossible to set maxEdits lower than 1"); // 0 means exact match
			this.maxEdits = 1;
		} else {
			this.maxEdits = maxEdits;
		}
	}
	
	/**
	 * set minNchar
	 * @param minNchar don't search anything in the index if number of character is below this number (default to 5)
	 */
	public void setMinNchar (int minNchar) {
		this.minNchar = minNchar;
	}
	
	/**
	 * Search a normalized term with a Levenshtein distance (fuzzy query of Lucene)
	 * @param term The normalized term to search in the Lucene Index
	 * @return A set of synonyms
	 * @throws IOException If the index is not found
	 * @throws ParseException If the Lucene query fails
	 */
	private HashSet<String[]> searchIndexLeven(String term) throws IOException, ParseException {
		// return this : exact term and typos in term
		HashSetStringArray synonyms = new HashSetStringArray(); // A customized HashSet of array

		// don't search anything if less than 4 characters (to avoid noise)
		if (term.length()<this.minNchar) { 
			return(synonyms);
		}

		int maxHits = 10; // number of maximum hits - results

		// search a typo in a term (ex : cardique for cardiaque) or a concatenation (meningoencephalite for meningo encephalite)
		Query query = searchIndex.fuzzyQuery(term, IndexBigramLucene.CONCATENATED_FIELD, maxEdits);
		ScoreDoc[] hits = searchIndex.evaluateQuery(query, maxHits);

		// if no hits return
		if (hits.length == 0) {
			addUnmatched(term);
			return(synonyms);
		}

		// if hits, add the array of synonyms
		for (int i = 0; i<hits.length ; i++) {
			Document doc = searchIndex.getIsearcher().doc(hits[i].doc);
			String bigram = doc.get(IndexBigramLucene.BIGRAM_FIELD);
			logger.debug("detected synonyms : " + bigram);
			String[] bigramArray = bigram.split(" ");
			if (!synonyms.containsArray(bigramArray)) {
				synonyms.add(bigramArray);
			}
		}
		logger.debug("synonyms size : " + synonyms.size());
		
		addMatched(term, synonyms);
		return(synonyms);
	}
	
	/**
	 * Add an unmatched token that was not found in the Lucene Index
	 * @param token an unmatched token
	 */
	private void addUnmatched(String token) {
		unmatched.add(token);
	}
	
	/**
	 * Add a set of unmatched terms - words that won't be matched or we don't want to be matched
	 * @param tokensSet A set of tokens
	 */
	public void addUnmatched(Set<String> tokensSet) {
		unmatched.addAll(tokensSet);
	}
	
	/**
	 * Add a matched token that was found in the Lucene Index
	 * @param token a token found in this index
	 * @param synonyms a list of "synonyms" (aka lexical variants)
	 */
	private void addMatched(String token, HashSet<String[]> synonyms) {
		matched.put(token, synonyms);
	}
	
	@Override
	public HashSet<String[]> getSynonyms(String token) {
		HashSet<String[]> output = new HashSet<String[]>();
		// if already searched and no matched
		if (unmatched.contains(token)) {
			return(output);
		}
		
		// if already searched and matched found 
		if (matched.containsKey(token)) {
			output = matched.get(token);
			return(output);
		}
		
		// if it's the first time we searched this token :
		try {
			output = searchIndexLeven(token);
		} catch (IOException | ParseException e) {
			logger.debug("an error occured while searching in the lucene index");
			MyExceptions.logException(logger, e);
		}
		return(output);
	}

}
