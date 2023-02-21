package fr.erias.iamsystem_java.matcher;

import java.util.Collection;

import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tree.INode;

public class TransitionState<T extends IToken>
{

	public static boolean isStartState(TransitionState<? extends IToken> state)
	{
		return state.parent == null;
	}

	private final INode node;
	private final TransitionState<T> parent;
	private final Collection<String> algos;

	private final T token;

	public TransitionState(TransitionState<T> parent, INode node, T token, Collection<String> algos)
	{
		this.node = node;
		this.parent = parent;
		this.algos = algos;
		this.token = token;
	}

	public Collection<String> getAlgos()
	{
		return algos;
	}

	public INode getNode()
	{
		return node;
	}

	public TransitionState<T> getParent()
	{
		return parent;
	}

	public T getToken()
	{
		return token;
	}
}
