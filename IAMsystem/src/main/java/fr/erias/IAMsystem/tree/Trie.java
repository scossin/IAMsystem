package fr.erias.IAMsystem.tree;

import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.tokenizer.ITokenizer;

/**
 * IAMsystem algorithm utilizes a finate-state automata. 
 * Each state is a node in a trie datastructure. 
 * The initial state is the root node. 
 * A transition between two states, with word w, is possible if nodes are connected in the trie. 
 * @author Sebastien Cossin
 *
 */
public class Trie {
	
	// There is only one unique emptyNode
	//public final static int emptyNodeNumber = -1;
	//public final static Node emptyNode = new Node("EMPTY_NODE", emptyNodeNumber);
	public static boolean isTheEmptyNode(INode node) {
		return node == EmptyNode.EMPTYNODE;
	}
	
	// RootNode
	public final static int nodeRootNumber = 0;
	public static boolean isTheRootNode(INode node) {
		return node.getNodeNumber() == nodeRootNumber;
	}
	private int nodeNumber = 1;
	Node buildRootNode() {
		return new Node("START_TOKEN", nodeRootNumber);
	}
	
	private final Node rootNode;
	
	public Trie() {
		this.rootNode = buildRootNode();
	}
	
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
	
	public Node getInitialState() {
		return(rootNode);
	}

	public int getMaxNodeNumber() {
		return nodeNumber;
	}
}
