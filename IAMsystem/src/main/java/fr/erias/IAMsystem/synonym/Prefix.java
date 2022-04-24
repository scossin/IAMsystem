package fr.erias.IAMsystem.synonym;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.normalizer.Normalizer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizer.Tokenizer;
import fr.erias.IAMsystem.tokenizer.TokenizerWhiteSpace;
import fr.erias.IAMsystem.tree.INode;
import fr.erias.IAMsystem.tree.Trie;

/**
 * Approximate String algorithm based on the prefix of a token
 * It returns all the string, in the dictionary, that begins with a prefix of length 'minPrefixLength' and with a maximum number of characters diff of 'maxCharDiff'
 * 
 * @author Sebastien Cossin
 *
 */
public class Prefix implements ISynonym {

	private final int minPrefixLength;
	private final int maxCharDiff;
	private final Trie trie;
	private final ITokenizer charTokenizer;
	private final INormalizer normalizer = new Normalizer();

	/**
	 * Approximate String algorithm based on the prefix of a token
	 * @param minPrefixLength minimum number of characters of the prefix/token <br>
	 * Ignore all token that has length below minPrefixLength 
	 */
	public Prefix(int minPrefixLength) {
		this(minPrefixLength, 10000);
	}
	
	/**
	 * 
	 * @param minPrefixLength minPrefixLength minimum number of characters of the prefix/token
	 * @param maxCharDiff maximum number of character between the prefix and a string (ex: diabet --- diabetes ; 2 char)
	 */
	public Prefix(int minPrefixLength, int maxCharDiff) {
		this.minPrefixLength = minPrefixLength;
		this.maxCharDiff = maxCharDiff;
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
	 * Retrieve the tree storing the characters
	 * @return {@link Trie}
	 */
	public Trie getTrie() {
		return(this.trie);
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

	private Set<List<String>> getTokenStartingWith(String token) {
		List<String> chars = Arrays.asList(this.charTokenizer.tokenize(token));
		INode node = trie.getInitialState().gotoNode(chars);
		ArrayList<INode> nodes = new ArrayList<INode>(1);
		nodes.add(node);
		PrefixFounder prefix = new PrefixFounder(nodes, maxCharDiff);
		return(prefix.getTokenStartingWith());
	}


	@Override
	public Set<List<String>> getSynonyms(String token) {
		if (tokenLengthLessThanMinSize(token)) {
			return ISynonym.no_synonyms;
		}
		return(getTokenStartingWith(token));
	}
	
	private boolean tokenLengthLessThanMinSize(String token) {
		return token.length() < this.minPrefixLength;
	}
}

class PrefixFounder {
	private final Collection<INode> nodes;
	private final int maxCharDiff;
	
	PrefixFounder(Collection<INode> nodes, int maxCharDiff) {
		this.nodes = nodes;
		this.maxCharDiff = maxCharDiff;
	}
	
	public Set<List<String>> getTokenStartingWith() {
		Set<List<String>> startsWith = new HashSet<List<String>>();
		for (INode node : nodes) {
			startsWith.addAll(getTokenStartingWith(node));
		}
		return(startsWith);
	}
	
	private Set<List<String>> getTokenStartingWith(INode node) {
		Set<List<String>> startsWith = new HashSet<List<String>>();
		
		if (node.isAfinalState()) addLabel(node, startsWith);
		
		if (maxCharDiff > 0) { // go deeper in the trie
			PrefixFounder sub = new PrefixFounder(node.getChildNodes(), maxCharDiff -1);
			startsWith.addAll(sub.getTokenStartingWith());
		}
		return(startsWith);
	}
	
	private void addLabel(INode node, Set<List<String>> startsWith) {
		List<String> list = new ArrayList<String>(1);
		list.add(node.getTerm().getLabel());
		startsWith.add(list);
	}
}
