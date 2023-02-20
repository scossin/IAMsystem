package fr.erias.iamsystem_java.matcher;

import fr.erias.iamsystem_java.fuzzy.ExactMatch;
import fr.erias.iamsystem_java.fuzzy.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.ISynsProvider;
import fr.erias.iamsystem_java.fuzzy.SynAlgos;
import fr.erias.iamsystem_java.fuzzy.SynsProvider;
import fr.erias.iamsystem_java.keywords.IKeyword;
import fr.erias.iamsystem_java.keywords.IStoreKeywords;
import fr.erias.iamsystem_java.keywords.Keyword;
import fr.erias.iamsystem_java.keywords.Terminology;
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.TokStopImp;
import fr.erias.iamsystem_java.tree.EmptyNode;
import fr.erias.iamsystem_java.tree.INode;
import fr.erias.iamsystem_java.tree.Trie;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Matcher<T extends IToken>
    implements IMatcher<T>, IStoreKeywords, ITokenizer<T>, IStopwords<T>, ISynsProvider<T> {

  private ITokenizer<T> tokenizer;
  private IStopwords<T> stopwords;
  private final Detector<T> detector;
  private int w = 1;
  private final Trie trie = new Trie();
  private Terminology termino = new Terminology();
  private boolean removeNestedAnnot = true;
  private TokStopImp<T> tokstop; // TODO set stopwords/tokenizer
  private List<FuzzyAlgo<T>> fuzzyAlgos = new ArrayList<FuzzyAlgo<T>>();
  private SynsProvider<T> synsProvider;

  public Matcher(ITokenizer<T> tokenizer, IStopwords<T> stopwords) {
    this.setTokenizer(tokenizer);
    this.setStopwords(stopwords);
    this.detector = new Detector<T>();
    this.tokstop = new TokStopImp<T>(tokenizer, stopwords);
    fuzzyAlgos.add(new ExactMatch<T>());
    this.synsProvider = new SynsProvider<T>(fuzzyAlgos);
  }

  public ITokenizer<T> getTokenizer() {
    return tokenizer;
  }

  public void setTokenizer(ITokenizer<T> tokenizer) {
    this.tokenizer = tokenizer;
  }

  public IStopwords<T> getStopwords() {
    return stopwords;
  }

  public void setStopwords(IStopwords<T> stopwords) {
    this.stopwords = stopwords;
  }

  public int getW() {
    return w;
  }

  public void setW(int w) {
    this.w = w;
  }

  @Override
  public Collection<IKeyword> getKeywords() {
    return termino.getKeywords();
  }

  public Set<String> getUnigrams() {
    return IStoreKeywords.getUnigrams(this.getKeywords(), tokstop);
  }

  public void addKeyword(Iterable<? extends IKeyword> keywords) {
    for (IKeyword kw : keywords) {
      this.addKeyword(kw);
    }
  }

  /**
   * Add keywords by providing a String iterable.
   *
   * @param keywords A collection of keywords to add.
   */
  public void addKeyword(String[] keywords) {
    for (String kw : keywords) {
      Keyword k = new Keyword(kw);
      this.addKeyword(k);
    }
  }

  @Override
  public void addKeyword(IKeyword keyword) {
    termino.addKeyword(keyword);
    trie.addIKeyword(keyword, tokstop);
  }

  @Override
  public boolean isTokenAStopword(T token) {
    return stopwords.isTokenAStopword(token);
  }

  @Override
  public List<T> tokenize(String text) {
    return tokenizer.tokenize(text);
  }

  public List<IAnnotation<T>> annot(String text) {
    List<T> tokens = tokenize(text);
    return this.annot(tokens);
  }

  @Override
  public Collection<SynAlgos> getSynonyms(
      List<T> tokens, T token, List<TransitionState<T>>[] wStates) {
    return synsProvider.getSynonyms(tokens, token, wStates);
  }

  public List<IAnnotation<T>> annot(List<T> tokens) {
    return detector.detect(tokens, w, trie.getInitialState(), this, stopwords);
  }
}

class Detector<T extends IToken> {

  public List<IAnnotation<T>> detect(
      List<T> tokens,
      int w,
      INode initialState,
      ISynsProvider<T> synsProvider,
      IStopwords<T> stopwords) {
    List<IAnnotation<T>> annots = new ArrayList<IAnnotation<T>>();
    TransitionState<T> startState = new TransitionState<T>(null, initialState, null, null);
    int w_states_size = w + 2;
    List<TransitionState<T>>[] w_states = new ArrayList[w_states_size];
    for (int i = 0; i < w_states_size; i++) {
      w_states[i] = new ArrayList<TransitionState<T>>();
    }
    w_states[w_states_size - 2].add(startState);
    List<TransitionState<T>> tempTransStates = w_states[w_states_size - 1];

    int count_not_stopword = 0;
    List<T> stopTokens = new ArrayList<T>();
    for (T token : tokens) {
      if (stopwords.isTokenAStopword(token)) {
        stopTokens.add(token);
        continue;
      }
      count_not_stopword++;
      tempTransStates.clear();
      Iterable<SynAlgos> synAlgos = synsProvider.getSynonyms(tokens, token, w_states);
      for (SynAlgos synAlgo : synAlgos) {
        for (int i = 0; i < w_states_size - 1; i++) {
          List<TransitionState<T>> transStates = w_states[i];
          for (TransitionState<T> transState : transStates) {
            INode node = transState.getNode().gotoNode(synAlgo.getSyn());
            if (node == EmptyNode.EMPTYNODE) continue;
            TransitionState<T> newTransState =
                new TransitionState<T>(transState, node, token, synAlgo.getAlgos());
            if (node.isAfinalState()) {
              IAnnotation<T> annotation = this.createAnnot(newTransState, stopTokens);
              annots.add(annotation);
            }
            tempTransStates.add(newTransState);
          }
        }
      }
      w_states[count_not_stopword % w].clear();
      w_states[count_not_stopword % w].addAll(tempTransStates);
      // TODO: test performance creating tempTransStates at each iteration
      // vs doing clear and addAll operation.
    }
    return annots;
  }

  private Annotation<T> createAnnot(TransitionState<T> last_el, List<T> stopTokens) {
    List<TransitionState<T>> transStates = this.toList(last_el);
    INode lastState = last_el.getNode();
    List<T> tokens = transStates.stream().map(t -> t.getToken()).collect(Collectors.toList());
    tokens.sort(Comparator.naturalOrder());
    List<Collection<String>> algos =
        transStates.stream().map(t -> t.getAlgos()).collect(Collectors.toList());
    return new Annotation<T>(tokens, algos, lastState, stopTokens);
  }

  private List<TransitionState<T>> toList(TransitionState<T> last_el) {
    List<TransitionState<T>> transStates = new ArrayList<>();
    transStates.add(last_el);
    TransitionState<T> parent = last_el.getParent();
    while (!TransitionState.isStartState(parent)) {
      transStates.add(parent);
      parent = parent.getParent();
    }
    Collections.reverse(transStates);
    return transStates;
  }
}
