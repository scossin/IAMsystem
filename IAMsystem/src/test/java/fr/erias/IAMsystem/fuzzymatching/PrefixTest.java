package fr.erias.IAMsystem.detectct;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.detect.DetectCT;
import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.DetectionTest;
import fr.erias.IAMsystem.detect.IDetectCT;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.normalizer.Normalizer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.synonym.Prefix;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizer.TokenizerWhiteSpace;
import fr.erias.IAMsystem.tokenizernormalizer.TNoutput;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.Trie;

public class PrefixTest {

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
	
	public static int getPrefixSize(int minPrefixSize, int maxCharDiff) {
		Prefix prefixDetector = new Prefix(minPrefixSize, maxCharDiff);
		IStopwords stopwords = new StopwordsImpl();
		Term term = new Term("insuffisanc","I09");
		Term term2 = new Term("insuffisance cardiaque","I10");
		Term term3 = new Term("insuffisances respi","I11");
		prefixDetector.addTerm(term, stopwords);
		prefixDetector.addTerm(term2, stopwords);
		prefixDetector.addTerm(term3, stopwords);
		Set<List<String>> syns = prefixDetector.getSynonyms("insuffisanc");
		return(syns.size());
	}

	@Test
	public void synonymsSizeTest() throws IOException, UnfoundTokenInSentence, ParseException{
		// insuffisanc is searched and has 11 characters  
		assertEquals(getPrefixSize(12, 1000), 0); // because insuffisanc.length < 12
		assertEquals(getPrefixSize(11, 1000), 3); // insuffisanc, insuffisance and insuffisances
		assertEquals(getPrefixSize(11, 1), 2); // insuffisanc, insuffisance because 1 char diff max
		assertEquals(getPrefixSize(11, 0), 1); // insuffisanc only because 0 char diff (perfect match)
	}
	
	@Test
	public void detectionTest() throws IOException, UnfoundTokenInSentence, ParseException{
		// insuffisanc is searched and has 11 characters  
		TermDetector termDetector = new TermDetector();
		Term term3 = new Term ("insuffisance cardiaque aigue", "I50");
		termDetector.addTerm(term3);
		Prefix prefixDetector = new Prefix(3, 2); // 3 because aig is 3 characters
		prefixDetector.addTerm(term3, new StopwordsImpl());
		termDetector.addSynonym(prefixDetector);
		DetectOutput output = termDetector.detect("insuffisan cardiaq aig");
		assertEquals(output.getCTcodes().size(), 1);
	}
}
