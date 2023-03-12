package fr.erias.iamsystem_java.tree;

import java.util.Collection;
import java.util.List;

import fr.erias.iamsystem_java.keywords.IKeyword;

/**
 * The empty node represents the empty set in the set of states of a final-state
 * automata.
 *
 * @author Sebastien Cossin
 */
public class EmptyNode implements INode
{

	public static final EmptyNode EMPTYNODE = new EmptyNode();

	private EmptyNode()
	{
	}

	@Override
	public void addChildNode(INode node)
	{
		throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
	}

	@Override
	public void addKeyword(IKeyword term)
	{
		throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
	}

	@Override
	public Collection<INode> getAncestors()
	{
		throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
	}

	@Override
	public Collection<INode> getChildrenNodes()
	{
		throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
	}

	/********************
	 * Return an exception if any method below is called
	 *************/

	@Override
	public Collection<IKeyword> getKeywords()
	{
		throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
	}

	@Override
	public int getNodeNumber()
	{
		throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
	}

	@Override
	public INode getParentNode()
	{
		throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
	}

	@Override
	public String getToken()
	{
		throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
	}

	@Override
	public INode gotoNode(List<String> tokens)
	{
		return EMPTYNODE;
	}

	@Override
	public INode gotoNode(String token)
	{
		return EMPTYNODE;
	}

	@Override
	public INode gotoNode(String[] tokens)
	{
		return EMPTYNODE;
	}

	@Override
	public boolean hasTransitionTo(String token)
	{
		return false;
	}

	@Override
	public boolean isAfinalState()
	{
		return false;
	}
}
