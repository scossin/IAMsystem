package fr.erias.iamsystem_java.fuzzy.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.erias.iamsystem_java.matcher.StateTransition;
import fr.erias.iamsystem_java.tokenize.IToken;

/**
 * The default {@link ISynsProvider} implementation.
 *
 * @author Sebastien Cossin
 *
 */
public class SynsProvider implements ISynsProvider
{

	private List<? extends FuzzyAlgo> fuzzyAlgos;

	/**
	 * A list of fuzzy algorithms stored by this provider.
	 * 
	 * @param fuzzyAlgos multiple {@link FuzzyAlgo}.
	 */
	public SynsProvider(List<? extends FuzzyAlgo> fuzzyAlgos)
	{
		this.fuzzyAlgos = fuzzyAlgos;
	}

	@Override
	public Collection<SynAlgos> getSynonyms(List<IToken> tokens, IToken token, Iterable<StateTransition> transitions)
	{
		Map<String, SynAlgos> syn2synAlgos = new HashMap<String, SynAlgos>();
		for (FuzzyAlgo fuzzyAlgo : fuzzyAlgos)
		{
			// synsAlgo: multiple synonym for one algorithm.
			// synAlgos: one synonym for multiple algorithms.
			List<SynAlgo> synsAlgo = fuzzyAlgo.getSynonyms(tokens, token, transitions);

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
