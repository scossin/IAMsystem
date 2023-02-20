package fr.erias.iamsystem_java.fuzzy;

import fr.erias.iamsystem_java.matcher.TransitionState;
import fr.erias.iamsystem_java.tokenize.IToken;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SynsProvider<T extends IToken> implements ISynsProvider<T> {

  private Iterable<FuzzyAlgo<T>> fuzzyAlgos;

  public SynsProvider(Iterable<FuzzyAlgo<T>> fuzzyAlgos) {
    this.fuzzyAlgos = fuzzyAlgos;
  }

  @Override
  public Collection<SynAlgos> getSynonyms(
      List<T> tokens, T token, List<TransitionState<T>>[] wStates) {
    Map<String, SynAlgos> syn2synAlgos = new HashMap<String, SynAlgos>();
    for (FuzzyAlgo<T> fuzzyAlgo : fuzzyAlgos) {
      String[] syns = fuzzyAlgo.getSynonyms(tokens, token, wStates);
      for (String syn : syns) {
        if (syn2synAlgos.containsKey(syn)) {
          syn2synAlgos.get(syn).addAlgo(fuzzyAlgo.getName());
        } else {
          SynAlgos synAlgo = new SynAlgos(syn, fuzzyAlgo.getName());
          syn2synAlgos.put(syn, synAlgo);
        }
      }
    }
    return syn2synAlgos.values();
  }
}
