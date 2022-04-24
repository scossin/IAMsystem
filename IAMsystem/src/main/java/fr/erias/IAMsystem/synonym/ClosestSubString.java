package fr.erias.IAMsystem.synonym;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.tree.EmptyNode;
import fr.erias.IAMsystem.tree.INode;
import fr.erias.IAMsystem.tree.PrefixTrie;

/**
 * Approximate String algorithm based on the prefix of a token
 * It returns the closest and unique token, from the dictionary, that begins with the same prefix, with a mininum length 'minPrefixLength', closest to a maximum number of characters diff of 'maxCharDiff'
 * 
 * @author Sebastien Cossin
 *
 */
public class ClosestSubString implements ISynonym {

	private final PrefixTrie prefixTrie;
	private final int maxDistance;

	/**
	 * Fuzzy matching algorithm that finds the closest token substring, in the all terminology, of a token in a document
	 * Ex: term is "high blood pressure" ; token is 'pressures' ; the closest substring is 'pressure'
	 * Complexity is O(1)
	 * @param prefixTrie a {@link PrefixTrie} that stores all the characters of unique tokens in the terminology in a trie
	 * @param maxDistance the maximum number of characters between the closest substring and a token in the document
	 */
	public ClosestSubString(PrefixTrie prefixTrie, int maxDistance) {
		this.prefixTrie = prefixTrie;
		this.maxDistance = maxDistance;
	}
	
	private Set<List<String>> getTokenStartingWith(String token) {
		// algorithm: go deep in the trie character by character and record the node of the last final state
		// compare the node depth with maxDistance value: this is the number of characters between the closest substring and the token
		Set<List<String>> startsWith = new HashSet<List<String>>();
		List<String> chars = Arrays.asList(this.prefixTrie.getCharTokenizer().tokenize(token));
		INode currentNode = this.prefixTrie.getTrie().getInitialState();
		INode lastFinalNode = EmptyNode.EMPTYNODE;
		int countDepth = 0;
		for (String c : chars) {
			currentNode = currentNode.gotoNode(c);
			if (currentNode.isAfinalState()) {
				lastFinalNode = currentNode;
				countDepth = 0;
			} else {
				countDepth = countDepth + 1;
			}
		}
		if (lastFinalNode != EmptyNode.EMPTYNODE && countDepth <= this.maxDistance) {
			ArrayList<String> phrase = new ArrayList<String>(1);
			phrase.add(lastFinalNode.getTerm().getLabel());
			startsWith.add(phrase);
		}
		return(startsWith);
	}


	@Override
	public Set<List<String>> getSynonyms(String token) {
		if (this.prefixTrie.tokenLengthLessThanMinSize(token)) {
			return ISynonym.no_synonyms;
		}
		return(getTokenStartingWith(token));
	}
}
