package fr.erias.iamsystem_java.fuzzy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.fuzzy.base.NormLabelAlgo;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;

/**
 * A class to stores fuzzy algorithms output in cache.
 *
 * @author Sebastien Cossin
 *
 */
public class CacheFuzzyAlgos extends NormLabelAlgo
{

	private List<NormLabelAlgo> fuzzyAlgos;
	private Map<String, List<SynAlgo>> cache = new HashMap<String, List<SynAlgo>>();

	/**
	 * Create a new instance called 'Cache'.
	 */
	public CacheFuzzyAlgos()
	{
		this("Cache");
	}

	private CacheFuzzyAlgos(String name)
	{
		super(name);
		this.fuzzyAlgos = new ArrayList<NormLabelAlgo>();
	}

	/**
	 * Add a fuzzy algorithm in cache.
	 *
	 * @param fuzzyAlgo a {@link NormLabelAlgo} algorithm.
	 */
	public void addFuzzyAlgo(NormLabelAlgo fuzzyAlgo)
	{
		this.fuzzyAlgos.add(fuzzyAlgo);
	}

	private void callAlgos(String normLabel)
	{
		List<SynAlgo> synsAlgo = fuzzyAlgos.stream()
				.map(fuzzy -> fuzzy.getSynsOfWord(normLabel))
				.flatMap(List::stream)
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
