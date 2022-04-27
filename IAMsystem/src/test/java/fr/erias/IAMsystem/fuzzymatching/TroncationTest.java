package fr.erias.IAMsystem.fuzzymatching;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.normalizer.Normalizer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.Troncation;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizer.TokenizerWhiteSpace;
import fr.erias.IAMsystem.tree.PrefixTrie;
import fr.erias.IAMsystem.tree.Trie;

public class TroncationTest {

	public static Trie getTrieTest() {
		INormalizer normalizer = new Normalizer();
		ITokenizer tokenizer = new TokenizerWhiteSpace();
		Trie trie = new Trie();
		Term term1 = new Term("avc sylvien droit", "I63");
		Term term2 = new Term("avc hemorragique", "I61");
		Term term3 = new Term ("insuffisance cardiaque aigue", "I50");
		Term term4 = new Term("abces", "X1");
		Term term5 = new Term("abces chambre implantable", "X10");
		trie.addTerm(term1, tokenizer, normalizer);
		trie.addTerm(term2, tokenizer, normalizer);
		trie.addTerm(term3, tokenizer, normalizer);
		trie.addTerm(term4, tokenizer, normalizer);
		trie.addTerm(term5, tokenizer, normalizer);
		return(trie);
	}
	
	public static int getNumberOfTruncatedWords(int minPrefixSize, int maxDistance) {
		PrefixTrie prefixTrie = new PrefixTrie(minPrefixSize);
		IStopwords stopwords = IStopwords.noStopwords;
		Term term = new Term("insuffisanc","I09");
		Term term2 = new Term("insuffisance cardiaque","I10");
		Term term3 = new Term("insuffisances respi","I11");
		prefixTrie.addTerm(term, stopwords);
		prefixTrie.addTerm(term2, stopwords);
		prefixTrie.addTerm(term3, stopwords);
		Troncation troncation = new Troncation(prefixTrie, maxDistance);
		Set<List<String>> syns = troncation.getSynonyms("insuffisanc");
		return(syns.size());
	}

	@Test
	public void synonymsSizeTest() {
		// insuffisanc is searched and has 11 characters  
		assertEquals(getNumberOfTruncatedWords(12, 1000), 0); // because insuffisanc.length < 12
		assertEquals(getNumberOfTruncatedWords(11, 1000), 3); // insuffisanc, insuffisance and insuffisances
		assertEquals(getNumberOfTruncatedWords(11, 1), 2); // insuffisanc, insuffisance because 1 char diff max
		assertEquals(getNumberOfTruncatedWords(11, 0), 1); // insuffisanc only because 0 char diff (perfect match)
	}
	
	@Test
	public void detectionTest() {
		PrefixTrie prefixTrie = new PrefixTrie(3); // 3 because aig is 3 characters
		// insuffisanc is searched and has 11 characters  
		TermDetector termDetector = new TermDetector();
		Term term3 = new Term ("insuffisance cardiaque aigue", "I50");
		termDetector.addTerm(term3);
		Troncation troncation = new Troncation(prefixTrie, 2);
		prefixTrie.addTerm(term3, IStopwords.noStopwords);
		termDetector.addFuzzyAlgorithm(troncation);
		DetectOutput output = termDetector.detect("insuffisan cardiaq aig");
		assertEquals(output.getCTcodes().size(), 1);
	}
}
