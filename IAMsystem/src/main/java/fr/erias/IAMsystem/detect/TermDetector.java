package fr.erias.IAMsystem.detect;
import java.io.IOException;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.lucene.IndexBigramLucene;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.synonym.LevenshteinTypoLucene;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystem.tree.SetTokenTreeBuilder;

/**
 * A class that encapsulates everything
 * 
 * @author Cossin Sebastien
 *
 */
public class TermDetector {
	
	final static Logger logger = LoggerFactory.getLogger(TermDetector.class);
	
	/**
	 * stores stopwords
	 */
	private IStopwords stopwords;
	
	/**
	 * normalizes terms and textual content. It stores the stopwords instances
	 */
	private ITokenizerNormalizer tokenizerNormalizer;
	
	/**
	 * store abbreviations
	 */
	private Abbreviations abbreviations = new Abbreviations();

	/**
	 * stores the terminology in a tree datastructure
	 */
	private SetTokenTree setTokenTree;
	
	/**
	 * store Levenshtein method
	 */
	private ISynonym levenshtein;
	
	/**
	 * Offer the possibility to add synonym
	 */
	private HashSet<ISynonym> synonyms = new HashSet<ISynonym>();
	
	
	/**
	 * Create a Lucene index
	 * @param terminology {@link Terminology}
	 * @throws IOException if 
	 */
	public void addLevenshteinIndex(Terminology terminology) throws IOException {
		IndexBigramLucene.IndexLuceneUniqueTokensBigram(terminology, this.tokenizerNormalizer); // create the index ; do it only once
		LevenshteinTypoLucene levenshteinTypoLucene = new LevenshteinTypoLucene(); // open the index
		this.levenshtein = levenshteinTypoLucene;
	}
	
	/**
	 * Constructor
	 */
	public TermDetector() {
		this.stopwords = new StopwordsImpl(); // stopwords by default
		this.tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer(this.stopwords); // default tokenizerNormalizer
		this.setTokenTree = new SetTokenTree();
		this.abbreviations = new Abbreviations();
		// initialize levenshtein distance - replace it 
		levenshtein = new ISynonym() {
			@Override
			public HashSet<String[]> getSynonyms(String token) {
				return new HashSet<String[]>();
			}
		};
	}

	
	/**
	 * Set stopwords
	 * @param stopwords a stopword instance
	 */
	public void setStopwords(IStopwords stopwords) {
		this.stopwords = stopwords;
	}
	/**
	 * add a word
	 * @param word a stopword 
	 */
	public void addStopwords(String word) {
		this.stopwords.addStopwords(word);
	}
	
	/**
	 * Add an abbreviation
	 * @param term (ex : 'acide')
	 * @param abbreviation (ex: 'ac')
	 */
	public void addAbbreviations(String term, String abbreviation) {
		this.abbreviations.addAbbreviation(term, abbreviation, tokenizerNormalizer);
	}
	
	/**
	 * Add a term of a terminology
	 * @param term the label of a term (it will be normalized)
	 * @param code the code of the term
	 */
	public void addTerm(String term, String code) {
		Term newTerm = new Term(term,code,tokenizerNormalizer.getNormalizer());
		SetTokenTreeBuilder.addTerm(newTerm, this.setTokenTree, this.tokenizerNormalizer);
	}
	
	/**
	 * Add a terminology
	 * @param terminology {@link Terminology}
	 */
	public void addTerminology(Terminology terminology) {
		SetTokenTreeBuilder.loadTokenTree(terminology, this.setTokenTree, this.tokenizerNormalizer);
	}
	
	/**
	 * Detect candidate terms, return a {@link DetectOutput}
	 * @param sentence textual content to analyze
	 * @return A set of CandidateTerm (CT) detected
	 */
	public DetectOutput detect(String sentence) {
		HashSet<ISynonym> synonyms = new HashSet<ISynonym>();
		for (ISynonym synonym : this.synonyms) {
			synonyms.add(synonym);
		}
		synonyms.add(abbreviations);
		synonyms.add(levenshtein);
		DetectDictionaryEntry detectDictionaryEntry = new DetectDictionaryEntry(this.setTokenTree,this.tokenizerNormalizer,synonyms);
		return(detectDictionaryEntry.detectCandidateTerm(sentence));
	}
	
	/**
	 * Add a synonym
	 * @param synonym an implementation of a {@link ISynonym} to find alternatives to a token
	 */
	public void addSynonym(ISynonym synonym) {
		this.synonyms.add(synonym);
	}
	
	/**
	 * Retrieve the {@link ITokenizerNormalizer}
	 * @return the {@link ITokenizerNormalizer}
	 */
	public ITokenizerNormalizer getTokenizerNormalizer() {
		return(this.tokenizerNormalizer);
	}
	
	/**
	 * retrieve the stopwords
	 * @return the stopwords
	 */
	public IStopwords getStopwords() {
		return(this.stopwords);
	}
}