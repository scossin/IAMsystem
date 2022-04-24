package fr.erias.IAMsystem.tree;

import java.util.HashSet;
import java.util.Set;

import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.normalizer.Normalizer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizer.Tokenizer;
import fr.erias.IAMsystem.tokenizer.TokenizerWhiteSpace;

/**

 * @author Sebastien Cossin
 *
 */
public class PrefixTrie {

	private final int minPrefixLength;
	private final Trie trie;
	private final ITokenizer charTokenizer;
	private final INormalizer normalizer = new Normalizer();

	
	/**
	 * Approximate String algorithm based on the prefix of a token
	 * @param minPrefixLength minimum number of characters of the prefix/token <br>
	 * Ignore all token that has length below minPrefixLength 
	 */
	public PrefixTrie(int minPrefixLength) {
		this.minPrefixLength = minPrefixLength;
		this.trie = new Trie();
		Tokenizer charTokenizer = new Tokenizer();
		charTokenizer.setPattern("[a-z]");
		this.charTokenizer = charTokenizer;
	}

	/**
	 * Add a terminology (doesn't remove terms already added) <br>
	 * Each token of each term is stored in the trie
	 * @param terminology {@link Terminology}
	 * @param stopwords {@link IStopwords}  stopwords are tokens not in the trie
	 */
	public void addTerminology(Terminology terminology, IStopwords stopwords) {
		Set<String> tokens = getUniqueToken(terminology, stopwords);
		for (String token : tokens) {
			addToken(token);
		}
	}
	
	/**
	 * Add a term : each token is stored in the trie
	 * @param term {@link Term}
	 * @param stopwords {@link IStopwords} stopwords are tokens not in the trie
	 */
	public void addTerm(Term term, IStopwords stopwords) {
		Set<String> tokens = getUniqueToken(term, stopwords);
		for (String token : tokens) {
			addToken(token);
		}
	}

	private void addToken(String token) {
		Term term = new Term(token,"TOKEN"); // the code of the term doesn't matter
		trie.addTerm(term, charTokenizer, normalizer);
	}

	private Set<String> getUniqueToken(Terminology terminology, IStopwords stopwords) {
		Set<String> uniqueTokens = new HashSet<String> ();
		for (Term term : terminology.getTerms()) {
			uniqueTokens.addAll(getUniqueToken(term, stopwords));
		}
		return(uniqueTokens);
	}

	private Set<String> getUniqueToken(Term term, IStopwords stopwords) {
		ITokenizer tokenizer = new TokenizerWhiteSpace();
		Set<String> uniqueTokens = new HashSet<String> ();
		String normalizeLabel = term.getNormalizedLabel();
		String[] tokensArray = tokenizer.tokenize(normalizeLabel);
		tokensArray = IStopwords.removeStopWords(stopwords, tokensArray);
		for (String token : tokensArray) {
			if (tokenLengthLessThanMinSize(token)) {
				continue;
			}
			uniqueTokens.add(token);
		}
		return(uniqueTokens);
	}
	
	/**
	 * Check if token length is greater than the minimum size
	 * @param token a document from the document
	 * @return true if greater or equal to the minimum size
	 */
	public boolean tokenLengthLessThanMinSize(String token) {
		return token.length() < this.minPrefixLength;
	}
	
	/**
	 * Retrieve the character tokenizer
	 * @return a tokenizer
	 */
	public ITokenizer getCharTokenizer() {
		return charTokenizer;
	}
	
	
	/**
	 * Retrieve the tree storing the characters
	 * @return {@link Trie}
	 */
	public Trie getTrie() {
		return(this.trie);
	}
	
}