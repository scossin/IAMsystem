package fr.erias.iamsystem.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem.stopwords.Stopwords;
import fr.erias.iamsystem.tokenize.ETokenizer;
import fr.erias.iamsystem.tokenize.IToken;
import fr.erias.iamsystem.tokenize.ITokenizer;
import fr.erias.iamsystem.tokenize.ITokenizerStopwords;
import fr.erias.iamsystem.tokenize.NormFunctions;
import fr.erias.iamsystem.tokenize.SplitFunctions;
import fr.erias.iamsystem.tokenize.TokenizerFactory;
import fr.erias.iamsystem.tokenize.TokenizerImp;
import fr.erias.iamsystem.tree.EmptyNode;
import fr.erias.iamsystem.tree.INode;
import fr.erias.iamsystem.tree.Trie;
import fr.erias.iamsystem.utils.MockData;

class TrieTest implements ITokenizerStopwords
{

	private ITokenizer tokenizer;
	private Stopwords stopwords;

	@Override
	public boolean isTokenAStopword(IToken token)
	{
		return this.stopwords.isTokenAStopword(token);
	}

	@BeforeEach
	void setUp() throws Exception
	{
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		this.stopwords = new Stopwords();
	}

	@Test
	void testFinalStateTrie()
	{
		// Go to insuffisance node and checks it's not a final state.
		Trie trie = new Trie();
		trie.addKeywords(MockData.getICG(), this);
		assertFalse(trie.getInitialState().isAfinalState());
		INode insuffisance = trie.getInitialState().gotoNode("insuffisance");
		assertFalse(insuffisance == EmptyNode.EMPTYNODE);
		assertFalse(insuffisance.isAfinalState());
	}

	@Test
	void testTrieAllStopwords()
	{
		// remove all string to check no transition(only the root node is present).
		Trie trie = new Trie();
		this.stopwords.add(Arrays.asList("insuffisance", "cardiaque", "gauche"));
		trie.addKeywords(MockData.getICG(), this);
		assertEquals(trie.getNumberOfNodes(), 1);
	}

	@Test
	void testTrieChangeNormF()
	{
		// Check tokens stored depend on the normalizing function of tokenizer.
		Trie trie = new Trie();
		this.tokenizer = new TokenizerImp(NormFunctions.noNormalization, SplitFunctions.splitAlphaNum);
		trie.addKeywords(MockData.getICG(), this);
		assertEquals(trie.getNumberOfNodes(), 4);
		assertTrue(!trie.getInitialState().hasTransitionTo("insuffisance"));
		assertTrue(trie.getInitialState().hasTransitionTo("Insuffisance"));
	}

	@Test
	void testTrieCheckTransitionFromRootNode()
	{
		// insuffisance ventriculaire gauche': START_TOKEN -> 'insuffisance'
		Trie trie = new Trie();
		trie.addKeywords(MockData.getICG(), this);
		assertTrue(trie.getInitialState().hasTransitionTo("insuffisance"));
	}

	@Test
	void testTrieInitialState()
	{
		// Number of tokens in keywords.
		Trie trie = new Trie();
		Trie.isTheRootNode(trie.getInitialState());
	}

	@Test
	void testTrieNumberOfNodes()
	{
		// Number of tokens in keywords.
		Trie trie = new Trie();
		assertEquals(trie.getNumberOfNodes(), 1);
		trie.addKeywords(MockData.getICG(), this);
		assertEquals(trie.getNumberOfNodes(), 4);
	}

	@Test
	void testTrieWithStopwords()
	{
		// remove insuffisance to check transition is to next token 'cardiaque'.
		Trie trie = new Trie();
		this.stopwords.add(Arrays.asList("insuffisance"));
		trie.addKeywords(MockData.getICG(), this);
		assertTrue(!trie.getInitialState().hasTransitionTo("insuffisance"));
		assertTrue(trie.getInitialState().hasTransitionTo("cardiaque"));
	}

	@Override
	public List<IToken> tokenize(String text)
	{
		return this.tokenizer.tokenize(text);
	}
}
