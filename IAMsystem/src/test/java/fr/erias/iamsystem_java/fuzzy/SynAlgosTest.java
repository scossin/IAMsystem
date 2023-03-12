package fr.erias.iamsystem_java.fuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.fuzzy.base.SynAlgos;

class SynAlgosTest
{

	private SynAlgos build(String syn)
	{
		return new SynAlgos(syn, "Exact");
	}

	@Test
	void testSynAlgosAlgos()
	{
		SynAlgos synAlgo = build("insuffisance");
		assertEquals(synAlgo.getAlgos().size(), 1);
		synAlgo.addAlgo("Exact");
		assertEquals(synAlgo.getAlgos().size(), 1);
		synAlgo.addAlgo("exact");
		assertEquals(synAlgo.getAlgos().size(), 2);
	}

	@Test
	void testSynAlgosEquality()
	{
		SynAlgos synAlgo = build("insuffisance");
		SynAlgos synAlgo2 = build("insuffisance");
		assertEquals(synAlgo, synAlgo2);
		assertEquals(synAlgo.hashCode(), synAlgo2.hashCode());
		SynAlgos synAlgo3 = build("insuffisance ");
		assertNotEquals(synAlgo, synAlgo3);
		assertNotEquals(synAlgo.hashCode(), synAlgo3.hashCode());
	}

	@Test
	void testSynAlgosSyn()
	{
		// trailing space has no importance
		SynAlgos synAlgo = build("insuffisance");
		assertEquals(synAlgo.getSynToken().length, 1);
		SynAlgos synAlgo2 = build("insuffisance  ");
		assertEquals(synAlgo2.getSynToken().length, 1);
		SynAlgos synAlgo3 = build("insuffisance cardiaque");
		assertEquals(synAlgo3.getSynToken().length, 2);
	}
}
