package fr.erias.iamsystem_java.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.liblevenshtein.transducer.Algorithm;

import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.tokenize.IToken;

class DocTest
{

	@BeforeEach
	void setUp() throws Exception
	{
	}

	@Test
	void testExactMatchKeyword()
	{
		Matcher matcher = new Matcher.Builder<IToken>().keywords("acute respiratory distress syndrome", "diarrrhea")
				.build();
		List<IAnnotation<IToken>> annots = matcher.annot("Pt c/o Acute Respiratory Distress Syndrome and diarrrhea");
		assertEquals(2, annots.size());
		IAnnotation<IToken> ann = annots.get(1);
		assertEquals(ann.toString(), "diarrrhea	47 56	diarrrhea");
	}

	@Test
	void testReadmeExample()
	{
		Matcher matcher = new Matcher.Builder<IToken>().keywords("North America", "South America").stopwords("and")
				.abbreviations("amer", "America").levenshtein(4, 1, Algorithm.TRANSPOSITION).w(2).build();
		List<IAnnotation<IToken>> annots = matcher.annot("Northh and south Amer.");
		assertEquals(2, annots.size());
	}

//    matcher = Matcher.build(
//            keywords=["acute respiratory distress syndrome", "diarrrhea"]
//        )
//        annots = matcher.annot_text(
//            text="Pt c/o Acute Respiratory Distress Syndrome and diarrrhea"
//        )
//        for annot in annots:
//            print(annot)
}
