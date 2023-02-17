package fr.erias.iamsystem_java.tree;

import fr.erias.iamsystem_java.keywords.IKeyword;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The empty node doesn't belong to a trie (no parentNode, no childNode) It represents the emptyset
 * in the set of states of a final-state automata.
 *
 * @author Sebastien Cossin
 */
public class EmptyNode implements INode {

  public static final EmptyNode EMPTYNODE = new EmptyNode();

  private EmptyNode() {}
  ;

  private static final Set<INode> noNodes = new HashSet<INode>();

  @Override
  public boolean hasTransitionTo(String token) {
    return false;
  }

  @Override
  public INode gotoNode(String token) {
    return EMPTYNODE;
  }

  @Override
  public INode gotoNode(List<String> tokens) {
    return EMPTYNODE;
  }

  @Override
  public boolean isAfinalState() {
    return false;
  }

  @Override
  public Set<INode> gotoNodes(Set<List<String>> setOfsynonyms) {
    return noNodes;
  }

  /******************** Return an exception  if any method below is called *************/

  @Override
  public Collection<IKeyword> getKeywords() {
    throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
  }

  @Override
  public int getNodeNumber() {
    throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
  }

  @Override
  public void addKeyword(IKeyword term) {
    throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
  }

  @Override
  public void addChildNode(INode node) {
    throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
  }

  @Override
  public String getToken() {
    throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
  }

  @Override
  public INode getParentNode() {
    throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
  }

  @Override
  public Collection<INode> getChildNodes() {
    throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
  }

  @Override
  public Collection<INode> getAncestors() {
    throw new UnsupportedOperationException("EmptyNode don't implement this operation.");
  }
}
