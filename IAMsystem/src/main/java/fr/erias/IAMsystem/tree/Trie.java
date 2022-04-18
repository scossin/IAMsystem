package fr.erias.IAMsystem.tree;

import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;

/**
 * IAMsystem algorithm utilizes a finate-state automata. <br>
 * Each state is a node in a trie datastructure. <br>
 * The initial state is the root node. <br>
 * A transition between two states, with word w, is possible if nodes are connected in the trie. 
 * @author Sebastien Cossin
 *
 */
public class Trie {
	
	/**
	 * The empty is not in the trie. It represents the "empty" state. 
	 * This emptyNode is returned when no path is found in the trie. 
	 * @param node a node of the trie
	 * @return true if this node is the EmptyNode
	 */
	public static boolean isTheEmptyNode(INode node) {
		return node == EmptyNode.EMPTYNODE;
	}
	
	// RootNode
	public final static int nodeRootNumber = 0;
	public static boolean isTheRootNode(INode node) {
		return node.getNodeNumber() == nodeRootNumber;
	}
	protected Node buildRootNode() {
		return new Node("START_TOKEN", nodeRootNumber);
	}
	private final Node rootNode;
	private int nodeNumber = 1; // each node in the trie has a unique number. This variable counts every node. 
	
	/**
	 * Construct a trie to store a terminology. <br>
	 * Each node is a state of a finite-state automata. 
	 */
	public Trie() {
		this.rootNode = buildRootNode();
	}
	
	/**
	 * Add a term of a terminology. 
	 * The trie creates as many nodes (= a token / state) as needed. 
	 * @param term A term of a terminology
	 * @param tokenizer to tokenize the label of the term
	 * @param normalizer to normalize the label of the term
	 */
	public void addTerm(Term term, ITokenizer tokenizer, INormalizer normalizer) {
		IStopwords stopwords = normalizer.getStopwords();
		String normalizedLabel = term.getNormalizedLabel();
		if (stopwords.isStopWord(normalizedLabel)) {
			return;
		}
		String[] tokens = tokenizer.tokenize(normalizedLabel);
		String[] tokens_without_stopwords = IStopwords.removeStopWords(stopwords, tokens);
		if (tokens_without_stopwords.length == 0) {
			return;
		}
		addTerm(term, tokens_without_stopwords);
	}
	
	/**
	 * Add a term of a terminology. 
	 * @param term a term of a terminology
	 * @param tokenizerNormalizer to tokenize and normalize the labels of the terms
	 */
	public void addTerm(Term term, ITokenizerNormalizer tokenizerNormalizer) {
		addTerm(term, tokenizerNormalizer.getTokenizer(), tokenizerNormalizer.getNormalizer());
	}
	
	/**
	 * add every term of a terminology
	 * @param terminology a {@link Terminology}
	 * @param tokenizer to tokenize the labels of the terms
	 * @param normalizer to normalize the labels of the terms
	 */
	public void addTerminology(Terminology terminology, ITokenizer tokenizer, INormalizer normalizer) {
		for (Term term : terminology.getTerms()) {
			addTerm(term, tokenizer, normalizer);
		}
	}
	
	/**
	 * add every term of a terminology
	 * @param terminology a {@link Terminology}
	 * @param tokenizerNormalizer to tokenize and normalize the labels of the terms
	 */
	public void addTerminology(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer) {
		for (Term term : terminology.getTerms()) {
			addTerm(term, tokenizerNormalizer.getTokenizer(), tokenizerNormalizer.getNormalizer());
		}
	}
	
	/**
	 * Add a term of a terminology. 
	 * @param term a term of a terminology
	 * @param tokens a sequence of normalized token of the label of the term
	 */
	public void addTerm(Term term, String[] tokens) {
		if (tokens.length == 0) {
			return;
		}
		INode currentNode = rootNode;
		for (int i = 0; i<tokens.length; i++) {
			String token = tokens[i];
			if (currentNode.hasTransitionTo(token)) {
				currentNode = currentNode.gotoNode(token);
			} else {
				INode newNode = new Node(token, currentNode, nodeNumber);
				nodeNumber++;
				currentNode = newNode;
			}
		}
		currentNode.setTerm(term);
	}
	
	/**
	 * The initial state of the final-state automata is the rootNode of the trie
	 * @return the rootNode
	 */
	public Node getInitialState() {
		return(rootNode);
	}

	/**
	 * Each node has a unique number. The trie keeps track of the number of nodes.
	 * @return The number of nodes/states in the trie
	 */
	public int getNumberOfNodes() {
		return nodeNumber;
	}
}
