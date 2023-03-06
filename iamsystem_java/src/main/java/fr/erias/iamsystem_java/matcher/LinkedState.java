package fr.erias.iamsystem_java.matcher;

import java.util.Collection;

import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tree.INode;

public class LinkedState
{

	public static boolean isStartState(LinkedState state)
	{
		return state.parent == null;
	}

	private final INode node;
	private final LinkedState parent;
	private final Collection<String> algos;
	private final IToken token;
	private final int wBucket;

	public LinkedState(LinkedState parent, INode node, IToken token, Collection<String> algos, int wBucket)
	{
		this.node = node;
		this.parent = parent;
		this.algos = algos;
		this.token = token;
		this.wBucket = wBucket;
	}

	@Override
	public boolean equals(Object o)
	{
		LinkedState c = (LinkedState) o;
		return this.node.getNodeNumber() == c.node.getNodeNumber();
	}

	public Collection<String> getAlgos()
	{
		return algos;
	}

	public int getId()
	{
		return this.node.getNodeNumber();
	}

	public INode getNode()
	{
		return node;
	}

	public LinkedState getParent()
	{
		return parent;
	}

	public IToken getToken()
	{
		return token;
	}

	public int getwBucket()
	{
		return wBucket;
	}

	@Override
	public int hashCode()
	{
		return (this.node.getNodeNumber());
	}

	public boolean isObsolete(int countNotStopWord, int w)
	{
		if (isStartState(this))
			return false;
		int distance2currentToken = countNotStopWord - this.wBucket;
		return w - distance2currentToken < 0;
	}
}
