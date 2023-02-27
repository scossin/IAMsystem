package fr.erias.iamsystem_java.fuzzy.troncation;

import java.util.Collection;
import java.util.List;

import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.ITokenizerStopwords;
import fr.erias.iamsystem_java.tokenize.NormFunctions;
import fr.erias.iamsystem_java.tokenize.SplitFunctions;
import fr.erias.iamsystem_java.tokenize.TokenizerImp;
import fr.erias.iamsystem_java.tree.Trie;

/**
 * @author Sebastien Cossin
 *
 */
public class PrefixTrie implements ITokenizerStopwords
{

	private final int minPrefixLength;
	private final Trie trie;
	private final ITokenizer charTokenizer;

	/**
	 * Approximate String algorithm based on the prefix of a token
	 *
	 * @param minPrefixLength minimum number of characters of the prefix/token <br>
	 *                        Ignore all token that has length below minPrefixLength
	 */
	public PrefixTrie(int minPrefixLength)
	{
		this.minPrefixLength = minPrefixLength;
		this.trie = new Trie();
		this.charTokenizer = new TokenizerImp(NormFunctions.rmAccents, SplitFunctions.splitChar);
	}

	public void addToken(Collection<String> tokens)
	{
		tokens.stream().filter(w -> !tokenLengthLessThanMinSize(w)).forEach((w) -> addToken(w));
	}

	public void addToken(String token)
	{
		trie.addKeyword(token, this);
	}

	/**
	 * Retrieve the character tokenizer
	 *
	 * @return a tokenizer
	 */
	public ITokenizer getCharTokenizer()
	{
		return charTokenizer;
	}

	/**
	 * Retrieve the tree storing the characters
	 *
	 * @return {@link Trie}
	 */
	public Trie getTrie()
	{
		return (this.trie);
	}

	@Override
	public boolean isTokenAStopword(IToken token)
	{
		return false;
	}

	@Override
	public List<IToken> tokenize(String text)
	{
		return this.charTokenizer.tokenize(text);
	}

	/**
	 * Check if token length is greater than the minimum size
	 *
	 * @param token a document from the document
	 * @return true if greater or equal to the minimum size
	 */
	public boolean tokenLengthLessThanMinSize(String token)
	{
		return token.length() < this.minPrefixLength;
	}

}
