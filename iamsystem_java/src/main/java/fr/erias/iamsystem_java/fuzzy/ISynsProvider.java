package fr.erias.iamsystem_java.fuzzy;

import fr.erias.iamsystem_java.matcher.TransitionState;
import fr.erias.iamsystem_java.tokenize.IToken;
import java.util.Collection;
import java.util.List;

public interface ISynsProvider<T extends IToken> {

  public Collection<SynAlgos> getSynonyms(
      List<T> tokens, T token, List<TransitionState<T>>[] wStates);
}
