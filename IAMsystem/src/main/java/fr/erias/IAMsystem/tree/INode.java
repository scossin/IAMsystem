package fr.erias.IAMsystem.tree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.erias.IAMsystem.terminology.Term;

public interface INode {
	
	public INode getParentNode();
	
	public void addChildNode(INode node);
	
	public boolean hasTransitionTo(String token);
	
	public INode gotoNode(String token);
	
	public INode gotoNode(List<String> tokens);
	
	public Set<INode> gotoNodes(HashSet<String[]> setOfsynonyms);
	
	public boolean isAfinalState();
	
	public Term getTerm();
	
	public void setTerm(Term term);
	
	public int getNodeNumber();
	
	public String getToken();

}
