package fr.erias.iamsystem.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.language.Soundex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.liblevenshtein.transducer.Algorithm;

import fr.erias.iamsystem.annotation.IAnnotation;
import fr.erias.iamsystem.fuzzy.normfun.FrenchStemmer;
import fr.erias.iamsystem.keywords.Entity;
import fr.erias.iamsystem.matcher.Matcher;
import fr.erias.iamsystem.matcher.MatcherBuilder;
import fr.erias.iamsystem.tokenize.ITokenizer;
import fr.erias.iamsystem.tokenize.NormFunctions;
import fr.erias.iamsystem.tokenize.SplitFunctions;
import fr.erias.iamsystem.tokenize.TokenizerImp;

class FuzzyTest
{

	@BeforeEach
	void setUp() throws Exception
	{
	}

	@Test
	void testFuzzy()
	{

		ITokenizer tokenizer = new TokenizerImp(NormFunctions.lowerCase, SplitFunctions.splitAlphaNumFloat);
		Matcher matcher = new MatcherBuilder().keywords("calcium numval mmol/L")
				.stopwords("level", "is", "normal")
				.tokenizer(tokenizer)
				.fuzzyRegex("regex_num", "^\\d*[.,]?\\d*$", "numval")
				.build();
		List<IAnnotation> annots = matcher.annot("the blood calcium level is normal: 2.1 mmol/L");
		assertEquals(annots.size(), 1);
		assertEquals(annots.get(0).toString(), "calcium 2.1 mmol/L	10 17;35 45	calcium numval mmol/L");
	}

	@Test
	void testFuzzyNegative()
	{
		ITokenizer tokenizer = new TokenizerImp(NormFunctions.lowerCase, SplitFunctions.splitAlphaNumFloat);
		Matcher matcher = new MatcherBuilder().keywords("calcium numval mmol/L")
				.negative(true)
				.tokenizer(tokenizer)
				.fuzzyRegex("regex_num", "^\\d*[.,]?\\d*$", "numval")
				.build();
		List<IAnnotation> annots = matcher.annot("the blood calcium level is normal: 2.1 mmol/L");
		assertEquals(annots.size(), 1);
		assertEquals(annots.get(0).toString(), "calcium 2.1 mmol/L	10 17;35 45	calcium numval mmol/L");
	}

	@Test
	void testLevenSoundex()
	{

		Entity ent = new Entity("acute respiratory distress", "J80");
		Matcher matcher = new MatcherBuilder().keywords(ent)
				.levenshtein(5, 1, Algorithm.STANDARD)
				.stringEncoder(new Soundex(), 5)
				.build();
		List<IAnnotation> annots = matcher.annot("acute resiratory distresssss");
		assertEquals(annots.size(), 1);
	}

	@Test
	void testStringDistanceIgnored()
	{

		Matcher matcher = new MatcherBuilder().keywords("poids").levenshtein(4, 1, Algorithm.STANDARD).build();
		List<IAnnotation> annots = matcher.annot("Absence de poils.");
		assertEquals(annots.size(), 1);
		matcher = new MatcherBuilder().keywords("poids")
				.stringDistanceWords2ignore(Arrays.asList("poils"))
				.levenshtein(4, 1, Algorithm.STANDARD)
				.build();
		annots = matcher.annot("Absence de poils.");
		assertEquals(annots.size(), 0);
	}

	@Test
	void testWordNormalizer()
	{
		Entity ent1 = new Entity("cancer de la prostate", "C72");
		Matcher matcher = new MatcherBuilder().keywords(ent1)
				.wordNormalizer("french_stemmer", new FrenchStemmer())
				.stopwords("de", "la")
				.build();
		List<IAnnotation> annots = matcher.annot("cancer prostatique");
		assertEquals(annots.size(), 1);
		assertEquals(annots.get(0).toString(), "cancer prostatique	0 18	cancer de la prostate (C72)");
	}
}
