package fr.erias.IAMsystem.synonym;

import java.util.HashSet;

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
	public HashSet<String[]> getSynonyms(String token);
}
