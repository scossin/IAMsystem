package fr.erias.IAMsystem.fuzzymatching;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.ClosestSubString;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.tree.PrefixTrie;

public class ClosestSubStringTest {

	@Test
	public void detectionOneCharDiffTest() {
		TermDetector termDetector = new TermDetector();
		PrefixTrie prefixTrie = new PrefixTrie(5);
		ClosestSubString closestSub = new ClosestSubString(prefixTrie, 1);
		termDetector.addFuzzyAlgorithm(closestSub);
		Term term = new Term ("ulcere gastrique", "I50");
		termDetector.addTerm(term);
		prefixTrie.addTerm(term, new StopwordsImpl());
		DetectOutput output = termDetector.detect("ulceres gastriques");
		assertEquals(output.getCTcodes().size(), 1);
	}
	
	@Test
	public void noDetectionTest() {
		TermDetector termDetector = new TermDetector();
		PrefixTrie prefixTrie = new PrefixTrie(5);
		ClosestSubString closestSub = new ClosestSubString(prefixTrie, 1);
		termDetector.addFuzzyAlgorithm(closestSub);
		Term term = new Term ("ulcere gastrique", "I50");
		termDetector.addTerm(term);
		prefixTrie.addTerm(term, new StopwordsImpl());
		DetectOutput output = termDetector.detect("ulceres gastriquess"); // max distance is 2
		assertEquals(output.getCTcodes().size(), 0);
	}
	
	@Test
	public void synonymFoundTest() {
		PrefixTrie prefix = new PrefixTrie(8);
		ClosestSubString closestSubStr = new ClosestSubString(prefix, 10000);
		Term term = new Term("high blood pressure", "I10");
		prefix.addTerm(term, new StopwordsImpl());
		for (List<String> list : closestSubStr.getSynonyms("pressuresss")) {
			assertEquals(list.toString(), "[pressure]");
		}
	}
}
