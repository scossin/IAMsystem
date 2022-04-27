package fr.erias.IAMsystem.detect;
import java.util.HashSet;
import java.util.Set;

import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.Trie;

/**
 * A class that encapsulates everything
 * 
 * @author Cossin Sebastien
 *
 */
public class TermDetector implements IDetectCT {
	
	/**
	 * normalizes terms and textual content. It stores the stopwords instances
	 */
	private final ITokenizerNormalizer tokenizerNormalizer;
	
	/**
	 * stores the terminology in a tree datastructure
	 */
	private final Trie trie;
	
	/**
	 * Offer the possibility to add synonym
	 */
	private final Set<ISynonym> fuzzyAlgorithms;
	
	/**
	 * Term detector
	 */
	private IDetectCT detector;
	
	/**
	 *  Create a TermDetector with the default TokenizerNormaliser and no stopword
	 */
	public TermDetector() {
		this(IStopwords.noStopwords);
	}

	/**
	 * Create a TermDetector with the default TokenizerNormaliser and a list of stopwords
	 * @param stopwords a class that can check if a token is a stopword
	 */
	public TermDetector(IStopwords stopwords) {
		this(TokenizerNormalizer.getDefaultTokenizerNormalizer(stopwords));
	}
	
	/**
	 * Create a termDetector with a {@link ITokenizerNormalizer}
	 * @param tokenizerNormalizer a class to tokenize and normalize a sentence, check for stopwords
	 */
	public TermDetector(ITokenizerNormalizer tokenizerNormalizer) {
		this.tokenizerNormalizer = tokenizerNormalizer;
		this.fuzzyAlgorithms = new HashSet<ISynonym>();
		this.trie = new Trie();
		this.detector = new DetectCT(this.trie,this.tokenizerNormalizer,this.fuzzyAlgorithms);
	}

	/**
	 * Add a term of a terminology
	 * @param label the label of a term (it will be normalized)
	 * @param code the code of the term
	 */
	public void addTerm(String label, String code) {
		Term newTerm = new Term(label,code,tokenizerNormalizer);
		trie.addTerm(newTerm, this.tokenizerNormalizer);
	}
	
	/**
	 * Add a term of a terminology
	 * @param term a {@link Term}
	 */
	public void addTerm(Term term) {
		trie.addTerm(term, this.tokenizerNormalizer);
	}
	
	/**
	 * Add a terminology (doesn't remove terms already added)
	 * @param terminology {@link Terminology}
	 */
	public void addTerminology(Terminology terminology) {
		trie.addTerminology(terminology, this.tokenizerNormalizer);
	}
	
	/**
	 * Detect candidate terms, return a {@link DetectOutput}
	 * @param document textual content to analyze
	 * @return A set of CandidateTerm (CT) detected
	 */
	public DetectOutput detect(String document) {
		return(detector.detectCandidateTerm(document));
	}
	
	@Override
	public DetectOutput detectCandidateTerm(String document) {
		return(detector.detectCandidateTerm(document));
	}
	
	/**
	 * Add a class that handles approximate string matching. <br>
	 * For example {@link fr.erias.IAMsystem.synonym.Abbreviations} or {@link fr.erias.IAMsystem.synonym.LevenshteinTypoLucene} or a customize class that implements {@link ISynonym}
	 * @param fuzzyAlgorithm an implementation of a {@link ISynonym} to find alternatives to a token
	 */
	public void addFuzzyAlgorithm(ISynonym fuzzyAlgorithm) {
		this.fuzzyAlgorithms.add(fuzzyAlgorithm);
		// create a new detector to reset the cacheSyn 
		this.detector = new DetectCT(this.trie,this.tokenizerNormalizer,this.fuzzyAlgorithms);
	}
	
	/**
	 * Method renamed 'addFuzzyAlgorithm' ; this method will be removed in future releases
	 * @param synonym an implementation of a {@link ISynonym} to find alternatives to a token
	 */
	@Deprecated
	public void addSynonym(ISynonym synonym) {
		addFuzzyAlgorithm(synonym);
	}
	
	/**
	 * Retrieve the set of {@link ISynonym}
	 * @return set of {@link ISynonym}
	 */
	public Set<ISynonym> getFuzzyAlgorithms() {
		return(this.fuzzyAlgorithms);
	}
	
	/**
	 * Method renamed 'getFuzzyAlgorithms' ; this method will be removed in future releases
	 * @return set of {@link ISynonym}
	 */
	@Deprecated
	public Set<ISynonym> getSynonyms() {
		return(this.fuzzyAlgorithms);
	}
	
	/**
	 * Retrieve the {@link ITokenizerNormalizer}
	 * @return the {@link ITokenizerNormalizer}
	 */
	public ITokenizerNormalizer getTokenizerNormalizer() {
		return(this.tokenizerNormalizer);
	}
	
	/**
	 * Retrieve the TokenizerNormalizer's stopword instance
	 * @return {@link IStopwords}
	 */
	public IStopwords getStopwords() {
		return(this.tokenizerNormalizer);
	}
	
	/**
	 * Retrieve the tree data structure of the terminology: {@link Trie}
	 * @return the trie
	 */
	public Trie getTrie() {
		return(this.trie);
	}
}