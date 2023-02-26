package fr.erias.iamsystem_java.fuzzy.troncation;

import java.util.Collection;

import fr.erias.iamsystem_java.stopwords.NoStopwords;
import fr.erias.iamsystem_java.tokenize.AbstractTokNorm;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.NormFunctions;
import fr.erias.iamsystem_java.tokenize.SplitFunctions;
import fr.erias.iamsystem_java.tokenize.TokStopImp;
import fr.erias.iamsystem_java.tokenize.TokenizerImp;
import fr.erias.iamsystem_java.tree.Trie;

/**
 * @author Sebastien Cossin
 *
 */
public class PrefixTrie
{

	private final int minPrefixLength;
	private final Trie trie;
	private final ITokenizer<IToken> charTokenizer;
	private final AbstractTokNorm<IToken> toknorm;

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
		this.toknorm = new TokStopImp<IToken>(charTokenizer, new NoStopwords());
	}

	public void addToken(Collection<String> tokens)
	{
		tokens.stream().filter(w -> !tokenLengthLessThanMinSize(w)).forEach((w) -> addToken(w));
	}

	public void addToken(String token)
	{
		trie.addKeyword(token, toknorm);
	}

	/**
	 * Retrieve the character tokenizer
	 *
	 * @return a tokenizer
	 */
	public ITokenizer<IToken> getCharTokenizer()
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
