package fr.erias.iamsystem_java.fuzzy.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.matcher.StateTransition;
import fr.erias.iamsystem_java.tokenize.IToken;

/**
 * Fuzzy Algorithm base class.
 *
 * @author Sebastien Cossin
 *
 */
public abstract class FuzzyAlgo
{

	public static List<SynAlgo> NO_SYN = Arrays.asList();
	private final String name;

	/**
	 * Create a new fuzzy algorithm.
	 *
	 * @param name A name given to the algorithm (ex: "french stemmer").
	 */
	public FuzzyAlgo(String name)
	{
		this.name = name;
	}

	/**
	 * name getter.
	 *
	 * @return the algorithm name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Main fuzzy algorithm function that is called to retrieve synonyms.
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
	public abstract List<SynAlgo> getSynonyms(List<IToken> tokens, IToken token, Iterable<StateTransition> transitions);

	/**
	 * Utility function to transform a string to a list of {@link SynAlgo}.
	 *
	 * @param word a word synonym produced by the algorithm. Ex: word='insuffisance'
	 *             for token 'ins'.
	 * @return a list of {@link SynAlgo}.
	 */
	public List<SynAlgo> word2syn(String word)
	{
		SynAlgo synsAlgo = new SynAlgo(word, this.getName());
		List<SynAlgo> syns = new ArrayList<SynAlgo>(1);
		syns.add(synsAlgo);
		return syns;
	}

	/**
	 * Utility function to transform a sequence of string to the expected output
	 * type.
	 *
	 * @param words a sequence of words produced by the algorithm. Ex:
	 *              words=['insuffisance', 'cardiaque'] for the token 'ic'.
	 * @return a list of {@link SynAlgo}.
	 */
	public List<SynAlgo> words2syn(Collection<String> words)
	{
		return words.stream().map(w -> new SynAlgo(w, this.getName())).collect(Collectors.toList());
	}
}
