package fr.erias.iamsystem_java.stopwords;

import static org.junit.jupiter.api.Assertions.*;

import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.Token;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StopwordsTest {

  @BeforeEach
  void setUp() throws Exception {}

  @Test
  void testSimpleStopwords() {
    List<String> words = new ArrayList<String>();
    words.add("le");
    words.add("la");
    ISimpleStopwords<IToken> stopwords = new Stopwords(words);
    assertTrue(stopwords.isStopword("le"));
    assertTrue(!stopwords.isStopword("insuffisance"));
  }

  @Test
  void testSimpleStopwordsUppercase() {
    List<String> words = new ArrayList<String>();
    words.add("Insuffisance");
    ISimpleStopwords<IToken> stopwords = new Stopwords(words);
    assertTrue(stopwords.isStopword("insuffisance"));
  }

  @Test
  void testSimpleStopwordsEmptyString() {
    ISimpleStopwords<IToken> stopwords = new Stopwords();
    assertTrue(stopwords.isStopword(" "));
    assertTrue(stopwords.isStopword("\t"));
    assertTrue(stopwords.isStopword("\n"));
  }

  @Test
  void testSimpleStopwordsAccents() {
    List<String> words = new ArrayList<String>();
    words.add("à");
    ISimpleStopwords<IToken> stopwords = new Stopwords(words);
    assertTrue(stopwords.isStopword("à"));
    assertTrue(!stopwords.isStopword("a"));
  }

  @Test
  void testNegativeStopwords() {
    Token token = new Token(0, 1, "important", "important", 0);
    List<String> words = new ArrayList<String>();
    NegativeStopwords stopwords = new NegativeStopwords(words);
    assertTrue(stopwords.isTokenAStopword(token));
    words.add("important");
    stopwords.add(words);
    assertTrue(!stopwords.isTokenAStopword(token));
  }

  @Test
  void testNegativeStopwordsFun2keep() {
    Token token = new Token(0, 1, "important", "important", 0);
    NegativeStopwords stopwords = new NegativeStopwords();
    assertTrue(stopwords.isTokenAStopword(token));
    stopwords.add((word) -> word.equals("important") ? true : false);
    assertTrue(!stopwords.isTokenAStopword(token));
  }
}
