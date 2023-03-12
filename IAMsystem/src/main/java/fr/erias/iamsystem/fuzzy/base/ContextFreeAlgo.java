package fr.erias.iamsystem.fuzzy.base;

import java.util.List;

import fr.erias.iamsystem.matcher.StateTransition;
import fr.erias.iamsystem.tokenize.IToken;

/**
 * Base class of fuzzy algorithms that doesn't take into account context, only
 * the current token.
 *
 * @author Sebastien Cossin
 *
 */
public abstract class ContextFreeAlgo extends FuzzyAlgo
{

	/**
	 * Create a new instance.
	 *
	 * @param name A name given to this algorithm.
	 */
	public ContextFreeAlgo(String name)
	{
		super(name);
	}

	/**
	 * Retrieve synonyms based on the current token.
	 *
	 * @param token the token for which synonyms are expected.
	 * @return 0 to many synonyms.
	 */
	public abstract List<SynAlgo> getSynonyms(IToken token);

	@Override
	public List<SynAlgo> getSynonyms(List<IToken> tokens, IToken token, Iterable<StateTransition> transitions)
	{
		return this.getSynonyms(token);
	}
}
