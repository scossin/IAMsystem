package fr.erias.IAMsystem.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.erias.IAMsystem.terminology.Term;

/**
 * The empty node doesn't belong to a trie (no parentNode, no childNode) 
 * It represents the emptyset in the set of states of a final-state automata. 
 * @author Sebastien Cossin
 */
public class EmptyNode implements INode {

	public static final EmptyNode EMPTYNODE = new EmptyNode();
	
	private EmptyNode() {};
	
	@Override
	public boolean hasTransitionTo(String token) {
		return false;
	}

	@Override
	public INode gotoNode(String token) {
		return EMPTYNODE;
	}

	@Override
	public INode gotoNode(List<String> tokens) {
		return EMPTYNODE;
	}

	@Override
	public boolean isAfinalState() {
		return false;
	}

	@Override
	public Term getTerm() {
		return null;
	}

	@Override
	public int getNodeNumber() {
		return -1;
	}

	private static final Set<INode> noNodes = new HashSet<INode>();
	
	@Override
	public Set<INode> gotoNodes(Set<List<String>> setOfsynonyms) {
		return noNodes;
	}

	@Override
	public void setTerm(Term term) {
		
	}

	@Override
	public void addChildNode(INode node) {
		
	}

	@Override
	public String getToken() {
		return null;
	}

	@Override
	public INode getParentNode() {
		return null;
	}

	@Override
	public Collection<INode> getChildNodes() {
		return new ArrayList<INode>(0);
	}
}
