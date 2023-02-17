package fr.erias.iamsystem_java.tree;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmptyNodeTest {

  private INode emptyNode;

  @BeforeEach
  void setUp() throws Exception {
    this.emptyNode = EmptyNode.EMPTYNODE;
  }

  @Test
  void testIsFinalState() {
    assertFalse(this.emptyNode.isAfinalState());
  }

  @Test
  void testHasTransitionTo() {
    assertFalse(this.emptyNode.hasTransitionTo("Anythign"));
  }

  @Test
  void testGoToNode() {
    assertEquals(this.emptyNode, this.emptyNode.gotoNode("anything"));
  }

  @Test
  void testNodeNumber() {
    assertThrows(UnsupportedOperationException.class, () -> emptyNode.getNodeNumber());
  }

  @Test
  void testParentNode() {
    assertThrows(UnsupportedOperationException.class, () -> emptyNode.getParentNode());
  }

  @Test
  void testAncestors() {
    assertThrows(UnsupportedOperationException.class, () -> emptyNode.getAncestors());
  }

  @Test
  void testChildNodes() {
    assertThrows(UnsupportedOperationException.class, () -> emptyNode.getChildNodes());
  }

  @Test
  void testKeywords() {
    assertThrows(UnsupportedOperationException.class, () -> emptyNode.getKeywords());
  }
}
