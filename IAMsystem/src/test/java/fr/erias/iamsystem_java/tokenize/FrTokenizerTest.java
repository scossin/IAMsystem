package fr.erias.iamsystem_java.tokenize;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FrTokenizerTest
{

	private ITokenizer tokenizer;
	private List<IToken> tokens;

	@BeforeEach
	void setUp() throws Exception
	{
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		this.tokens = this.tokenizer.tokenize("Meningo-encéphalite");
	}

	@Test
	void testConcatLabel()
	{
		String tokenLabel = IToken.ConcatLabel(this.tokens);
		assertEquals("Meningo encéphalite", tokenLabel);
	}

	@Test
	void testConcatNormLabel()
	{
		String tokenNormLabel = IToken.ConcatNormLabel(this.tokens);
		assertEquals("meningo encephalite", tokenNormLabel);
	}

	@Test
	void testGetSpanSeqId()
	{
		String seqId = IOffsets.getSpanSeqId(this.tokens);
		assertEquals("(0,7);(8,19)", seqId);
	}

	@Test
	void testLabel()
	{
		String label0 = this.tokens.get(0).label();
		assertEquals(label0, "Meningo");
		String label1 = this.tokens.get(1).label();
		assertEquals(label1, "encéphalite");
	}

	@Test
	void testMaxEnd()
	{
		int maxTokenEnd = IOffsets.getMaxEnd(this.tokens);
		assertEquals(19, maxTokenEnd);
	}

	@Test
	void testMinStart()
	{
		int minTokenStart = IOffsets.getMinStart(this.tokens);
		assertEquals(0, minTokenStart);
	}

	@Test
	void testNormLabel()
	{
		String label0 = this.tokens.get(0).normLabel();
		assertEquals(label0, "meningo");
		String label1 = this.tokens.get(1).normLabel();
		assertEquals(label1, "encephalite");
	}

	@Test
	void testReverseOrder()
	{
		ITokenizer orderTokensTokenizer = new OrderTokensTokenizer(this.tokenizer);
		List<IToken> tokens = orderTokensTokenizer.tokenize("Meningo-encéphalite");
		assertEquals("Token(label='encéphalite', norm_label='encephalite', start=8, end=19, i=1)",
				tokens.get(0).toString());
	}

	@Test
	void testTokenization()
	{
		assertEquals(2, this.tokens.size());
	}

	@Test
	void testTokenOrder()
	{
		List<IToken> unSortedList = new ArrayList<IToken>();
		unSortedList.add(this.tokens.get(1));
		unSortedList.add(this.tokens.get(0));
		unSortedList.sort(Comparator.naturalOrder());
		String label0 = unSortedList.get(0).normLabel();
		assertEquals(label0, "meningo");
		String label1 = unSortedList.get(1).normLabel();
		assertEquals(label1, "encephalite");
	}
}
