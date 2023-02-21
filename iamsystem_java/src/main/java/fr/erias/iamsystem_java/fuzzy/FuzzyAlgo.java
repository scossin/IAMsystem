package fr.erias.iamsystem_java.fuzzy;

import java.util.Collection;
import java.util.List;

import fr.erias.iamsystem_java.matcher.TransitionState;
import fr.erias.iamsystem_java.tokenize.IToken;

public abstract class FuzzyAlgo<T extends IToken>
{

	public static String[] NO_SYN = new String[] {};
	private final String name;

	public FuzzyAlgo(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public abstract String[] getSynonyms(List<T> tokens, T token, List<TransitionState<T>>[] wStates);

	public String[] word2syn(String word)
	{
		return new String[] { word };
	}

	public String[] words2syn(Collection<String> words)
	{
		return words.toArray(new String[words.size()]);
	}
}
