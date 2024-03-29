/** */
package fr.erias.iamsystem.tokenize;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import fr.erias.iamsystem.tokenize.NormFunctions;

class NormFunctionsTest
{

	@Test
	void testLowerNoAccents()
	{
		String norm = NormFunctions.lowerNoAccents.normalize("İ");
		assertEquals("i", norm);
		norm = NormFunctions.lowerNoAccents.normalize("Ö");
		assertEquals("o", norm);
	}

	@Test
	void testRmAccents()
	{
		String norm = NormFunctions.rmAccents.normalize("éèô");
		assertEquals("eeo", norm);
	}

	@Test
	void testRmAccentsSpecial()
	{
		String norm = NormFunctions.rmAccents.normalize("İ");
		assertEquals("I", norm);
		norm = NormFunctions.rmAccents.normalize("Ö");
		assertEquals("O", norm);
	}
}
