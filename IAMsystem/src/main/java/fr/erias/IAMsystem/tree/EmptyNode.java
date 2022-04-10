package fr.erias.IAMsystem.tree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.erias.IAMsystem.terminology.Term;

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

	@Override
	public Set<INode> gotoNodes(HashSet<String[]> setOfsynonyms) {
		return new HashSet<INode>();
	}

	@Override
	public void setTerm(Term term) {
		
	}

	@Override
	public void addChildNode(INode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INode getParentNode() {
		return null;
	}
}
