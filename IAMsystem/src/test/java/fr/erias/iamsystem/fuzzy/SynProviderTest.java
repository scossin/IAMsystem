package fr.erias.iamsystem.fuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem.fuzzy.ExactMatch;
import fr.erias.iamsystem.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem.fuzzy.base.ISynsProvider;
import fr.erias.iamsystem.fuzzy.base.SynAlgos;
import fr.erias.iamsystem.fuzzy.base.SynsProvider;
import fr.erias.iamsystem.matcher.StateTransition;
import fr.erias.iamsystem.tokenize.ETokenizer;
import fr.erias.iamsystem.tokenize.IToken;
import fr.erias.iamsystem.tokenize.ITokenizer;
import fr.erias.iamsystem.tokenize.TokenizerFactory;

class SynProviderTest
{

	private ISynsProvider synProvider;
	private List<FuzzyAlgo> fuzzyAlgos;
	private ITokenizer tokenizer;
	private Set<StateTransition> states;

	@BeforeEach
	void setUp() throws Exception
	{
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		this.fuzzyAlgos = new ArrayList<FuzzyAlgo>();
		this.fuzzyAlgos.add(new ExactMatch());
		this.synProvider = new SynsProvider(fuzzyAlgos);
		this.states = new HashSet<StateTransition>(0);
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
