package fr.erias.iamsystem_java.fuzzy;

import java.util.List;

import fr.erias.iamsystem_java.fuzzy.base.NormLabelAlgo;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;

public class ExactMatch extends NormLabelAlgo
{

	public ExactMatch()
	{
		super("Exact");
	}

	@Override
	public List<SynAlgo> getSynsOfWord(String word)
	{
		return this.word2syn(word);
	}
}
