package fr.erias.iamsystem_java.tokenize;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FrTokenizerTest {

  private ITokenizer<Token> tokenizer;
  private List<Token> tokens;

  @BeforeEach
  void setUp() throws Exception {
    this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
    this.tokens = this.tokenizer.tokenize("Meningo-encéphalite");
  }

  @Test
  void testTokenization() {
    assertEquals(2, this.tokens.size());
  }

  @Test
  void testMinStart() {
    int minTokenStart = IOffsets.getMinStart(this.tokens);
    assertEquals(0, minTokenStart);
  }

  @Test
  void testMaxEnd() {
    int maxTokenEnd = IOffsets.getMaxEnd(this.tokens);
    assertEquals(19, maxTokenEnd);
  }

  @Test
  void testGetSpanSeqId() {
    String seqId = IOffsets.getSpanSeqId(this.tokens);
    assertEquals("(0,7);(8,19)", seqId);
  }

  @Test
  void testConcatNormLabel() {
    String tokenNormLabel = IToken.ConcatNormLabel(this.tokens);
    assertEquals("meningo encephalite", tokenNormLabel);
  }

  @Test
  void testConcatLabel() {
    String tokenLabel = IToken.ConcatLabel(this.tokens);
    assertEquals("Meningo encéphalite", tokenLabel);
  }

  @Test
  void testLabel() {
    String label0 = this.tokens.get(0).label();
    assertEquals(label0, "Meningo");
    String label1 = this.tokens.get(1).label();
    assertEquals(label1, "encéphalite");
  }

  @Test
  void testNormLabel() {
    String label0 = this.tokens.get(0).normLabel();
    assertEquals(label0, "meningo");
    String label1 = this.tokens.get(1).normLabel();
    assertEquals(label1, "encephalite");
  }
}
