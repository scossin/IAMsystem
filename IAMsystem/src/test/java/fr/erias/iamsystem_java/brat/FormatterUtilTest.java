package fr.erias.iamsystem_java.brat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.annotation.Annotation;
import fr.erias.iamsystem_java.annotation.IAnnotation;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.matcher.MatcherBuilder;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.IOffsets;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.NormFunctions;
import fr.erias.iamsystem_java.tokenize.SplitFunctions;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import fr.erias.iamsystem_java.tokenize.TokenizerImp;
import fr.erias.iamsystem_java.tree.Node;
import fr.erias.iamsystem_java.tree.Trie;

class FormatterUtilTest
{

	private ITokenizer tokenizer;
	private List<IToken> tokens;
	private String text;

	@BeforeEach
	void setUp() throws Exception
	{
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		this.text = "cancer de la glande prostate";
		List<IToken> tokens = this.tokenizer.tokenize(this.text);
		this.tokens = new ArrayList<IToken>();
		this.tokens.add(tokens.get(0));
		this.tokens.add(tokens.get(3));
		this.tokens.add(tokens.get(4));
	}

	@Test
	void testContSeqFormatterFormatter()
	{
		Trie trie = new Trie();
		Node lastState = new Node("insuffisance", trie.getInitialState(), 1);
		IAnnotation annot = new Annotation(this.tokens, new ArrayList<Collection<String>>(), lastState,
				new ArrayList<>());
		annot.setText(this.text);
		BratFormat format = BratFormatters.contSeqFormatter.getFormat(annot);
		assertEquals("cancer glande prostate", format.getText());
		assertEquals("0 6;13 28", format.getOffsets());
	}

	@Test
	void testGetBratFormatTokenFormatter()
	{
		List<List<IToken>> sequences = BratFormatters.groupContinuousSeq(this.tokens);
		List<IOffsets> offsetsSeq = BratFormatters.multipleSeqToOffsets(sequences);
		String offsets = BratFormatters.getBratFormat(offsetsSeq);
		assertEquals("0 6;13 28", offsets);
	}

	@Test
	void testGroupContinuousSeq()
	{
		List<List<IToken>> sequences = BratFormatters.groupContinuousSeq(this.tokens);
		assertEquals(2, sequences.size());
	}

	@Test
	void testMultipleSeqToOffsets()
	{
		List<List<IToken>> sequences = BratFormatters.groupContinuousSeq(this.tokens);
		List<IOffsets> offsets = BratFormatters.multipleSeqToOffsets(sequences);
		assertEquals(2, offsets.size());
		IOffsets offsets0 = offsets.get(0);
		assertEquals("cancer", this.text.substring(offsets0.start(), offsets0.end()));
		IOffsets offsets1 = offsets.get(1);
		assertEquals("glande prostate", this.text.substring(offsets1.start(), offsets1.end()));
	}

	@Test
	void testTokenFormatterPunctuation()
	{
		ITokenizer tokenizer = new TokenizerImp(NormFunctions.lowerCase, SplitFunctions.splitAlphaNumFloat);
		Matcher matcher = new MatcherBuilder().keywords("calcium 2.6 mmol/L").tokenizer(tokenizer).build();
		List<IAnnotation> anns = matcher.annot("calcium 2.6 mmol/L");
		assertEquals(anns.size(), 1);
		assertEquals(anns.get(0).toString(), "calcium 2.6 mmol/L	0 18	calcium 2.6 mmol/L");
	}
}
