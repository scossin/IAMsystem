package fr.erias.IAMsystem.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class search in the Lucene index
 * @author Cossin Sebastien (cossin.sebastien@gmail.com)
 *
 */
public class SearchIndex {

	final static Logger logger = LoggerFactory.getLogger(SearchIndex.class);
	
	/**
	 * By default a stopword analyzer without stopwords
	 */
	private Analyzer analyzer = new StopAnalyzer() ;
	private Directory directory = null;
    private IndexSearcher isearcher = null;
	
    /**
     * Create a {@link IndexSearcher} to search in a Lucene index
     * @param indexFolder The folder containing the Lucene index
     * @throws IOException If the folder is not found
     */
    public SearchIndex(File indexFolder) throws IOException{
    	directory = FSDirectory.open(indexFolder.toPath());
    	IndexReader ireader = DirectoryReader.open(directory);
    	isearcher = new IndexSearcher(ireader);
    }
    
    /**
     * 
     * @return the {@link IndexSearcher}
     */
    public IndexSearcher getIsearcher() {
    	return(isearcher);
    }
    
    /**
     * Close the Lucene index reader
     * @throws IOException If the reader can't be closed
     */
    public void closeReader() throws IOException {
    	this.isearcher.getIndexReader().close();
    }
    
    /**
     * Change the default analyzer 
     * @param analyzer a Lucene {@link Analyzer}
     */
    public void setAnalyzer(Analyzer analyzer) {
    	this.analyzer = analyzer;
    }
    
    /**
     * The main method to search in the index. The term is analyzed before the search
     * @param term A term to search
     * @param fieldName Where to search ? the fieldName. You must know well your index to know the fieldName
     * @return {@link ScoreDoc} containing the hits
     * @throws IOException If something bad happens while searching the index
     * @throws ParseException If something bad happens while parsing the term
     */
    public Query searchTerm (String term, String fieldName) throws IOException, ParseException {
    	logger.debug("Preparing query to search index term : " + term + " in fieldName : " + fieldName);
	    QueryParser parser = new QueryParser(fieldName, analyzer);
	    Query query = null;
	    query = parser.parse(term);
		return query;
    }
    
    /**
     * Evaluate a query in the Lucene index
     * @param query {@link Query} to be searched
     * @param maxHit The maximum number of hits the Lucene searcher will try to match
     * @return {@link ScoreDoc} containing the hits
     * @throws IOException If something bad happens while searching the index
     */
    public ScoreDoc[] evaluateQuery(Query query, int maxHit) throws IOException {
    	ScoreDoc[] hits = isearcher.search(query,maxHit).scoreDocs;
    	return(hits);
    }
    
    /**
     * The main method to search an ExactTerm. The term is NOT analyzed before the search
     * @param term An exact term to search
     * @param fieldName Where to search ? the fieldName. You must know well your index to know the fieldName
     * @return {@link ScoreDoc} containing the hits
     * @throws IOException If something append while searching the index
     */
    public Query searchExactTerm (String term, String fieldName) throws IOException{
        logger.debug("Preparing to search in index this exact term : " + term + " in fieldName : " + fieldName);
    	Query query = new TermQuery(new Term(fieldName,term));
    	return(query);
    }
    
    /**
     * Performs a fuzzy query in the Lucene Index
     * @param term The term to search in the document
     * @param fieldName The name of the field in the document
     * @param maxEdits The maximum number of delete/insertion in the string
     * @return A Lucene Query
     */
    public Query fuzzyQuery(String term, String fieldName, int maxEdits) {
    	FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(fieldName,term),maxEdits);
    	return(fuzzyQuery);
    }
    
}
