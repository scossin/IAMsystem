package fr.erias.IAMsystem.detect;

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

import fr.erias.IAMsystem.exceptions.MyExceptions;
import fr.erias.IAMsystem.lucene.SearchIndex;

/**
 * This class uses Lucene to perform a Levenshtein distance
 * @author Cossin Sebastien
 *
 */
public class LevenshteinTypoLucene implements Synonym {
	
	final static Logger logger = LoggerFactory.getLogger(LevenshteinTypoLucene.class);
	
	/**
	 * A lucene search engine to perform Levenshtein distance
	 */
	private SearchIndex searchIndex = null;
	
	/**
	 * the name of the Lucene field in the index containing a bigram concatenation (ex : meningoencephalite)
	 */
	private String concatenationField = null;
	
	/**
	 * bigramField the name of the Lucene field in the index containing the initial bigram (ex : meningo encephalite)
	 */
	private String bigramField = null;
	
	/**
	 * Save unmatched, no need to search multiples times the same token that had no match
	 */
	private HashSet<String> unmatched = new HashSet<String>() ;
	
	/**
	 * Save matched, no need to search multiples times the same token that had a match 
	 */
	private HashMap<String, HashSet<String[]>> matched = new HashMap<String, HashSet<String[]>>();
	
	/**
	 * Ignore term less than a minimum number of character
	 */
	private int minNcharTerm = 5;
	
	/**
	 * Constructor 
	 * @param indexFolder The indexFolder of the Lucene Index to perform fuzzy queries (Levenshtein distance)
	 * @param concatenationField the name of the Lucene field in the index containing a bigram concatenation (ex : meningoencephalite)
	 * @param bigramField the name of the Lucene field in the index containing the initial bigram (ex : meningo encephalite)
	 * @throws IOException If the Lucene index can't be found
	 */
	public LevenshteinTypoLucene(File indexFolder, String concatenationField, String bigramField) throws IOException {
		this.searchIndex = new SearchIndex(indexFolder);
		this.concatenationField = concatenationField;
		this.bigramField = bigramField;
	}
	
	
	/**
	 * Create an instance to connect to the Lucene Index
	 * @return the searchIndex instance
	 */
	public SearchIndex getSearchIndex() {
		return(searchIndex);
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

		// don't search anything if less than the edit distance (default 5)
		if (term.length()<this.minNcharTerm) { 
			return(synonyms);
		}

		int maxEdits = 1; // number of insertion, deletion of the Levenshtein distance. Max 2
		int maxHits = 20; // number of maximum hits - results

		// search a typo in a term (ex : cardique for cardiaque) or a concatenation (meningoencephalite for meningo encephalite)
		Query query = searchIndex.fuzzyQuery(term, concatenationField, maxEdits);
		ScoreDoc[] hits = searchIndex.evaluateQuery(query, maxHits);

		// if no hits return
		if (hits.length == 0) {
			addUnmatched(term);
			return(synonyms);
		}

		// if hits, add the array of synonyms
		for (int i = 0; i<hits.length ; i++) {
			Document doc = searchIndex.getIsearcher().doc(hits[i].doc);
			String bigram = doc.get(bigramField);
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
	
	/**
	 * Ignore term less than a minimum number of character
	 * @param minNcharTerm number of character
	 */
	public void setMinNcharTerm(int minNcharTerm) {
		this.minNcharTerm = minNcharTerm;
	}
	
	/**
	 * Ignore term less than a minimum number of character 
	 * @return minimum number of character
	 */
	public int getMinNcharTerm () {
		return(this.minNcharTerm);
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
