package fr.erias.IAMsystem.synonym;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import fr.erias.IAMsystem.lucene.IndexBigramLucene;
import fr.erias.IAMsystem.lucene.SearchIndex;

/**
 * This class uses Lucene to perform a Levenshtein distance
 * @author Cossin Sebastien
 *
 */
public class LevenshteinTypoLucene implements ISynonym {
	
	/**
	 * A lucene search engine to perform Levenshtein distance
	 */
	private SearchIndex searchIndex = null;
	
	/**
	 * Don't search a typo for these tokens
	 */
    private Set<String> tokens2ignore = new HashSet<String>();
	
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
			System.err.println("impossible to set maxEdits greater than 2");
			this.maxEdits = 2;
		} else if (maxEdits < 1) {
			System.err.println("impossible to set maxEdits lower than 1"); // 0 means exact match
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
	private Set<List<String>> searchIndexLeven(String term) throws IOException, ParseException {
		// don't search anything if less than 4 characters (to avoid noise)
		if (term.length() < this.minNchar) { 
			return(ISynonym.no_synonyms);
		}

		int maxHits = 10; // number of maximum hits - results

		// search a typo in a term (ex : cardique for cardiaque) or a concatenation (meningoencephalite for meningo encephalite)
		Query query = searchIndex.fuzzyQuery(term, IndexBigramLucene.CONCATENATED_FIELD, maxEdits);
		ScoreDoc[] hits = searchIndex.evaluateQuery(query, maxHits);

		// if no hits return
		if (hits.length == 0) {
			return(ISynonym.no_synonyms);
		}

		// if hits, add the array of synonyms
		Set<List<String>> synonyms = new HashSet<List<String>>();
		for (int i = 0; i<hits.length ; i++) {
			Document doc = searchIndex.getIsearcher().doc(hits[i].doc);
			String bigram = doc.get(IndexBigramLucene.BIGRAM_FIELD);
			String[] bigramArray = bigram.split(" ");
			synonyms.add(Arrays.asList(bigramArray));
		}
		return(synonyms);
	}
	
	@Override
	public Set<List<String>> getSynonyms(String token) {
		if (this.tokens2ignore.contains(token)) return ISynonym.no_synonyms;
		Set<List<String>> output;
		try {
			output = searchIndexLeven(token);
			return(output);
		} catch (IOException | ParseException e) {
			e.getStackTrace();
			return(ISynonym.no_synonyms);
		}
	}

	/**
	 * Retrieve the set of ignored tokens
	 * @return a set of ignored tokens
	 */
	public Set<String> getTokens2ignore() {
		return tokens2ignore;
	}

	/**
	 * add a set of tokens to ignore: this algorithm will not be called for these tokens
	 * @param tokens2ignore a set of normalized tokens to ignore
	 */
	public void setTokens2ignore(Set<String> tokens2ignore) {
		this.tokens2ignore = tokens2ignore;
	}
	
	/**
	 * Add a token to ignore: this algorithm will not be called for this token
	 * @param token2ignore a normalized token to ignore
	 */
	public void addAtoken2ignore(String token2ignore) {
		this.tokens2ignore.add(token2ignore);
	}
}
