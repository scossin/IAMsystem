package fr.erias.iamsystem_java.fuzzy.base;

import java.util.List;

import fr.erias.iamsystem_java.matcher.StateTransition;
import fr.erias.iamsystem_java.tokenize.IToken;

public abstract class ContextFreeAlgo extends FuzzyAlgo
{

	public ContextFreeAlgo(String name)
	{
		super(name);
	}

	public abstract List<SynAlgo> getSynonyms(IToken token);

	@Override
	public List<SynAlgo> getSynonyms(List<IToken> tokens, IToken token, Iterable<StateTransition> states)
	{
		return this.getSynonyms(token);
	}
}
