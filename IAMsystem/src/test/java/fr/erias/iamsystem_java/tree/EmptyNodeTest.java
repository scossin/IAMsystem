package fr.erias.iamsystem_java.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmptyNodeTest
{

	private INode emptyNode;

	@BeforeEach
	void setUp() throws Exception
	{
		this.emptyNode = EmptyNode.EMPTYNODE;
	}

	@Test
	void testAncestors()
	{
		assertThrows(UnsupportedOperationException.class, () -> emptyNode.getAncestors());
	}

	@Test
	void testChildNodes()
	{
		assertThrows(UnsupportedOperationException.class, () -> emptyNode.getChildrenNodes());
	}

	@Test
	void testGoToNode()
	{
		assertEquals(this.emptyNode, this.emptyNode.gotoNode("anything"));
	}

	@Test
	void testHasTransitionTo()
	{
		assertFalse(this.emptyNode.hasTransitionTo("Anythign"));
	}

	@Test
	void testIsFinalState()
	{
		assertFalse(this.emptyNode.isAfinalState());
	}

	@Test
	void testKeywords()
	{
		assertThrows(UnsupportedOperationException.class, () -> emptyNode.getKeywords());
	}

	@Test
	void testNodeNumber()
	{
		assertThrows(UnsupportedOperationException.class, () -> emptyNode.getNodeNumber());
	}

	@Test
	void testParentNode()
	{
		assertThrows(UnsupportedOperationException.class, () -> emptyNode.getParentNode());
	}
}
