package fr.erias.iamsystem_java.tree;

import fr.erias.iamsystem_java.keywords.IKeyword;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface INode {

  /**
   * Retrieve the parent of this node
   *
   * @return the parentNode: the (i-1)th node or the rootNode if this node stores the first token of
   *     a term
   */
  public INode getParentNode();

  /**
   * Add a child node (the next token of a term)
   *
   * @param node a child node
   */
  public void addChildNode(INode node);

  /**
   * Every node has a token coming from the terms of a terminology
   *
   * @return its token
   */
  public String getToken();

  /**
   * Check if, from this node, a transition is possible to another node. Each node is a state in a
   * final-state automata. A transition is possible iff the token is a path from this state to a
   * leaf of a trie
   *
   * @param token a normalized string
   * @return true if a path exists
   */
  public boolean hasTransitionTo(String token);

  /**
   * From this node go to another node with child relations
   *
   * @param token a normalized string
   * @return a {@link Node} if a path exists in the trie or the {@link EmptyNode}
   */
  public INode gotoNode(String token);

  /**
   * From this node go to another node with child relations
   *
   * @param tokens a sequence of normalized string
   * @return a {@link Node} if a path exists in the trie or the {@link EmptyNode}
   */
  public INode gotoNode(List<String> tokens);

  /**
   * For each unique sequence of tokens returns a node
   *
   * @param setOfsynonyms a set of sequences of tokens
   * @return a set of {@link Node}
   */
  public Set<INode> gotoNodes(Set<List<String>> setOfsynonyms);

  /**
   * A node is a final state iff it's a leaf in the trie. Then it has a term.
   *
   * @return true if it's a final
   */
  public boolean isAfinalState();

  /**
   * Retrieve the term stored by this node (only if it's a leaf / final state)
   *
   * @return the term of a Terminology if it's a final state or NULL
   */
  public Collection<IKeyword> getKeywords();

  /**
   * Add a term to this node
   *
   * @param term a {@link Term} of a terminology
   */
  public void addKeyword(IKeyword keyword);

  /**
   * Each node has a unique number in a trie
   *
   * @return its number
   */
  public int getNodeNumber();

  /**
   * get the child nodes
   *
   * @return a collection of child nodes of a node, empty if it's a leaf
   */
  public Collection<INode> getChildNodes();

  public Collection<INode> getAncestors();
}