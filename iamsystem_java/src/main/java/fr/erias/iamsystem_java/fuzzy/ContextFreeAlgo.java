package fr.erias.iamsystem_java.fuzzy;

import fr.erias.iamsystem_java.matcher.TransitionState;
import fr.erias.iamsystem_java.tokenize.IToken;
import java.util.List;

public abstract class ContextFreeAlgo<T extends IToken> extends FuzzyAlgo<T> {

  public ContextFreeAlgo(String name) {
    super(name);
  }

  public String[] getSynonyms(List<T> tokens, T token, List<TransitionState<T>>[] wStates) {
    return this.getSynonyms(token);
  }

  public abstract String[] getSynonyms(T token);
}
