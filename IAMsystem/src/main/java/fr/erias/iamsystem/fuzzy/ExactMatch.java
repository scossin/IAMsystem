package fr.erias.iamsystem.fuzzy;

import java.util.List;

import fr.erias.iamsystem.fuzzy.base.NormLabelAlgo;
import fr.erias.iamsystem.fuzzy.base.SynAlgo;

/**
 * The exact match class that returns the token's label.
 *
 * @author Sebastien Cossin
 *
 */
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
