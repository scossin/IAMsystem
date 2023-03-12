package fr.erias.iamsystem.fuzzy.abbreviations;

import fr.erias.iamsystem.tokenize.IToken;

@FunctionalInterface
public interface TokenIsAnAbbreviation
{
	/**
	 * Check if a token is an abbreviation.
	 *
	 * @param token A document's token.
	 * @return true if the token seems to be an abbreviation.
	 */
	public boolean isAnAbb(IToken token);
}
