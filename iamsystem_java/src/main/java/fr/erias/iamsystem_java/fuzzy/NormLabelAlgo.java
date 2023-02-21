package fr.erias.iamsystem_java.fuzzy;

import java.util.List;

import fr.erias.iamsystem_java.tokenize.IToken;

public abstract class NormLabelAlgo<T extends IToken> extends ContextFreeAlgo<T>
{

	public NormLabelAlgo(String name)
	{
		super(name);
	}

	@Override
	public List<SynAlgo> getSynonyms(T token)
	{
		return this.getSynsOfWord(token.normLabel());
	}

	public abstract List<SynAlgo> getSynsOfWord(String normLabel);
}
