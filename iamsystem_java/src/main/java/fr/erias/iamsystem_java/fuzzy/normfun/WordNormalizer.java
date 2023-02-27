package fr.erias.iamsystem_java.fuzzy.normfun;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import fr.erias.iamsystem_java.fuzzy.base.NormLabelAlgo;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.tokenize.INormalizeF;

public class WordNormalizer extends NormLabelAlgo
{

	private INormalizeF normfun;
	private Map<String, List<SynAlgo>> norm2syns = new HashedMap<String, List<SynAlgo>>();

	public WordNormalizer(String name, INormalizeF normfun)
	{
		super(name);
		this.normfun = normfun;
	}
	
	public void add(Iterable<String> unigrams) {
		
	}

	@Override
	public List<SynAlgo> getSynsOfWord(String normLabel)
	{
		return null;
	}

}
