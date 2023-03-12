package fr.erias.iamsystem_java.matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.tokenize.Token;
import fr.erias.iamsystem_java.tree.INode;
import fr.erias.iamsystem_java.tree.Node;
import fr.erias.iamsystem_java.tree.Trie;

class StateTransitionTest
{

	static StateTransition createTrans(StateTransition parent, INode node, Token token)
	{
		List<String> algos = Arrays.asList();
		return new StateTransition(parent, node, token, algos, 0);
	}

	private StateTransition stateIns;

	private StateTransition stateCard;

	@BeforeEach
	void setUp() throws Exception
	{
		Token tokenIns = new Token(0, 0, "insuffisance", "insuffisance", 0);
		Token tokenCard = new Token(1, 1, "cardiaque", "cardiaque", 1);
		Trie trie = new Trie();
		INode initialState = trie.getInitialState();
		Node nodeIns = new Node("insuffisance", initialState, 1);
		Node nodeCard = new Node("cardiaque", nodeIns, 2);
		this.stateIns = createTrans(null, nodeIns, tokenIns);
		this.stateCard = createTrans(stateIns, nodeCard, tokenCard);
	}

	@Test
	void testEquality()
	// Two states are equal if they have the same nodeNumber
	{
		assertNotEquals(stateIns, stateCard);
		Trie trie = new Trie();
		INode initialState = trie.getInitialState();
		Node nodeIns2 = new Node("insuffisance", initialState, 1);
		StateTransition otherStateSameNum = createTrans(null, nodeIns2, null);
		assertEquals(stateIns, otherStateSameNum);
		Set<StateTransition> transitions = new HashSet<StateTransition>();
		transitions.add(stateIns);
		assertTrue(transitions.contains(otherStateSameNum));
	}

	@Test
	void testsStartState()
	// A start state has a null parent.
	{
		assertTrue(StateTransition.isFirstTrans(stateIns));
		assertFalse(StateTransition.isFirstTrans(stateCard));
	}
}
