package fr.erias.IAMsystem.synonym;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents synonyms : abbreviations and typos for example. 
 * Given a token, return one or multiples array of tokens (String[])
 * Ex : given "meningoencephalite", return {"meningo","encephalite"}
 * @author Cossin Sebastien
 *
 */
public interface ISynonym {
	
	/**
	 * Search synonyms like abbreviations and typos
	 * @param token a Token that could match an abbreviation or a typo
	 * @return An array of token. Size 0 if no one found
	 */
	public Set<List<String>> getSynonyms(String token);
	
	/**
	 * I use this empty instance if a ISynonym instance doesn't have any synonym to return
	 */
	public static final Set<List<String>> no_synonyms = new HashSet<List<String>>();
}
