package fr.erias.iamsystem_java.fuzzy;

import static org.junit.jupiter.api.Assertions.*;

import fr.erias.iamsystem_java.matcher.TransitionState;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SynProviderTest {

  private ISynsProvider<IToken> synProvider;
  private List<FuzzyAlgo<IToken>> fuzzyAlgos;
  private ITokenizer<IToken> tokenizer;
  private ArrayList<TransitionState<IToken>>[] wStates;

  @BeforeEach
  void setUp() throws Exception {
    this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
    this.fuzzyAlgos = new ArrayList<FuzzyAlgo<IToken>>();
    this.fuzzyAlgos.add(new ExactMatch<IToken>());
    this.synProvider = new SynsProvider<IToken>(fuzzyAlgos);
    this.wStates = new ArrayList[0];
  }

  @Test
  void test() {
    List<IToken> tokens = this.tokenizer.tokenize("(ic) Insuffisance cardiaque");
    assertEquals(3, tokens.size());
    IToken ic = tokens.get(0);
    Collection<SynAlgos> syns = this.synProvider.getSynonyms(tokens, ic, this.wStates);
    assertEquals(1, syns.size());
    for (SynAlgos synAlgos : syns) {
      assertEquals("ic", synAlgos.getSyn());
      assertEquals(1, synAlgos.getAlgos().size());
      assertEquals(1, synAlgos.getSynToken().length);
      assertEquals("ic", synAlgos.getSynToken()[0]);
    }
  }
}
