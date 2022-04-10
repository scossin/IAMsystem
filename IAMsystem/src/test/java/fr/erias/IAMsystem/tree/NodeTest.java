package fr.erias.IAMsystem.tree;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import fr.erias.IAMsystem.terminology.Term;

public class NodeTest {
    
	@Test
    public void nodeBuildTest() {
		Term term = new Term("insuffisance cardiaque aigue", "I50");
		String token1 = "insuffisance";
		String token2 = "cardiaque";
		String token3 = "aigue";
		
		Trie trie = new Trie();
		Node rootNode = trie.buildRootNode();
		assertTrue(Trie.isTheRootNode(rootNode));
		assertFalse(Trie.isTheEmptyNode(rootNode));
		assertFalse(Trie.isTheRootNode(EmptyNode.EMPTYNODE));
		Node node1 = new Node(token1, rootNode, 1);
		Node node2 = new Node(token2, node1, 2);
		Node node3 = new Node(token3, node2, 3);
		node3.setTerm(term);
		
		String[] tokens = {token1, token2, token3};
		Node node = (Node) rootNode.gotoNode(Arrays.asList(tokens));
		assertFalse(Trie.isTheEmptyNode(node));
		assertTrue(node.getTokenSequence().toString().equals("[insuffisance, cardiaque, aigue]"));
		assertTrue(node3 == node);
		assertTrue(node.isAfinalState());
    }
}
