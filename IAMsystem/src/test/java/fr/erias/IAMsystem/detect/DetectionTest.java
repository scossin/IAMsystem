package fr.erias.IAMsystem.detect;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.tokenizernormalizer.TNoutput;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;

@Deprecated
public class DetectionTest {

	public static ISynonym getCardiaquTypo() {
		ISynonym levenshtein = new ISynonym() {
			@Override
			public Set<List<String>> getSynonyms(String token) {
				Set<List<String>> synonym = new HashSet<List<String>>();
				if (token.equals("cardiaqu")) {
					String[] temp = {"cardiaque"};
					synonym.add(Arrays.asList(temp));
				}
				return synonym;
			}
		};
		return(levenshtein);
	}
	
	public static SetTokenTree getSetTokenTreeTest() {
		SetTokenTree setTokenTree = new SetTokenTree();
		setTokenTree.addTokenTree(DetectionBackwardTest.getTokenTree("avc sylvien droit", "I63"));
		setTokenTree.addTokenTree(DetectionBackwardTest.getTokenTree("avc hemorragique", "I61"));
		setTokenTree.addTokenTree(DetectionBackwardTest.getTokenTree("insuffisance cardiaque aigue", "I50"));
		setTokenTree.addTokenTree(DetectionBackwardTest.getTokenTree("abces", "X1"));
		setTokenTree.addTokenTree(DetectionBackwardTest.getTokenTree("abces chambre implantable", "X10"));
		return(setTokenTree);
	}

	@Test
	public void DetectionTermTest() throws IOException, UnfoundTokenInSentence, ParseException{
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
			public Set<List<String>> getSynonyms(String token) {
				Set<List<String>> synonym = new HashSet<List<String>>();
				if (token.equals("cardiaqu")) {
					String[] temp = {"cardiaque"};
					synonym.add(Arrays.asList(temp));
				}
				return synonym;
			}
		};
		// find synonyms with abbreviations and typos : 
		HashSet<ISynonym> fuzzyAlgorithms = new HashSet<ISynonym>();
		fuzzyAlgorithms.add(abbreviations);
		fuzzyAlgorithms.add(levenshtein);

		// load the dictionary :
		SetTokenTree tokenTreeSet0 = getSetTokenTreeTest();

		// class that detects dictionary entries
		IDetectCT detectDictionaryEntry = new DetectDictionaryEntry(tokenTreeSet0,
				tokenizerNormalizer,fuzzyAlgorithms);
		
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
		
		// detection with stopwords: 
		TNoutput tnoutput = detectOutput.getTNoutput();
		int tokenStart = CTdetected.getTokenStartPosition();
		int tokenEnd = CTdetected.getTokenEndPosition();
		String[] tokens = tnoutput.getTokens();
		assertEquals(tokens[tokenStart], "abces");
		assertEquals(tokens[tokenEnd], "implantable");
	}
}
