package fr.erias.IAMsystem.synonym;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizer.Tokenizer;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.utils.Utils;

/**
 * This class extracts all the prefixes of a set of Tokens
 * For example, given "insuffisance", "i","in","ins","insu" ... are prefixes of "insuffisance"
 * See also StemByPrefix
 * 
 * @author Cossin Sebastien
 *
 */
public class PrefixDetector implements ISynonym {

	/**
	 * map prefix => HashSet of Terms
	 */
	private final HashMap<String, HashSet<Term>> mapPrefix2terms = new HashMap<String, HashSet<Term>>();
	
	private int minPrefixLength = 1;
	private int maxPrefixLength = 15;

	/**
	 * Constructor
	 * @param uniqueTokensTermino a set tokens (e.g. every token that a terminology contains)
	 */
	public PrefixDetector(Set<String> uniqueTokensTermino) {
		this.setPrefixDetectors(uniqueTokensTermino);
	}
	
	/**
	 * 
	 * @param uniqueTokensTermino
	 * @param minPrefixLength prefix below this length will be ignored
	 */
	public PrefixDetector(Set<String> uniqueTokensTermino, int minPrefixLength) {
		this.minPrefixLength = minPrefixLength;
		this.setPrefixDetectors(uniqueTokensTermino);
	}
	
	/**
	 * 
	 * @param uniqueTokensTermino
	 * @param minPrefixLength prefix below this length will be ignored
	 * @param maxPrefixLength prefix above this length will be ignored
	 */
	public PrefixDetector(Set<String> uniqueTokensTermino, int minPrefixLength, int maxPrefixLength) {
		this.minPrefixLength = minPrefixLength;
		this.maxPrefixLength = maxPrefixLength;
		this.setPrefixDetectors(uniqueTokensTermino);
	}
	
	/**
	 * Constructor 
	 * @param terminology a {@link Terminology} - all unique token of this terminology will be extracted
	 */
	public PrefixDetector(Terminology terminology) {
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer();
		tokenizerNormalizer.setTokenizer(new Tokenizer());
		HashSet<String> uniqueTokensTermino = Utils.getUniqueToken(terminology, tokenizerNormalizer);
		this.setPrefixDetectors(uniqueTokensTermino);
	}
	
	/**
	 * A map: prefix to a HashSet of terms beginning by this prefix
	 * @return
	 */
	public HashMap<String, HashSet<Term>> getPrefixDetector(){
		return(this.mapPrefix2terms);
	}
	
	/**
	 * For each term, extract prefix and add it to mapPrefix2terms
	 * @param uniqueTokensTermino
	 */
	private void setPrefixDetectors(Set<String> uniqueTokensTermino) {
		for (String token : uniqueTokensTermino) {
			Term term = new Term(token, token);
			for (int prefixLength = minPrefixLength; prefixLength<maxPrefixLength; prefixLength++) {
				if (token.length() <= prefixLength) { 
					break;
				}
				String prefix = token.substring(0, prefixLength);
				createNewSetIfNotExists(prefix);
				mapPrefix2terms.get(prefix).add(term); // add term beginning by this prefix
			}
		}
	}
	
	private void createNewSetIfNotExists(String prefix){
		if (!mapPrefix2terms.containsKey(prefix)) {
			mapPrefix2terms.put(prefix, new HashSet<Term>());
		}
	}
	
	@Override
	public HashSet<String[]> getSynonyms(String token) {
		HashSet<String[]> synonyms = new HashSet<String[]>();
		if (token.length() > this.maxPrefixLength) { // no prefix are longer than max
			return(synonyms);
		}
		if (!this.mapPrefix2terms.containsKey(token)) { // token doesn't match any prefix
			return(synonyms);
		}
		for (Term term : this.mapPrefix2terms.get(token)) { // output each possible label
			synonyms.add(new String[] {term.getLabel()});
		}
		return synonyms;
	}
}
