package fr.erias.iamsystem_java.tree;

import static org.junit.jupiter.api.Assertions.*;

import fr.erias.iamsystem_java.keywords.Entity;
import fr.erias.iamsystem_java.keywords.IEntity;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NodeTest {
  //
  //    trie = Trie()
  //    root_node = trie.get_initial_state()
  //    self.ins_node = Node(
  //        token="insuffisance", node_num=1, parent_node=root_node
  //    )
  //    self.card_node = Node(
  //        token="cardiaque", node_num=2, parent_node=self.ins_node
  //    )
  //    self.node_gauche = Node(
  //        token="gauche", node_num=3, parent_node=self.card_node
  //    )

  private Trie trie;
  private Node insNode;
  private Node cardNode;
  private Node gaucheNode;
  private Node root;

  @BeforeEach
  void setUp() throws Exception {
    this.trie = new Trie();
    this.root = this.trie.getInitialState();
    this.insNode = new Node("insuffisance", root, 1);
    this.cardNode = new Node("cardiaque", insNode, 2);
    this.gaucheNode = new Node("gauche", cardNode, 3);
  }

  @Test
  void testKeywordNotOverriden() {
    // Adding the same ent to a node doesn't override it.
    IEntity ent = new Entity("Insuffisance Cardiaque Gauche", "XXX");
    Node gaucheNode = new Node("gauche", this.insNode, 3);
    gaucheNode.addKeyword(ent);
    gaucheNode.addKeyword(ent);
    assertEquals(2, gaucheNode.getKeywords().size());
    //		this.trie.addIKeyword(ent, MockData.getFrenchTokStop());
    //		this.trie.addIKeyword(ent, MockData.getFrenchTokStop());
    //		fail("Not yet implemented");
  }

  @Test
  void testHasTransitionTo() {
    assertTrue(this.insNode.hasTransitionTo("cardiaque"));
    assertFalse(this.insNode.hasTransitionTo("gauche"));
    assertTrue(this.cardNode.hasTransitionTo("gauche"));
  }

  @Test
  void testGetAncestors() {
    assertEquals(2, this.gaucheNode.getAncestors().size());
    assertTrue(this.gaucheNode.getAncestors().contains(this.insNode));
    assertTrue(this.gaucheNode.getAncestors().contains(this.cardNode));
    assertFalse(this.gaucheNode.getAncestors().contains(this.root));
  }

  @Test
  void testGetChildNodes() {
    assertEquals(1, this.insNode.getChildNodes().size());
    assertTrue(this.insNode.getChildNodes().contains(this.cardNode));
  }

  @Test
  void testGoToNodeDeadEnd() {
    assertEquals(EmptyNode.EMPTYNODE, this.insNode.gotoNode("DoesNotExist"));
    assertEquals(EmptyNode.EMPTYNODE, this.insNode.gotoNode(Arrays.asList("one", "two")));
  }

  @Test
  void testGoToNode() {
    assertEquals(this.gaucheNode, this.insNode.gotoNode(Arrays.asList("cardiaque", "gauche")));
  }

  @Test
  void testIsAFinalState() {
    assertFalse(this.gaucheNode.isAfinalState());
    IEntity ent = new Entity("Insuffisance Cardiaque Gauche", "XXX");
    this.gaucheNode.addKeyword(ent);
    assertTrue(this.gaucheNode.isAfinalState());
  }

  @Test
  void testGetKeywords() {
    assertEquals(0, this.gaucheNode.getKeywords().size());
    IEntity ent = new Entity("Insuffisance Cardiaque Gauche", "XXX");
    this.gaucheNode.addKeyword(ent);
    this.gaucheNode.addKeyword(ent);
    assertEquals(2, this.gaucheNode.getKeywords().size());
  }
}
