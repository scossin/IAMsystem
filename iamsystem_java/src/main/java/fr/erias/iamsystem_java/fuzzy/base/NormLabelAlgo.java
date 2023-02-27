package fr.erias.iamsystem_java.fuzzy.base;

import java.util.List;

import fr.erias.iamsystem_java.tokenize.IToken;

public abstract class NormLabelAlgo extends ContextFreeAlgo
{

	public NormLabelAlgo(String name)
	{
		super(name);
	}

	@Override
	public List<SynAlgo> getSynonyms(IToken token)
	{
		return this.getSynsOfWord(token.normLabel());
	}

	public abstract List<SynAlgo> getSynsOfWord(String normLabel);
}
