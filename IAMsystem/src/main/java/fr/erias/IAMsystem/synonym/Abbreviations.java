package fr.erias.IAMsystem.synonym;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.erias.IAMsystem.tokenizer.TokenizerWhiteSpace;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;

/**
 * Manage and store abbreviations
 * 
 * @author Cossin Sebastien
 *
 */
public class Abbreviations implements ISynonym {
	
	private Map<String, Set<List<String>>> abbreviations = new HashMap<String, Set<List<String>>>();
	
	/**
	 * Add an abbreviation
	 * @param longFormTokens (ex : 'accident' 'vasculaire' 'cerebral'). See {@link ITokenizerNormalizer} to tokenize
	 * @param shortForm (ex : 'avc')
	 */
	public void addAbbreviation(List<String> longFormTokens, String shortForm) {
		createSetIfShortFormIsUnknown(shortForm);
		Set<List<String>> longForms = abbreviations.get(shortForm);
		longForms.add(longFormTokens);
	}
	
	private void createSetIfShortFormIsUnknown(String shortForm) {
		if (!abbreviations.containsKey(shortForm)) {
			Set<List<String>> temp = new HashSet<List<String>>();
			abbreviations.put(shortForm, temp);
			return;
		}
	}
	
	/**
	 * Add an abbreviation
	 * @param longFormTokens (ex : 'accident' 'vasculaire' 'cerebral'). See {@link ITokenizerNormalizer} to tokenize
	 * @param shortForm (ex : 'avc')
	 */
	public void addAbbreviation(String[] longFormTokens, String shortForm) {
		addAbbreviation(List.of(longFormTokens), shortForm);
	}
	
	/**
	 * Add an abbreviation. Warning: {@link TokenizerWhiteSpace} is used to tokenize the term. 
	 * See other ways to add abbreviations if you want to control this behavior. 
	 * @param term (ex: 'accident vasculaire cerebral')
	 * @param shortForm (ex: 'avc') (not normalized)
	 */
	public void addAbbreviation(String term, String shortForm) {
		String[] tokensArray = new TokenizerWhiteSpace().tokenize(term);
		addAbbreviation(tokensArray, shortForm);
	}
	
	
	/**
	 * Add an abbreviation. The term and the abbreviation are normalized with the {@link ITokenizerNormalizer}
	 * @param term (ex : 'insuf')
	 * @param abbreviation (ex : 'insuffisance')
	 * @param tokenizerNormalizer a {@link ITokenizerNormalizer}
	 */
	public void addAbbreviation(String term, String abbreviation, ITokenizerNormalizer tokenizerNormalizer) {
		String normalizedAbbreviation = tokenizerNormalizer.getNormalizer().getNormalizedSentence(abbreviation);
		String normalizedTerm = tokenizerNormalizer.getNormalizer().getNormalizedSentence(term);
		String[] tokensArray = tokenizerNormalizer.getTokenizer().tokenize(normalizedTerm);
		addAbbreviation(tokensArray, normalizedAbbreviation);
	}
	
	@Override
	public Set<List<String>> getSynonyms(String token){
		if (abbreviations.containsKey(token)) {
			return(abbreviations.get(token));
		} else {
			return(ISynonym.no_synonyms);
		}
	}
}
