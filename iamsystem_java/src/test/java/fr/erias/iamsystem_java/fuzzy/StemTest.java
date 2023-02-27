package fr.erias.iamsystem_java.fuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.fuzzy.normfun.FrenchStemmer;
import fr.erias.iamsystem_java.fuzzy.normfun.WordNormalizer;
import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.stopwords.NoStopwords;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.INormalizeF;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;
import fr.erias.iamsystem_java.utils.MockData;

class StemTest
{

	private ITokenizer tokenizer;
	private Matcher matcher;
	private WordNormalizer fuzzyStemmer;

	@BeforeEach
	void setUp() throws Exception
	{
		INormalizeF frStemmer = new FrenchStemmer();
		this.fuzzyStemmer = new WordNormalizer("french stemmer", frStemmer);
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		this.matcher = new Matcher(tokenizer, new NoStopwords());
		this.matcher.setRemoveNestedAnnot(false);
		matcher.addFuzzyAlgo(fuzzyStemmer);
	}

	@Test
	void testGetSynonyms()
	{
		this.fuzzyStemmer.add(Arrays.asList("insuffisance"));
		List<SynAlgo> syns = this.fuzzyStemmer.getSynsOfWord("insuffisant");
		assertEquals(syns.size(), 1);
	}

	@Test
	void testMatcher()
	{
		this.fuzzyStemmer.add(Arrays.asList("insuffisance", "cardiaque", "gauche"));
		matcher.addKeyword(MockData.getICG());
		List<IAnnotation> annots = matcher.annot("insuffisant cardiaqu gauch");
		assertEquals(annots.size(), 2);
	}

}
