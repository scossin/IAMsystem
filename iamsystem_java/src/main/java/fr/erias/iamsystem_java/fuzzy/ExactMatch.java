package fr.erias.iamsystem_java.fuzzy;

import fr.erias.iamsystem_java.tokenize.IToken;

public class ExactMatch<T extends IToken> extends NormLabelAlgo<T>
{

	public ExactMatch()
	{
		super("Exact");
	}

	@Override
	public String[] getSynsOfWord(String word)
	{
		return this.word2syn(word);
	}
}
