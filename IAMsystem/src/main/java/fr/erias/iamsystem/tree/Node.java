package fr.erias.iamsystem.tree;

import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.erias.iamsystem.keywords.IKeyword;

/**
 * This class is used to build the nodes of a {@link Trie}.
 *
 * @author Sebastien Cossin
 */
public class Node implements INode
{

	private final INode parentNode;
	private final int nodeNumber; // in a trie, each node is unique and is assigned a unique number
	private final String token; // each node has a token (not unique in the trie)
	private List<IKeyword> keywords = null;
	private final Map<String, INode> childNodes = new HashMap<String, INode>(); // 0 if the node is a leaf

	/**
	 * Node constructor
	 *
	 * @param token      a token of a term
	 * @param parentNode For the ith token of a term, the parentNode is the (i-1)th
	 *                   token of the term. The rootNode if the token is the first
	 *                   token of the term.
	 * @param nodeNumber a unique number that identifies a node in the trie
	 */
	public Node(String token, INode parentNode, int nodeNumber)
	{
		this.token = token;
		this.parentNode = parentNode;
		this.nodeNumber = nodeNumber;
		parentNode.addChildNode(this);
	}

	// package private used to build the rootNode
	// (the rootNode has no parentNode so parentNode.addChildNode throws a
	// NULLException)
	Node(String token, int nodeNumber)
	{
		this.token = token;
		this.parentNode = null;
		this.nodeNumber = nodeNumber;
	}

	@Override
	public void addChildNode(INode childNode)
	{
		String token = childNode.getToken();
		childNodes.put(token, childNode);
	}

	@Override
	public void addKeyword(IKeyword keyword)
	{
		if (keywords == null)
			keywords = new ArrayList<IKeyword>(0);
		keywords.add(keyword);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}

		if (!(o instanceof Node))
		{
			return false;
		}

		Node c = (Node) o;
		return this.getNodeNumber() == c.getNodeNumber();
	}

	@Override
	public Collection<INode> getAncestors()
	{
		List<INode> ancestors = new ArrayList<INode>();
		INode ancest = this.parentNode;
		while (!Trie.isTheRootNode(ancest))
		{
			ancestors.add(ancest);
			ancest = ancest.getParentNode();
		}
		return ancestors;
	}

	@Override
	public Collection<INode> getChildrenNodes()
	{
		return this.childNodes.values();
	}

	@Override
	public Collection<IKeyword> getKeywords()
	{
		if (keywords == null)
		{
			throw new InaccessibleObjectException("Can't access Keywords if not a final state");
		}
		return (keywords);
	}

	/** Get the unique number that identifies this node in the trie */
	@Override
	public int getNodeNumber()
	{
		return nodeNumber;
	}

	@Override
	public INode getParentNode()
	{
		return parentNode;
	}

	@Override
	public String getToken()
	{
		return token;
	}

	/**
	 * Retrieve all the tokens from the root to this node
	 *
	 * @return the sequence of tokens
	 */
	public List<String> getTokenSequence()
	{
		List<String> sequence = new ArrayList<String>();
		sequence.add(token);
		INode currentNode = this;
		INode parentNode = null;
		while (!Trie.isTheRootNode(parentNode = currentNode.getParentNode()))
		{
			sequence.add(parentNode.getToken());
			currentNode = parentNode;
		}
		Collections.reverse(sequence);
		return (sequence);
	}

	@Override
	public INode gotoNode(List<String> tokens)
	{
		INode node = this;
		for (String token : tokens)
		{
			node = node.gotoNode(token);
		}
		return (node);
	}

	@Override
	public INode gotoNode(String token)
	{
		return childNodes.getOrDefault(token, EmptyNode.EMPTYNODE);
	}

	@Override
	public INode gotoNode(String[] tokens)
	{
		INode node = this;
		for (String token : tokens)
		{
			node = node.gotoNode(token);
		}
		return (node);
	}

	@Override
	public int hashCode()
	{
		return (this.getNodeNumber());
	}

	@Override
	public boolean hasTransitionTo(String token)
	{
		return childNodes.containsKey(token);
	}

	@Override
	public boolean isAfinalState()
	{
		return keywords != null;
	}

	@Override
	public String toString()
	{
		List<String> sequence = getTokenSequence();
		return (sequence.toString());
	}
}
