package fr.erias.iamsystem.fuzzy.base;

import java.util.Collection;
import java.util.List;

import fr.erias.iamsystem.matcher.StateTransition;
import fr.erias.iamsystem.tokenize.IToken;

/**
 * Any class that can retrieve synonyms (from {@link FuzzyAlgo}) can implement
 * this interface. The class provides all the synonyms of fuzzy algorithms.
 *
 * @author Sebastien Cossin
 *
 */
public interface ISynsProvider
{

	/**
	 * "Main fuzzy algorithm function that is called to retrieve synonyms.
	 *
	 * @param tokens      the sequence of tokens of the document. Useful when the
	 *                    fuzzy algorithm needs context, namely the tokens around
	 *                    the token of interest.
	 * @param token       the token of this sequence for which synonyms are
	 *                    expected.
	 * @param transitions the state transitions in which the algorithm currently is.
	 *                    Useful is the fuzzy algorithm needs to know the next or
	 *                    possible transitions.
	 * @return 0 to many synonyms.
	 */
	public Collection<SynAlgos> getSynonyms(List<IToken> tokens, IToken token, Iterable<StateTransition> transitions);
}
