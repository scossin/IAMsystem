package fr.erias.iamsystem_java.brat;

import static org.junit.jupiter.api.Assertions.*;

import fr.erias.iamsystem_java.matcher.Annotation;
import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.IOffsets;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import fr.erias.iamsystem_java.tree.Node;
import fr.erias.iamsystem_java.tree.Trie;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FormatterTest {

  private ITokenizer<IToken> tokenizer;
  private List<IToken> tokens;
  private String text;

  @BeforeEach
  void setUp() throws Exception {
    this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
    this.text = "cancer de la glande prostate";
    List<IToken> tokens = this.tokenizer.tokenize(this.text);
    this.tokens = new ArrayList<IToken>();
    this.tokens.add(tokens.get(0));
    this.tokens.add(tokens.get(3));
    this.tokens.add(tokens.get(4));
  }

  @Test
  void testGroupContinuousSeq() {
    List<List<IToken>> sequences = BratFormatters.groupContinuousSeq(this.tokens);
    assertEquals(2, sequences.size());
  }

  @Test
  void testMultipleSeqToOffsets() {
    List<List<IToken>> sequences = BratFormatters.groupContinuousSeq(this.tokens);
    List<IOffsets> offsets = BratFormatters.multipleSeqToOffsets(sequences);
    assertEquals(2, offsets.size());
    IOffsets offsets0 = offsets.get(0);
    assertEquals("cancer", this.text.substring(offsets0.start(), offsets0.end()));
    IOffsets offsets1 = offsets.get(1);
    assertEquals("glande prostate", this.text.substring(offsets1.start(), offsets1.end()));
  }

  @Test
  void testGetBratFormatTokenFormatter() {
    List<List<IToken>> sequences = BratFormatters.groupContinuousSeq(this.tokens);
    List<IOffsets> offsetsSeq = BratFormatters.multipleSeqToOffsets(sequences);
    String offsets = BratFormatters.getBratFormat(offsetsSeq);
    assertEquals("0 6;13 28", offsets);
  }

  @Test
  void testTokenFormatter() {
    Trie trie = new Trie();
    Node lastState = new Node("insuffisance", trie.getInitialState(), 1);
    IAnnotation<IToken> annot =
        new Annotation<IToken>(
            this.tokens, new ArrayList<Collection<String>>(), lastState, new ArrayList<>());
    BratFormat format = BratFormatters.tokenFormatter.getFormat(annot);
    assertEquals("cancer glande prostate", format.getText());
    assertEquals("0 6;13 28", format.getOffsets());
  }
}
