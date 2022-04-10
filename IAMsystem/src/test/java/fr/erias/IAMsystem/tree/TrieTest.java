package fr.erias.IAMsystem.tree;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.normalizer.Normalizer;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizer.TokenizerWhiteSpace;

public class TrieTest {
    
	@Test
    public void trieBuildTest() {
		INormalizer normalizer = new Normalizer();
		ITokenizer tokenizer = new TokenizerWhiteSpace();
		Trie trie = new Trie();
		Term term = new Term("insuffisance cardiaque aigue congestive", "I51");
		trie.addTerm(term, tokenizer, normalizer);
		assertTrue(trie.getMaxNodeNumber() == 5);
		
		String[] tokens = {"insuffisance","cardiaque","aigue"};
		Node nodeAigue = (Node) trie.getInitialState().gotoNode(List.of(tokens));
		assertTrue(Trie.isTheEmptyNode(nodeAigue) == false); 
		assertTrue(nodeAigue.isAfinalState() == false); // congestive is
		
		Term term1 = new Term("insuffisance cardiaque aigue", "I50");
		trie.addTerm(term1, tokenizer, normalizer);
		assertTrue(nodeAigue.isAfinalState() == true);
		assertTrue(nodeAigue.getTerm().getCode().equals("I50"));
		assertTrue(trie.getMaxNodeNumber() == 5);
		
		String[] tokens2 = {"insuffisance","cardiaqu"};
		INode empty = trie.getInitialState().gotoNode(List.of(tokens2));
		assertTrue(Trie.isTheEmptyNode(empty) == true);
    }
}
