package fr.erias.iamsystem_java.fuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Caverphone1;
import org.apache.commons.codec.language.bm.BeiderMorseEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringEncoderTest
{

	private StringEncoderSyn stringEncoder;

	@BeforeEach
	void setUp() throws Exception
	{
		this.stringEncoder = new StringEncoderSyn(new Caverphone1(), 5);
	}

	@Test
	void testBeiderMorseEncoder() throws EncoderException
	{
		this.stringEncoder = new StringEncoderSyn(new BeiderMorseEncoder(), 5);
		this.stringEncoder.add(Arrays.asList("insuffisance"));
		List<SynAlgo> synAlgos = this.stringEncoder.getSynsOfWord("insuffizzzance");
		assertEquals(6, synAlgos.size());
		assertEquals("insuffisance", synAlgos.get(0).getSyn());
		assertEquals("BeiderMorseEncoder", synAlgos.get(0).getAlgo());
	}

	@Test
	void testCaverphone1() throws EncoderException
	{
		this.stringEncoder.add(Arrays.asList("insuffisance"));
		List<SynAlgo> synAlgos = this.stringEncoder.getSynsOfWord("insuffizzzance");
		assertEquals(1, synAlgos.size());
		assertEquals("insuffisance", synAlgos.get(0).getSyn());
		assertEquals("Caverphone1", synAlgos.get(0).getAlgo());
	}

	@Test
	void testExactMatch() throws EncoderException
	{
		this.stringEncoder.add(Arrays.asList("insuffisance"));
		List<SynAlgo> synAlgos = this.stringEncoder.getSynsOfWord("insuffisance");
		assertEquals(1, synAlgos.size());
		assertEquals("insuffisance", synAlgos.get(0).getSyn());
		assertEquals("Caverphone1", synAlgos.get(0).getAlgo());
	}
}
