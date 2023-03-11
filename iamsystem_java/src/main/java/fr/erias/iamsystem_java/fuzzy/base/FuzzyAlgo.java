package fr.erias.iamsystem_java.fuzzy.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.matcher.StateTransition;
import fr.erias.iamsystem_java.tokenize.IToken;

public abstract class FuzzyAlgo
{

	public static List<SynAlgo> NO_SYN = Arrays.asList();
	private final String name;

	public FuzzyAlgo(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public abstract List<SynAlgo> getSynonyms(List<IToken> tokens, IToken token, Iterable<StateTransition> states);

	public List<SynAlgo> word2syn(String word)
	{
		SynAlgo synsAlgo = new SynAlgo(word, this.getName());
		List<SynAlgo> syns = new ArrayList<SynAlgo>(1);
		syns.add(synsAlgo);
		return syns;
	}

	public List<SynAlgo> words2syn(Collection<String> words)
	{
		return words.stream().map(w -> new SynAlgo(w, this.getName())).collect(Collectors.toList());
	}
}
