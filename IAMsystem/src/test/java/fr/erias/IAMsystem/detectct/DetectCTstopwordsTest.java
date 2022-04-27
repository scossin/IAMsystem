package fr.erias.IAMsystem.detectct;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Test;

import fr.erias.IAMsystem.detect.DetectCT;
import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.IDetectCT;
import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.normalizer.Normalizer;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizer.TokenizerWhiteSpace;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.Trie;

public class DetectCTstopwordsTest {

	public static Trie getTrieTest() {
		INormalizer normalizer = new Normalizer();
		ITokenizer tokenizer = new TokenizerWhiteSpace();
		Trie trie = new Trie();
		Term term1 = new Term("vitamine a", "I63");
		trie.addTerm(term1, tokenizer, normalizer);
		return(trie);
	}

	@Test
	public void detectStopwordDiacriticsTest() {
		HashSet<String> stopwordsSet = new HashSet<String>();
		stopwordsSet.add("à");
		StopwordsImpl stopwords = new StopwordsImpl(stopwordsSet);
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer(stopwords);
		// class that detects dictionary entries
		IDetectCT detector = new DetectCT(getTrieTest(),
				tokenizerNormalizer,new HashSet<ISynonym>());
		DetectOutput output = detector.detectCandidateTerm("prendre une vitamine à la maison");
		assertEquals(0, output.getCTcodes().size());
	}
}
