package fr.erias.IAMsystem.detectct;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.detect.DetectCT;
import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.IDetectCT;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.normalizer.Normalizer;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizer.TokenizerWhiteSpace;
import fr.erias.IAMsystem.tokenizernormalizer.TNoutput;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.Trie;

public class DetectionImpTest {

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

	@Test
	public void detectTrieTest() throws IOException, UnfoundTokenInSentence, ParseException{
		// Tokenizer
		HashSet<String> stopwordsSet = new HashSet<String>();
		stopwordsSet.add("de");
		stopwordsSet.add("la");
		StopwordsImpl stopwords = new StopwordsImpl(stopwordsSet);
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer(stopwords);

		// Abbreviations : 
		Abbreviations abbreviations = new Abbreviations();
		abbreviations.addAbbreviation("insuffisance", "insuf",tokenizerNormalizer);

		// Levenshtein distance : 
		// simulating a levenshtein distance : 
		ISynonym levenshtein = new ISynonym() {
			@Override
			public HashSet<String[]> getSynonyms(String token) {
				HashSet<String[]> synonym = new HashSet<String[]>();
				if (token.equals("cardiaqu")) {
					String[] temp = {"cardiaque"};
					synonym.add(temp);
				}
				return synonym;
			}
		};

		// find synonyms with abbreviations and typos : 
		HashSet<ISynonym> synonyms = new HashSet<ISynonym>();
		synonyms.add(abbreviations);
		synonyms.add(levenshtein);

		// class that detects dictionary entries
		IDetectCT detectDictionaryEntry = new DetectCT(getTrieTest(),
				tokenizerNormalizer,synonyms);
		
		String sentence = "Insuf.            cardiaqu aigue";
		DetectOutput detectOutput = detectDictionaryEntry.detectCandidateTerm(sentence);
		
		// only one match : 
		assertEquals(detectOutput.getCTcodes().size(), 1);
		CTcode CTdetected = detectOutput.getCTcodes().iterator().next();
		
		assertEquals(CTdetected.getCandidateTermString(), sentence);
		assertEquals(CTdetected.getCandidateTerm(), "insuf cardiaqu aigue");
		
		// getPreviousCode test : 
		sentence = "Abcès de la chambre anterieure"; // must find abces only 
		detectOutput = detectDictionaryEntry.detectCandidateTerm(sentence);
		// only one match : 
		assertEquals(detectOutput.getCTcodes().size(), 1);
		CTdetected = detectOutput.getCTcodes().iterator().next();
		assertEquals(CTdetected.getCandidateTerm(), "abces");
		assertEquals(CTdetected.getCode(), "X1"); // the abces code
		
		// detection with stopwords: 
		sentence = "Abcès chambre implantable"; // must find abces de la chambre implantable
		detectOutput = detectDictionaryEntry.detectCandidateTerm(sentence);
		// only one match : 
		assertEquals(detectOutput.getCTcodes().size(), 1);
		CTdetected = detectOutput.getCTcodes().iterator().next();
		assertEquals(CTdetected.getCandidateTermString(), sentence);
		assertEquals(CTdetected.getCode(), "X10"); // the "abces de la chambre implantable" code
		
		TNoutput tnoutput = detectOutput.getTNoutput();
		int tokenStart = CTdetected.getTokenStartPosition();
		int tokenEnd = CTdetected.getTokenEndPosition();
		String[] tokens = tnoutput.getTokens();
		assertEquals(tokens[tokenStart], "abces");
		assertEquals(tokens[tokenEnd], "implantable");
	}
	
	@Test
	public void detectDeadEndTest() {
		INormalizer normalizer = new Normalizer();
		ITokenizer tokenizer = new TokenizerWhiteSpace();
		Trie trie = new Trie();
		Term term1 = new Term("syndrome triple a", "1");
		Term term2 = new Term("triple", "2");
		Term term3 = new Term("syndrome", "3");
		trie.addTerm(term1, tokenizer, normalizer);
		trie.addTerm(term2, tokenizer, normalizer);
		trie.addTerm(term3, tokenizer, normalizer);
		HashSet<ISynonym> synonyms = new HashSet<ISynonym>();
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer();
		IDetectCT detectDictionaryEntry = new DetectCT(trie,tokenizerNormalizer,synonyms);
		String sentence = "syndrome triple x"; 
		DetectOutput detectOutput = detectDictionaryEntry.detectCandidateTerm(sentence);
		assertEquals(detectOutput.getCTcodes().size(),1);
		// syndrome is detected
		// triple is not a term
		// a is a deadend
		// The algorithm restarts at token 'a' (not 'triple' and it's not detected). 
		// This is the only detection difference with IAMsystem 1.1.0 that used TokenTree method. 
	}
}
