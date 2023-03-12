package fr.erias.iamsystem_java.fuzzy.base;

import java.util.List;

import fr.erias.iamsystem_java.tokenize.IToken;

/**
 * Base class of algorithms that use only the normalized label of a token. These
 * fuzzy algorithms can be put in cache to avoid calling them multiple times.
 *
 * @author Sebastien Cossin
 *
 */
public abstract class NormLabelAlgo extends ContextFreeAlgo
{

	/**
	 * Create a new instance.
	 *
	 * @param name A name given to this algorithm.
	 */
	public NormLabelAlgo(String name)
	{
		super(name);
	}

	@Override
	public List<SynAlgo> getSynonyms(IToken token)
	{
		return this.getSynsOfWord(token.normLabel());
	}

	/**
	 * Returns synonyms of this word.
	 *
	 * @param normLabel the normalized label of a token.
	 * @return a list of synonyms ({@link SynAlgo}).
	 */
	public abstract List<SynAlgo> getSynsOfWord(String normLabel);
}
