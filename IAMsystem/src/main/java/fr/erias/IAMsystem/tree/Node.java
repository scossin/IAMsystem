package fr.erias.IAMsystem.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.erias.IAMsystem.terminology.Term;

/**
 * Nodes of a trie. 
 * @author Sebastien Cossin
 *
 */
public class Node implements INode {
	
	private final INode parentNode; 
	private final int nodeNumber; // in a trie, each node is unique and assigned a unique number
	private final String token;
	private Term term;
	private final Map<String, INode> childNodes = new HashMap<String, INode>();
	
	Node(String token, int nodeNumber) {
		this.token = token;
		this.parentNode = null;
		this.nodeNumber = nodeNumber;
	}
	
	public Node(String token, INode parentNode, int nodeNumber) {
		this.token = token;
		this.parentNode = parentNode;
		this.nodeNumber = nodeNumber;
		parentNode.addChildNode(this);
	}
	
	public boolean hasTransitionTo(String token) {
		return childNodes.containsKey(token);
	}
	
	public INode gotoNode(String token) {
		return childNodes.getOrDefault(token, EmptyNode.EMPTYNODE);
	}
	
	public INode gotoNode(List<String> tokens) {
		INode node = this;
		for (String token : tokens) {
			node = node.gotoNode(token);
		}
		return(node);
	}
	
	public Set<INode> gotoNodes(Set<List<String>> setOfsynonyms){
		Set<INode> nodes = new HashSet<INode>();
		for (List<String> synonyms : setOfsynonyms) {
			INode node = gotoNode(synonyms);
			nodes.add(node);
		}
		nodes.remove(EmptyNode.EMPTYNODE);
		return(nodes);
	}
	
	public void addChildNode(INode childNode) {
		String token = childNode.getToken();
		childNodes.put(token, childNode);
	}
	
	public boolean isAfinalState() {
		return term != null;
	}
	
	public Term getTerm() {
		return(term);
	}
	
	public void setTerm(Term term) {
		this.term = term;
	}
	
	public String getToken() {
		return token;
	}

	public INode getParentNode() {
		return parentNode;
	}
	
	public List<String> getTokenSequence() {
		List<String> sequence = new ArrayList<String>();
		sequence.add(token);
		INode currentNode = this;
		INode parentNode = null;
		while(!Trie.isTheRootNode(parentNode = currentNode.getParentNode())) {
			sequence.add(parentNode.getToken());
			currentNode = parentNode;
		}
		Collections.reverse(sequence);
		return(sequence);
	}
	
	public int getNodeNumber() {
		return nodeNumber;
	}
	
	@Override
	public String toString() {
		List<String> sequence = getTokenSequence();
		return(sequence.toString());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) { 
			return true; 
		} 

		if (!(o instanceof Node)) { 
			return false; 
		} 

		Node c = (Node) o; 
		return this.getNodeNumber() == c.getNodeNumber();
	}
	
	@Override
    public int hashCode() {
        return(this.getNodeNumber());
    }
}
