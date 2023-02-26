package fr.erias.iamsystem_java.matcher;

import java.util.Collection;

import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tree.INode;

public class LinkedState<T extends IToken>
{

	public static boolean isStartState(LinkedState<? extends IToken> state)
	{
		return state.parent == null;
	}

	private final INode node;
	private final LinkedState<T> parent;
	private final Collection<String> algos;

	private final T token;
	private final int wBucket;

	public LinkedState(LinkedState<T> parent, INode node, T token, Collection<String> algos, int wBucket)
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

	public INode getNode()
	{
		return node;
	}

	public LinkedState<T> getParent()
	{
		return parent;
	}

	public T getToken()
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
}
