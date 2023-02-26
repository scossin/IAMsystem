package fr.erias.iamsystem_java.fuzzy;

import java.util.List;
import java.util.Set;

import fr.erias.iamsystem_java.matcher.LinkedState;
import fr.erias.iamsystem_java.tokenize.IToken;

public abstract class ContextFreeAlgo<T extends IToken> extends FuzzyAlgo<T>
{

	public ContextFreeAlgo(String name)
	{
		super(name);
	}

	@Override
	public List<SynAlgo> getSynonyms(List<T> tokens, T token, Set<LinkedState<T>> states)
	{
		return this.getSynonyms(token);
	}

	public abstract List<SynAlgo> getSynonyms(T token);
}
