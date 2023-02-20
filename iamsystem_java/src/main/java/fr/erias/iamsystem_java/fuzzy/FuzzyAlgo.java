package fr.erias.iamsystem_java.fuzzy;

import fr.erias.iamsystem_java.matcher.TransitionState;
import fr.erias.iamsystem_java.tokenize.IToken;
import java.util.Collection;
import java.util.List;

public abstract class FuzzyAlgo<T extends IToken> {

  public static String[] NO_SYN = new String[] {};
  private final String name;

  public FuzzyAlgo(String name) {
    this.name = name;
  }

  public abstract String[] getSynonyms(List<T> tokens, T token, List<TransitionState<T>>[] wStates);

  public String[] word2syn(String word) {
    return new String[] {word};
  }

  public String[] words2syn(Collection<String> words) {
    return words.toArray(new String[words.size()]);
  }

  public String getName() {
    return name;
  }
}
