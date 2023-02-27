package fr.erias.iamsystem_java.fuzzy.troncation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.base.NormLabelAlgo;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.tree.EmptyNode;
import fr.erias.iamsystem_java.tree.INode;

class PrefixFounder
{
	private final Collection<INode> nodes;
	private final int maxDistance;

	PrefixFounder(Collection<INode> nodes, int maxDistance)
	{
		this.nodes = nodes;
		this.maxDistance = maxDistance;
	}

	public Set<String> getTokenStartingWith()
	{
		Set<String> startsWith = new HashSet<String>();
		for (INode node : nodes)
		{
			if (node == EmptyNode.EMPTYNODE)
				continue;
			startsWith.addAll(getTokenStartingWith(node));
		}
		return (startsWith);
	}

	private Set<String> getTokenStartingWith(INode node)
	{
		Set<String> startsWith = new HashSet<String>();

		if (node.isAfinalState())
		{
			node.getKeywords().forEach((k) -> startsWith.add(k.label()));
		}

		if (maxDistance > 0)
		{ // go deeper in the trie
			PrefixFounder sub = new PrefixFounder(node.getChildNodes(), maxDistance - 1);
			startsWith.addAll(sub.getTokenStartingWith());
		}
		return (startsWith);
	}
}

/**
 * Approximate String algorithm based on the prefix of a token It returns all
 * the string, in the dictionary, that begins with a prefix of length
 * 'minPrefixLength' and with a maximum difference characters of 'maxDistance'
 * Ex: 'diabet' is the troncation of 'diabete', maxDistance must be greater than
 * 1 Ex: 'ins' is the troncation of 'insuffisance', maxDistance must be greater
 * than 9 (12 - 3)
 *
 * @author Sebastien Cossin
 *
 */
public class Troncation extends NormLabelAlgo
{

	private final PrefixTrie prefixTrie;
	private final int maxDistance;

	/**
	 * Fuzzy string matching algorithm to find tokens in the terminology that ends
	 * by the token (truncated word) in the document
	 *
	 * @param prefixTrie  a {@link PrefixTrie} that stores all the characters of
	 *                    unique tokens in the terminology in a trie
	 * @param maxDistance maximum number of character between the prefix and a
	 *                    string (ex: diabet --- diabetes ; 2 char)
	 */
	public Troncation(String name, PrefixTrie prefixTrie, int maxDistance)
	{
		super(name);
		this.prefixTrie = prefixTrie;
		this.maxDistance = maxDistance;
	}

	@Override
	public List<SynAlgo> getSynsOfWord(String normLabel)
	{
		if (this.prefixTrie.tokenLengthLessThanMinSize(normLabel))
		{
			return FuzzyAlgo.NO_SYN;
		}
		Collection<String> tokensStartingWith = getTokenStartingWith(normLabel);
		return (this.words2syn(tokensStartingWith));
	}

	private Set<String> getTokenStartingWith(String token)
	{
		List<String> chars = this.prefixTrie.getCharTokenizer().tokenize(token).stream().map(t -> t.normLabel())
				.collect(Collectors.toList());
		INode node = this.prefixTrie.getTrie().getInitialState().gotoNode(chars);
		ArrayList<INode> nodes = new ArrayList<INode>(1);
		nodes.add(node);
		PrefixFounder prefix = new PrefixFounder(nodes, maxDistance);
		return (prefix.getTokenStartingWith());
	}
}
