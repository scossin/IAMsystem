package fr.erias.iamsystem_java.fuzzy.normfun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.erias.iamsystem_java.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.base.NormLabelAlgo;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.tokenize.INormalizeF;

public class WordNormalizer extends NormLabelAlgo
{

	private INormalizeF normfun;
	private Map<String, List<SynAlgo>> norm2syns = new HashMap<String, List<SynAlgo>>();

	public WordNormalizer(String name, INormalizeF normfun)
	{
		super(name);
		this.normfun = normfun;
	}

	public void add(Iterable<String> unigrams)
	{
		for (String unigram : unigrams)
		{
			String normalized = this.normfun.normalize(unigram);
			if (!norm2syns.containsKey(normalized))
			{
				norm2syns.put(normalized, new ArrayList<SynAlgo>());
			}
			SynAlgo synsAlgo = new SynAlgo(unigram, this.getName());
			norm2syns.get(normalized).add(synsAlgo);
		}
	}

	@Override
	public List<SynAlgo> getSynsOfWord(String normLabel)
	{
		String normalized = this.normfun.normalize(normLabel);
		if (!norm2syns.containsKey(normalized))
			return FuzzyAlgo.NO_SYN;
		return norm2syns.get(normalized);
	}

}
