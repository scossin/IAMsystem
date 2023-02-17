package fr.erias.iamsystem_java.tree;

import fr.erias.iamsystem_java.keywords.IKeyword;
import fr.erias.iamsystem_java.tokenize.AbstractTokNorm;
import fr.erias.iamsystem_java.tokenize.IToken;
import java.util.List;
import java.util.stream.Collectors;

/**
 * IAMsystem algorithm utilizes a finate-state automata. <br>
 * Each state is a node in a trie datastructure. <br>
 * The initial state is the root node. <br>
 * A transition between two states, with word w, is possible if nodes are connected in the trie.
 *
 * @author Sebastien Cossin
 */
public class Trie {

  /**
   * The empty is not in the trie. It represents the "empty" state. This emptyNode is returned when
   * no path is found in the trie.
   *
   * @param node a node of the trie
   * @return true if this node is the EmptyNode
   */
  public static boolean isTheEmptyNode(INode node) {
    return node == EmptyNode.EMPTYNODE;
  }

  // RootNode
  public static final int nodeRootNumber = 0;

  public static boolean isTheRootNode(INode node) {
    return node.getNodeNumber() == nodeRootNumber;
  }

  protected Node buildRootNode() {
    return new Node("START_TOKEN", nodeRootNumber);
  }

  private final Node rootNode;
  private int nodeNumber =
      1; // each node in the trie has a unique number. This variable counts every node.

  /**
   * Construct a trie to store a keywordinology. <br>
   * Each node is a state of a finite-state automata.
   */
  public Trie() {
    this.rootNode = buildRootNode();
  }

  /**
   * Add a keyword of a keywordinology. The trie creates as many nodes (= a token / state) as
   * needed.
   *
   * @param keyword A keyword of a keywordinology
   * @param tokenizer to tokenize the label of the keyword
   * @param normalizer to normalize the label of the keyword
   */
  public void addIKeyword(IKeyword keyword, AbstractTokNorm<? extends IToken> tokstop) {
    List<String> stringSeq =
        tokstop.tokenizeRmStopwords(keyword.label()).stream()
            .map(token -> token.normLabel())
            .collect(Collectors.toList());
    addIKeyword(keyword, stringSeq);
  }

  /**
   * add every keyword of a keywordinology
   *
   * @param keywordinology a {@link IKeywordinology}
   * @param tokenizer to tokenize the labels of the keywords
   * @param normalizer to normalize the labels of the keywords
   */
  public void addKeywords(
      Iterable<? extends IKeyword> keywords, AbstractTokNorm<? extends IToken> tokstop) {
    for (IKeyword keyword : keywords) {
      addIKeyword(keyword, tokstop);
    }
  }

  /**
   * Add a keyword of a keywordinology.
   *
   * @param keyword a keyword of a keywordinology
   * @param tokens a sequence of normalized token of the label of the keyword
   */
  public void addIKeyword(IKeyword keyword, List<String> stringSeq) {
    if (stringSeq.size() == 0) return;
    INode currentNode = rootNode;
    for (String normLabel : stringSeq) {
      if (currentNode.hasTransitionTo(normLabel)) {
        currentNode = currentNode.gotoNode(normLabel);
      } else {
        INode newNode = new Node(normLabel, currentNode, nodeNumber);
        nodeNumber++;
        currentNode = newNode;
      }
      currentNode.addKeyword(keyword);
    }
  }

  /**
   * The initial state of the final-state automata is the rootNode of the trie
   *
   * @return the rootNode
   */
  public Node getInitialState() {
    return (rootNode);
  }

  /**
   * Each node has a unique number. The trie keeps track of the number of nodes.
   *
   * @return The number of nodes/states in the trie
   */
  public int getNumberOfNodes() {
    return nodeNumber;
  }
}
