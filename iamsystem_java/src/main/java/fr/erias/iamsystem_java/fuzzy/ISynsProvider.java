package fr.erias.iamsystem_java.fuzzy;

import java.util.Collection;
import java.util.List;

import fr.erias.iamsystem_java.matcher.TransitionState;
import fr.erias.iamsystem_java.tokenize.IToken;

public interface ISynsProvider<T extends IToken>
{

	public Collection<SynAlgos> getSynonyms(List<T> tokens, T token, List<TransitionState<T>>[] wStates);
}
