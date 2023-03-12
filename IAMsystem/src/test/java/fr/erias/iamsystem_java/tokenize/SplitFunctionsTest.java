package fr.erias.iamsystem_java.tokenize;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class SplitFunctionsTest
{

	@Test
	void testAlphaNumAccdent()
	{
		// Check an accent does not count as an offset
		List<IOffsets> offsets = SplitFunctions.splitAlphaNum.split("L'ulcéres");
		assertEquals(2, offsets.size());
	}

	@Test
	void testAlphaNumDash()
	{
		List<IOffsets> offsets = SplitFunctions.splitAlphaNum.split("meningo-encephalite");
		assertEquals(2, offsets.size());
	}

	@Test
	void testAlphaNumEmptyString()
	{
		List<IOffsets> offsets = SplitFunctions.splitAlphaNum.split("");
		assertEquals(0, offsets.size());
		offsets = SplitFunctions.splitAlphaNum.split(" ");
		assertEquals(0, offsets.size());
	}

	@Test
	void testAlphaNumPunctuation()
	{
		List<IOffsets> offsets = SplitFunctions.splitAlphaNum.split("one,two");
		assertEquals(2, offsets.size());
	}

	@Test
	void testAlphaNumQuotes()
	{
		List<IOffsets> offsets = SplitFunctions.splitAlphaNum.split("L'ulceres");
		assertEquals(2, offsets.size());
	}

	@Test
	void testAlphaNumStartEnd()
	{
		List<IOffsets> offsets = SplitFunctions.splitAlphaNum.split("one two");
		assertEquals(2, offsets.size());
		IOffsets offsets1 = offsets.get(1);
		assertEquals(offsets1.start(), 4);
		assertEquals(offsets1.end(), 7);
	}

	@Test
	void testSplitChar()
	{
		List<IOffsets> offsets = SplitFunctions.splitChar.split("one two");
		assertEquals(6, offsets.size());
		IOffsets offsets1 = offsets.get(0);
		assertEquals(offsets1.start(), 0);
		assertEquals(offsets1.end(), 1);
	}
}
