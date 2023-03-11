package fr.erias.iamsystem_java.tree;

import java.util.Collection;
import java.util.List;

import fr.erias.iamsystem_java.keywords.IKeyword;

public interface INode
{

	/**
	 * Add a child node (e.g. the next token of a keyword).
	 *
	 * @param node a child node
	 */
	public void addChildNode(INode node);

	/**
	 * Add a term to this node.
	 *
	 * @param term a {@link Term} of a terminology
	 */
	public void addKeyword(IKeyword keyword);

	/**
	 * Retrieve the parents.
	 *
	 * @return parents of this node
	 */
	public Collection<INode> getAncestors();

	/**
	 * Get the children nodes.
	 *
	 * @return a collection of child nodes of a node, empty if it's a leaf.
	 */
	public Collection<INode> getChildrenNodes();

	/**
	 * Retrieve the keywords stored by this node.
	 *
	 * @return one or multiple {@link IKeyword} types.
	 */
	public Collection<IKeyword> getKeywords();

	/**
	 * Return this node number.
	 *
	 * @return A unique number that identifies this node.
	 */
	public int getNodeNumber();

	/**
	 * Retrieve the parent of this node.
	 *
	 * @return the parent of this node.
	 */
	public INode getParentNode();

	/**
	 * The token used by the algorithm to transit from node to node.
	 *
	 * @return the token's label, in general the normalized label.
	 */
	public String getToken();

	/**
	 * From this node go to another node with child relations.
	 *
	 * @param tokens An ordered sequence of tokens.
	 * @return
	 */
	public INode gotoNode(List<String> tokens);

	/**
	 * From this node go to another node with child relations.
	 *
	 * @param token a normalized string
	 * @return a {@link Node} if a path exists in the trie or the {@link EmptyNode}.
	 */
	public INode gotoNode(String token);

	/**
	 * From this node go to another node with child relations.
	 *
	 * @param tokens a sequence of normalized string
	 * @return a {@link Node} if a path exists in the trie or the {@link EmptyNode}.
	 */
	public INode gotoNode(String[] tokens);

	/**
	 * Check if, from this node, a transition is possible to another node. Each node
	 * is a state in a final-state automata. A transition is possible iff the token
	 * is a path from this state to another node of a trie.
	 *
	 * @param token a normalized string
	 * @return true if a path exists
	 */
	public boolean hasTransitionTo(String token);

	/**
	 * A node is a final state iff it stores a keyword. It's not always a leaf in
	 * the trie.
	 *
	 * @return true if it's a final state.
	 */
	public boolean isAfinalState();
}
