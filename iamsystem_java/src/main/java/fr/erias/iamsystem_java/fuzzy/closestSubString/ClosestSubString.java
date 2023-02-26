package fr.erias.iamsystem_java.fuzzy.closestSubString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.base.NormLabelAlgo;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.fuzzy.troncation.PrefixTrie;
import fr.erias.iamsystem_java.keywords.IKeyword;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tree.EmptyNode;
import fr.erias.iamsystem_java.tree.INode;

public class ClosestSubString<T extends IToken> extends NormLabelAlgo<IToken>
{

	private final PrefixTrie prefixTrie;
	private final int maxDistance;

	/**
	 * Fuzzy matching algorithm that finds the closest token substring, in the all
	 * terminology, of a token in a document Ex: term is "high blood pressure" ;
	 * token is 'pressures' ; the closest substring is 'pressure' Complexity is O(1)
	 * 
	 * @param prefixTrie  a {@link PrefixTrie} that stores all the characters of
	 *                    unique tokens in the terminology in a trie
	 * @param maxDistance the maximum number of characters between the closest
	 *                    substring and a token in the document
	 */
	public ClosestSubString(String name, PrefixTrie prefixTrie, int maxDistance)
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
		Set<String> startsWith = getTokenStartingWith(normLabel);
		return (this.words2syn(startsWith));
	}

	private Set<String> getTokenStartingWith(String token)
	{
		// algorithm: go deep in the trie character by character and record the node of
		// the last final state
		// compare the node depth with maxDistance value: this is the number of
		// characters between the closest substring and the token
		Set<String> startsWith = new HashSet<String>();
		List<String> chars = this.prefixTrie.getCharTokenizer().tokenize(token).stream().map(t -> t.normLabel())
				.collect(Collectors.toList());
		INode currentNode = this.prefixTrie.getTrie().getInitialState();
		INode lastFinalNode = EmptyNode.EMPTYNODE;
		int countDepth = 0;
		for (String c : chars)
		{
			currentNode = currentNode.gotoNode(c);
			if (currentNode.isAfinalState())
			{
				lastFinalNode = currentNode;
				countDepth = 0;
			} else
			{
				countDepth = countDepth + 1;
			}
		}
		if (lastFinalNode != EmptyNode.EMPTYNODE && countDepth <= this.maxDistance)
		{
			for (IKeyword kw : lastFinalNode.getKeywords())
			{
				startsWith.add(kw.label());
			}
		}
		return (startsWith);
	}
}
