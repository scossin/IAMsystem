package fr.erias.IAMsystem.prefix;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.detect.DetectDictionaryEntry;
import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.DetectionTest;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.synonym.PrefixDetector;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;

public class PrefixDetectorTest {

	@Test
	public void prefixtest() {
		// Tokenizer
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer();

		// Abbreviations : 
		Set<String> tokens = new HashSet<String>();
		tokens.add("insuffisance");
		tokens.add("cardiaque");
		
		ISynonym prefixDetector = new PrefixDetector(tokens);
		HashSet<ISynonym> synonyms = new HashSet<ISynonym>();
		synonyms.add(prefixDetector);

		// load the dictionary :
		SetTokenTree tokenTreeSet0 = DetectionTest.getSetTokenTreeTest();

		// class that detects dictionary entries
		DetectDictionaryEntry detectDictionaryEntry = new DetectDictionaryEntry(tokenTreeSet0,
				tokenizerNormalizer,synonyms);

		String sentence = "Insuf.            cardiaqu aigue";
		DetectOutput detectOutput = detectDictionaryEntry.detectCandidateTerm(sentence);

		// only one match : 
		assertEquals(detectOutput.getCTcodes().size(), 1);
		CTcode CTdetected = detectOutput.getCTcodes().iterator().next();

		assertEquals(CTdetected.getCandidateTermString(), sentence);
		assertEquals(CTdetected.getCandidateTerm(), "insuf cardiaqu aigue");
	}
}
