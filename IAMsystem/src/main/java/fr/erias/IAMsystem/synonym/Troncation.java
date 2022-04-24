package fr.erias.IAMsystem.synonym;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.erias.IAMsystem.tree.INode;
import fr.erias.IAMsystem.tree.PrefixTrie;

/**
 * Approximate String algorithm based on the prefix of a token
 * It returns all the string, in the dictionary, that begins with a prefix of length 'minPrefixLength' and with a maximum difference characters of 'maxDistance'
 * Ex: 'diabet' is the troncation of 'diabete', maxDistance must be greater than 1 
 * Ex: 'ins' is the troncation of 'insuffisance', maxDistance must be greater than 9 (12 - 3)
 * 
 * @author Sebastien Cossin
 *
 */
public class Troncation implements ISynonym {

	private final PrefixTrie prefixTrie;
	private final int maxDistance;
	
	/**
	 * Fuzzy string matching algorithm to find tokens in the terminology that ends by the token (truncated word) in the document
	 * @param prefixTrie a {@link PrefixTrie} that stores all the characters of unique tokens in the terminology in a trie
	 * @param maxDistance  maximum number of character between the prefix and a string (ex: diabet --- diabetes ; 2 char)
	 */
	public Troncation(PrefixTrie prefixTrie, int maxDistance) {
		this.prefixTrie = prefixTrie;
		this.maxDistance = maxDistance;
	}

	private Set<List<String>> getTokenStartingWith(String token) {
		List<String> chars = Arrays.asList(this.prefixTrie.getCharTokenizer().tokenize(token));
		INode node = this.prefixTrie.getTrie().getInitialState().gotoNode(chars);
		ArrayList<INode> nodes = new ArrayList<INode>(1);
		nodes.add(node);
		PrefixFounder prefix = new PrefixFounder(nodes, maxDistance);
		return(prefix.getTokenStartingWith());
	}
	
	@Override
	public Set<List<String>> getSynonyms(String token) {
		if (this.prefixTrie.tokenLengthLessThanMinSize(token)) {
			return ISynonym.no_synonyms;
		}
		return(getTokenStartingWith(token));
	}
}

class PrefixFounder {
	private final Collection<INode> nodes;
	private final int maxDistance;

	PrefixFounder(Collection<INode> nodes, int maxDistance) {
		this.nodes = nodes;
		this.maxDistance = maxDistance;
	}

	public Set<List<String>> getTokenStartingWith() {
		Set<List<String>> startsWith = new HashSet<List<String>>();
		for (INode node : nodes) {
			startsWith.addAll(getTokenStartingWith(node));
		}
		return(startsWith);
	}

	private Set<List<String>> getTokenStartingWith(INode node) {
		Set<List<String>> startsWith = new HashSet<List<String>>();

		if (node.isAfinalState()) addLabel(node, startsWith);

		if (maxDistance > 0) { // go deeper in the trie
			PrefixFounder sub = new PrefixFounder(node.getChildNodes(), maxDistance -1);
			startsWith.addAll(sub.getTokenStartingWith());
		}
		return(startsWith);
	}

	private void addLabel(INode node, Set<List<String>> startsWith) {
		List<String> list = new ArrayList<String>(1);
		list.add(node.getTerm().getLabel());
		startsWith.add(list);
	}
}

