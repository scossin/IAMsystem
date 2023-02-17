package fr.erias.iamsystem_java.tree;

import static org.junit.jupiter.api.Assertions.*;

import fr.erias.iamsystem_java.stopwords.Stopwords;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.NormFunctions;
import fr.erias.iamsystem_java.tokenize.SplitFunctions;
import fr.erias.iamsystem_java.tokenize.TokStopImp;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import fr.erias.iamsystem_java.tokenize.TokenizerImp;
import fr.erias.iamsystem_java.utils.MockData;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrieTest {

  private ITokenizer<IToken> tokenizer;
  private Stopwords stopwords;
  private TokStopImp<IToken> tokstop;

  @BeforeEach
  void setUp() throws Exception {
    this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
    this.stopwords = new Stopwords();
    this.tokstop = new TokStopImp<IToken>(tokenizer, stopwords);
  }

  @Test
  void testTrieNumberOfNodes() {
    // Number of tokens in keywords.
    Trie trie = new Trie();
    assertEquals(trie.getNumberOfNodes(), 1);
    trie.addKeywords(MockData.getICG(), tokstop);
    assertEquals(trie.getNumberOfNodes(), 4);
  }

  @Test
  void testTrieInitialState() {
    // Number of tokens in keywords.
    Trie trie = new Trie();
    Trie.isTheRootNode(trie.getInitialState());
  }

  @Test
  void testTrieCheckTransitionFromRootNode() {
    // insuffisance ventriculaire gauche': START_TOKEN -> 'insuffisance'
    Trie trie = new Trie();
    trie.addKeywords(MockData.getICG(), tokstop);
    assertTrue(trie.getInitialState().hasTransitionTo("insuffisance"));
  }

  @Test
  void testTrieWithStopwords() {
    // remove insuffisance to check transition is to next token 'cardiaque'.
    Trie trie = new Trie();
    this.stopwords.add(Arrays.asList("insuffisance"));
    trie.addKeywords(MockData.getICG(), tokstop);
    assertTrue(!trie.getInitialState().hasTransitionTo("insuffisance"));
    assertTrue(trie.getInitialState().hasTransitionTo("cardiaque"));
  }

  @Test
  void testTrieAllStopwords() {
    // remove all string to check no transition(only the root node is present).
    Trie trie = new Trie();
    this.stopwords.add(Arrays.asList("insuffisance", "cardiaque", "gauche"));
    trie.addKeywords(MockData.getICG(), tokstop);
    assertEquals(trie.getNumberOfNodes(), 1);
  }

  @Test
  void testTrieChangeNormF() {
    // Check tokens stored depend on the normalizing function of tokenizer.
    Trie trie = new Trie();
    ITokenizer<IToken> tokenizer =
        new TokenizerImp(NormFunctions.noNormalization, SplitFunctions.splitAlphaNum);
    TokStopImp<IToken> tokstop = new TokStopImp<IToken>(tokenizer, stopwords);
    trie.addKeywords(MockData.getICG(), tokstop);
    assertEquals(trie.getNumberOfNodes(), 4);
    System.out.println();
    assertTrue(!trie.getInitialState().hasTransitionTo("insuffisance"));
    assertTrue(trie.getInitialState().hasTransitionTo("Insuffisance"));
  }

  //    trie = Trie()
  //    self.assertEqual(1, trie.get_number_of_nodes())
  //    # first ent
  //    ent = Entity("Insuffisance Cardiaque", "I50.9")
  //    tokens = ["insuffisance", "cardiaque"]
  //    trie.add_keyword_with_tokens(keyword=ent, tokens=tokens)
  //    self.assertEqual(3, trie.get_number_of_nodes())  # one per token
  //    # second ent
  //    ent = Entity("Insuffisance Cardiaque Gauche", "I50.1")
  //    tokens = ["insuffisance", "cardiaque", "gauche"]
  //    trie.add_keyword_with_tokens(keyword=ent, tokens=tokens)
  //    self.assertEqual(3 + 1, trie.get_number_of_nodes())  # new node gauche

}
