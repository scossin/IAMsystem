package fr.erias.iamsystem.tree;

import java.util.List;
import java.util.stream.Collectors;

import fr.erias.iamsystem.keywords.IKeyword;
import fr.erias.iamsystem.keywords.Keyword;
import fr.erias.iamsystem.tokenize.ITokenizerStopwords;

/**
 * IAMsystem algorithm utilizes a finate-state automata. <br>
 * Each state is a node in a trie datastructure. <br>
 * The initial state is the root node. <br>
 * A transition between two states, with word w, is possible if nodes are
 * connected in the trie.
 *
 * @author Sebastien Cossin
 */
public class Trie
{

	public static final int nodeRootNumber = 0;

	/**
	 * The empty is not in the trie. It represents the "empty" state. This emptyNode
	 * is returned when no path is found in the trie.
	 *
	 * @param node a node of the trie
	 * @return true if this node is the EmptyNode
	 */
	public static boolean isTheEmptyNode(INode node)
	{
		return node == EmptyNode.EMPTYNODE;
	}

	public static boolean isTheRootNode(INode node)
	{
		return node.getNodeNumber() == nodeRootNumber;
	}

	private final Node rootNode;

	private int nodeNumber = 1; // each node in the trie has a unique number. This variable counts every node.

	/**
	 * Construct a trie to store {@link IKeyword} <br>
	 */
	public Trie()
	{
		this.rootNode = buildRootNode();
	}

	/**
	 * Add a {@link IKeyword}.
	 *
	 * @param keyword A {@link IKeyword} instance.
	 * @param tokstop a {@link ITokenizerStopwords} to tokenize and normalize
	 *                keyword's label.
	 */
	public void addIKeyword(IKeyword keyword, ITokenizerStopwords tokstop)
	{
		List<String> stringSeq = ITokenizerStopwords.tokenizeRmStopwords(keyword.label(), tokstop)
				.stream()
				.map(token -> token.normLabel())
				.collect(Collectors.toList());
		addIKeyword(keyword, stringSeq);
	}

	/**
	 * Add a {@link IKeyword}.
	 *
	 * @param keyword   a {@link IKeyword} instance.
	 * @param stringSeq an ordered sequence of keyword's label normalized.
	 */
	public void addIKeyword(IKeyword keyword, List<String> stringSeq)
	{
		if (stringSeq.size() == 0)
			return;
		INode currentNode = rootNode;
		for (String normLabel : stringSeq)
		{
			if (currentNode.hasTransitionTo(normLabel))
			{
				currentNode = currentNode.gotoNode(normLabel);
			} else
			{
				INode newNode = new Node(normLabel, currentNode, nodeNumber);
				nodeNumber++;
				currentNode = newNode;
			}
		}
		currentNode.addKeyword(keyword);
	}

	/**
	 * Add a {@link IKeyword}.
	 *
	 * @param keyword a keyword label.
	 * @param tokstop a {@link ITokenizerStopwords} to tokenize and normalize
	 *                keyword's label.
	 */
	public void addKeyword(String keyword, ITokenizerStopwords tokstop)
	{
		Keyword kw = new Keyword(keyword);
		addIKeyword(kw, tokstop);
	}

	/**
	 * Add a collection of {@link IKeyword}.
	 *
	 * @param keywords multiple {@link IKeyword}.
	 * @param tokstop  a {@link ITokenizerStopwords} to tokenize and normalize
	 *                 keyword's label.
	 */
	public void addKeywords(Iterable<? extends IKeyword> keywords, ITokenizerStopwords tokstop)
	{
		for (IKeyword keyword : keywords)
		{
			addIKeyword(keyword, tokstop);
		}
	}

	/**
	 * Construct the root node / initial state.
	 *
	 * @return a root node.
	 */
	protected Node buildRootNode()
	{
		return new Node("START_TOKEN", nodeRootNumber);
	}

	/**
	 * The initial state of the final-state automata is the rootNode of the trie.
	 *
	 * @return the rootNode
	 */
	public Node getInitialState()
	{
		return (rootNode);
	}

	/**
	 * Each node has a unique number.
	 *
	 * @return The number of nodes/states in the trie.
	 */
	public int getNumberOfNodes()
	{
		return nodeNumber;
	}
}
