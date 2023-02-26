package fr.erias.iamsystem_java.matcher;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.Token;
import fr.erias.iamsystem_java.tree.INode;
import fr.erias.iamsystem_java.tree.Node;
import fr.erias.iamsystem_java.tree.Trie;

class LinkedStateTest
{

	private LinkedState<IToken> stateIns;
	private LinkedState<IToken> stateCard;

	@BeforeEach
	void setUp() throws Exception
	{
		Token tokenIns = new Token(0,0,"insuffisance","insuffisance", 0);
		Token tokenCard = new Token(1,1,"cardiaque","cardiaque", 1);
		Trie trie = new Trie();
		INode initialState = trie.getInitialState();
		Node nodeIns = new Node("insuffisance", initialState, 1);
		Node nodeCard = new Node("cardiaque", nodeIns, 2);
		this.stateIns = createState(null, nodeIns, tokenIns);
		this.stateCard = createState(stateIns, nodeCard, tokenCard);
	}
	
	static LinkedState<IToken> createState(LinkedState<IToken> parent, INode node, Token token) {
		List<String> algos = Arrays.asList();
		return new LinkedState<IToken>(parent, node, token, algos, 0);
	}

	@Test
	void testEquality()
	// Two states are equal if they have the same nodeNumber
	{
		assertNotEquals(stateIns, stateCard);
		Trie trie = new Trie();
		INode initialState = trie.getInitialState();
		Node nodeIns2 = new Node("insuffisance", initialState, 1);
		LinkedState<IToken> otherStateSameNum = createState(null, nodeIns2, null);
		assertEquals(stateIns, otherStateSameNum);
		Set<LinkedState<IToken>> states = new HashSet<LinkedState<IToken>>();
		states.add(stateIns);
		assertTrue(states.contains(otherStateSameNum));
	}
	
	@Test
	void testsStartState()
	// A start state has a null parent.
	{
		assertTrue(LinkedState.isStartState(stateIns));
		assertFalse(LinkedState.isStartState(stateCard));
	}
}
