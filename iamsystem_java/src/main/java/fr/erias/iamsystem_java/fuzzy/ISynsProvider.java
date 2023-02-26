package fr.erias.iamsystem_java.fuzzy;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import fr.erias.iamsystem_java.matcher.LinkedState;
import fr.erias.iamsystem_java.tokenize.IToken;

public interface ISynsProvider<T extends IToken>
{

	public Collection<SynAlgos> getSynonyms(List<T> tokens, T token, Set<LinkedState<T>> states);
}
