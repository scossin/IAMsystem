package fr.erias.iamsystem_java.fuzzy.base;

import java.util.Collection;
import java.util.List;

import fr.erias.iamsystem_java.matcher.LinkedState;
import fr.erias.iamsystem_java.tokenize.IToken;

public interface ISynsProvider
{

	public Collection<SynAlgos> getSynonyms(List<IToken> tokens, IToken token, Iterable<LinkedState> states);
}
