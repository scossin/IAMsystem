package fr.erias.iamsystem_java.fuzzy;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.erias.iamsystem_java.matcher.TransitionState;
import fr.erias.iamsystem_java.tokenize.IToken;

public class SynsProvider<T extends IToken> implements ISynsProvider<T>
{

	private List<? extends FuzzyAlgo<T>> fuzzyAlgos;

	public SynsProvider(List<? extends FuzzyAlgo<T>> fuzzyAlgos)
	{
		this.fuzzyAlgos = fuzzyAlgos;
	}

	@Override
	public Collection<SynAlgos> getSynonyms(List<T> tokens, T token, List<TransitionState<T>>[] wStates)
	{
		Map<String, SynAlgos> syn2synAlgos = new HashMap<String, SynAlgos>();
		for (FuzzyAlgo<T> fuzzyAlgo : fuzzyAlgos)
		{
			// synsAlgo: multiple synonym for one algorithm.
			// synAlgos: one synonym for multiple algorithms.
			List<SynAlgo> synsAlgo = fuzzyAlgo.getSynonyms(tokens, token, wStates);

			for (SynAlgo synAlgo : synsAlgo)
			{
				String syn = synAlgo.getSyn();
				if (syn2synAlgos.containsKey(syn))
				{
					syn2synAlgos.get(syn).addAlgo(synAlgo.getAlgo());
				} else
				{
					SynAlgos synAlgos = new SynAlgos(syn, synAlgo.getAlgo());
					syn2synAlgos.put(syn, synAlgos);
				}
			}
		}
		return syn2synAlgos.values();
	}
}
