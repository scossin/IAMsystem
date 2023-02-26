package fr.erias.iamsystem_java.fuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.base.ISynsProvider;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgos;
import fr.erias.iamsystem_java.fuzzy.base.SynsProvider;
import fr.erias.iamsystem_java.matcher.LinkedState;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;

class SynProviderTest
{

	private ISynsProvider<IToken> synProvider;
	private List<FuzzyAlgo<IToken>> fuzzyAlgos;
	private ITokenizer<IToken> tokenizer;
	private Set<LinkedState<IToken>> states;

	@BeforeEach
	void setUp() throws Exception
	{
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		this.fuzzyAlgos = new ArrayList<FuzzyAlgo<IToken>>();
		this.fuzzyAlgos.add(new ExactMatch<IToken>());
		this.synProvider = new SynsProvider<IToken>(fuzzyAlgos);
		this.states = new HashSet<LinkedState<IToken>>(0);
	}

	@Test
	void test()
	{
		List<IToken> tokens = this.tokenizer.tokenize("(ic) Insuffisance cardiaque");
		assertEquals(3, tokens.size());
		IToken ic = tokens.get(0);
		Collection<SynAlgos> syns = this.synProvider.getSynonyms(tokens, ic, this.states);
		assertEquals(1, syns.size());
		for (SynAlgos synAlgos : syns)
		{
			assertEquals("ic", synAlgos.getSyn());
			assertEquals(1, synAlgos.getAlgos().size());
			assertEquals(1, synAlgos.getSynToken().length);
			assertEquals("ic", synAlgos.getSynToken()[0]);
		}
	}
}
