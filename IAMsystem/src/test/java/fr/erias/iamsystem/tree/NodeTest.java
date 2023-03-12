package fr.erias.iamsystem.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InaccessibleObjectException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem.keywords.Entity;
import fr.erias.iamsystem.keywords.IEntity;
import fr.erias.iamsystem.tree.EmptyNode;
import fr.erias.iamsystem.tree.Node;
import fr.erias.iamsystem.tree.Trie;

class NodeTest
{

	private Trie trie;
	private Node insNode;
	private Node cardNode;
	private Node gaucheNode;
	private Node root;

	@BeforeEach
	void setUp() throws Exception
	{
		this.trie = new Trie();
		this.root = this.trie.getInitialState();
		this.insNode = new Node("insuffisance", root, 1);
		this.cardNode = new Node("cardiaque", insNode, 2);
		this.gaucheNode = new Node("gauche", cardNode, 3);
	}

	@Test
	void testGetAncestors()
	{
		assertEquals(2, this.gaucheNode.getAncestors().size());
		assertTrue(this.gaucheNode.getAncestors().contains(this.insNode));
		assertTrue(this.gaucheNode.getAncestors().contains(this.cardNode));
		assertFalse(this.gaucheNode.getAncestors().contains(this.root));
	}

	@Test
	void testGetChildNodes()
	{
		assertEquals(1, this.insNode.getChildrenNodes().size());
		assertTrue(this.insNode.getChildrenNodes().contains(this.cardNode));
	}

	@Test
	void testGetKeywords()
	{
		assertThrows(InaccessibleObjectException.class, () -> this.gaucheNode.getKeywords());
		IEntity ent = new Entity("Insuffisance Cardiaque Gauche", "XXX");
		this.gaucheNode.addKeyword(ent);
		this.gaucheNode.addKeyword(ent);
		assertEquals(2, this.gaucheNode.getKeywords().size());
	}

	@Test
	void testGoToNode()
	{
		assertEquals(this.gaucheNode, this.insNode.gotoNode(new String[] { "cardiaque", "gauche" }));
	}

	@Test
	void testGoToNodeDeadEnd()
	{
		assertEquals(EmptyNode.EMPTYNODE, this.insNode.gotoNode("DoesNotExist"));
		assertEquals(EmptyNode.EMPTYNODE, this.insNode.gotoNode(new String[] { "one", "two" }));
	}

	@Test
	void testHasTransitionTo()
	{
		assertTrue(this.insNode.hasTransitionTo("cardiaque"));
		assertFalse(this.insNode.hasTransitionTo("gauche"));
		assertTrue(this.cardNode.hasTransitionTo("gauche"));
	}

	@Test
	void testIsAFinalState()
	{
		assertFalse(this.gaucheNode.isAfinalState());
		IEntity ent = new Entity("Insuffisance Cardiaque Gauche", "XXX");
		this.gaucheNode.addKeyword(ent);
		assertTrue(this.gaucheNode.isAfinalState());
	}

	@Test
	void testKeywordNotOverriden()
	{
		// Adding the same ent to a node doesn't override it.
		IEntity ent = new Entity("Insuffisance Cardiaque Gauche", "XXX");
		Node gaucheNode = new Node("gauche", this.insNode, 3);
		gaucheNode.addKeyword(ent);
		gaucheNode.addKeyword(ent);
		assertEquals(2, gaucheNode.getKeywords().size());
	}
}
