package fr.erias.IAMsystem.detect;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.synonym.ISynonym;
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
	 * normalizes terms and textual content. It stores the stopwords instances
	 */
	private ITokenizerNormalizer tokenizerNormalizer;
	
	/**
	 * stores the terminology in a tree datastructure
	 */
	private SetTokenTree setTokenTree;
	
	/**
	 * Offer the possibility to add synonym
	 */
	private Set<ISynonym> synonyms = new HashSet<ISynonym>();
	
	/**
	 * Constructor
	 */
	public TermDetector() {
		this.tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer(); // default tokenizerNormalizer
		this.setTokenTree = new SetTokenTree();
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
	 * Add a terminology (doesn't remove terms already added)
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
		DetectDictionaryEntry detectDictionaryEntry = new DetectDictionaryEntry(this.setTokenTree,this.tokenizerNormalizer,synonyms);
		return(detectDictionaryEntry.detectCandidateTerm(sentence));
	}
	
	/**
	 * Add a class that handles synonymy. For example {@link fr.erias.IAMsystem.synonym.Abbreviations}, {@link fr.erias.IAMsystem.synonym.LevenshteinTypoLucene} or a customize class that implements {@link ISynonym}
	 * @param synonym an implementation of a {@link ISynonym} to find alternatives to a token
	 */
	public void addSynonym(ISynonym synonym) {
		this.synonyms.add(synonym);
	}
	
	/**
	 * Change the set of {@link ISynonym} ; default an empty set
	 * @param synonyms set of {@link ISynonym}
	 */
	public void setSynonyms(Set<ISynonym> synonyms) {
		this.synonyms = synonyms;
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
	 * set the {@link ITokenizerNormalizer}
	 * @param tokenizerNormalizer a {@link ITokenizerNormalizer}
	 */
	public void setTokenizerNormalizer(ITokenizerNormalizer tokenizerNormalizer){
		this.tokenizerNormalizer = tokenizerNormalizer;
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
	 * Retrieve the tree data structure of the terminology: {@link SetTokenTree}
	 * @return the setTokenTree
	 */
	public SetTokenTree getSetTokenTree() {
		return(this.setTokenTree);
	}
	
	/**
	 * Change the terminology by replace the {@link SetTokenTree}
	 * @param setTokenTree a new {@link SetTokenTree}
	 */
	public void setSetTokenTree(SetTokenTree setTokenTree) {
		this.setTokenTree = setTokenTree;
	}
}