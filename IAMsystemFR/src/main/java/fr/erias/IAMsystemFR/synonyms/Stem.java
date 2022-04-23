package fr.erias.IAMsystemFR.synonyms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;

/**
 * A French Stemmer for IAMsystem
 * 
 * @author Sebastien Cossin
 *
 */
public class Stem implements ISynonym {

	// stems to their original tokens
	private Map<String, Set<List<String>>> stems = new HashMap<String, Set<List<String>>>();

	private FrenchStemmer stemmer = new FrenchStemmer();

	/**
	 * Stem a word
	 * @param token from a document
	 * @return the stem of the token
	 */
	public String stem(String token) {
		return(stemmer.stem(token));
	}

	/**
	 * Add a terminology
	 * For each term of the terminology, retrieve all unique tokens and stem them
	 * @param terminology a {@link Terminology}
	 * @param tokenizerNormalizer a {@link ITokenizerNormalizer}
	 */
	public void addTerminology(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer) {
		Set<String> tokens = getUniqueToken(terminology, tokenizerNormalizer);
		addStems(tokens);
	}
	
	/**
	 * Add a term
	 * Retrieve all unique tokens of this term and stem them
	 * @param term a {@link Term}
	 * @param tokenizerNormalizer a {@link ITokenizerNormalizer}
	 */
	public void addTerm(Term term, ITokenizerNormalizer tokenizerNormalizer) {
		Set<String> tokens = getUniqueToken(term, tokenizerNormalizer);
		addStems(tokens);
	}
	
	private void addStems(Set<String> tokens) {
		for (String token : tokens) {
			String stem = stem(token);
			addStem(token, stem);
		}
	}

	private void addStem(String token, String stem) {
		String[] tokenArray = new String[] {token};
		if (!stems.containsKey(stem)) {
			Set<List<String>> temp = new HashSet<List<String>>();
			stems.put(stem, temp);
		} 
		stems.get(stem).add(Arrays.asList(tokenArray));
	}

	private Set<String> getUniqueToken(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer) {
		Set<String> uniqueTokens = new HashSet<String> ();
		for (Term term : terminology.getTerms()) {
			uniqueTokens.addAll(getUniqueToken(term, tokenizerNormalizer));
		}
		return(uniqueTokens);
	}

	private Set<String> getUniqueToken(Term term, ITokenizerNormalizer tokenizerNormalizer) {
		IStopwords stopwords = tokenizerNormalizer.getNormalizer().getStopwords();
		Set<String> uniqueTokens = new HashSet<String> ();
		String normalizeLabel = term.getNormalizedLabel();
		String[] tokensArray = tokenizerNormalizer.getTokenizer().tokenize(normalizeLabel);
		tokensArray = IStopwords.removeStopWords(stopwords, tokensArray);
		for (String token : tokensArray) {
			if (token.length() < 5) { // we won't search for a stem under 5 characters
				continue;
			}
			uniqueTokens.add(token);
		}
		return(uniqueTokens);
	}

	/**
	 * Retrieve synonyms stored by abbreviations
	 */
	public Set<List<String>> getSynonyms(String token){
		String stem = this.stem(token);
		if (stems.containsKey(stem)) {
			return(stems.get(stem));
		} else {
			return(ISynonym.no_synonyms);
		}
	}
}
