package fr.erias.iamsystem_java.fuzzy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.tokenize.IToken;

public class CacheFuzzyAlgos<T extends IToken> extends NormLabelAlgo<T>
{

	private List<NormLabelAlgo<T>> fuzzyAlgos;

	private Map<String, List<SynAlgo>> cache = new HashMap<String, List<SynAlgo>>();

	public CacheFuzzyAlgos()
	{
		this("Cache");
	}

	public CacheFuzzyAlgos(String name)
	{
		super(name);
		this.fuzzyAlgos = new ArrayList<NormLabelAlgo<T>>();
	}

	public void addFuzzyAlgo(NormLabelAlgo<T> fuzzyAlgo)
	{
		this.fuzzyAlgos.add(fuzzyAlgo);
	}

	private void callAlgos(String normLabel)
	{
		List<SynAlgo> synsAlgo = fuzzyAlgos.stream().map(fuzzy -> fuzzy.getSynsOfWord(normLabel)).flatMap(List::stream)
				.collect(Collectors.toList());
		this.cache.put(normLabel, synsAlgo);
	}

	@Override
	public List<SynAlgo> getSynsOfWord(String normLabel)
	{
		if (!cache.containsKey(normLabel))
		{
			this.callAlgos(normLabel);
		}
		return cache.get(normLabel);
	}
}
