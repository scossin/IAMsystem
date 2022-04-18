package fr.erias.IAMsystem.detect;
import java.util.HashSet;
import java.util.Set;

import fr.erias.IAMsystem.stopwords.IStopwords;
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
	private final Set<ISynonym> synonyms;
	
	/**
	 * Term detector
	 */
	private final IDetectCT detector;
	
	/**
	 * Constructor
	 */
	public TermDetector() {
		this.synonyms = new HashSet<ISynonym>();
		this.tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer(); // default tokenizerNormalizer
		this.trie = new Trie();
		this.detector = new DetectCT(this.trie,this.tokenizerNormalizer,this.synonyms);
	}

	/**
	 * Add a term of a terminology
	 * @param term the label of a term (it will be normalized)
	 * @param code the code of the term
	 */
	public void addTerm(String term, String code) {
		Term newTerm = new Term(term,code,tokenizerNormalizer.getNormalizer());
		trie.addTerm(newTerm, this.tokenizerNormalizer.getTokenizer(), this.tokenizerNormalizer.getNormalizer());
	}
	
	/**
	 * Add a terminology (doesn't remove terms already added)
	 * @param terminology {@link Terminology}
	 */
	public void addTerminology(Terminology terminology) {
		trie.addTerminology(terminology, this.tokenizerNormalizer.getTokenizer(), this.tokenizerNormalizer.getNormalizer());
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
	 * Add a class that handles synonymy. For example {@link fr.erias.IAMsystem.synonym.Abbreviations}, {@link fr.erias.IAMsystem.synonym.LevenshteinTypoLucene} or a customize class that implements {@link ISynonym}
	 * @param synonym an implementation of a {@link ISynonym} to find alternatives to a token
	 */
	public void addSynonym(ISynonym synonym) {
		this.synonyms.add(synonym);
	}
	
	/**
	 * Retrieve the set of {@link ISynonym}
	 * @return set of {@link ISynonym}
	 */
	public Set<ISynonym> getSynonyms() {
		return(this.synonyms);
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
		return(this.tokenizerNormalizer.getNormalizer().getStopwords());
	}
	
	/**
	 * Change the TokenizerNormalizer's {@link IStopwords}
	 * @param stopwords a {@link IStopwords}
	 */
	public void setStopwords(IStopwords stopwords) {
		this.tokenizerNormalizer.getNormalizer().setStopwords(stopwords);
	}
	
	/**
	 * Retrieve the tree data structure of the terminology: {@link Trie}
	 * @return the trie
	 */
	public Trie getTrie() {
		return(this.trie);
	}
}