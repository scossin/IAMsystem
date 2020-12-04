package fr.erias.IAMsystem.detect;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizer.Tokenizer;
import fr.erias.IAMsystem.tokenizernormalizer.TNoutput;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystem.tree.TokenTree;

public class DetectionTest {

	public static SetTokenTree getSetTokenTreeTest() {
		SetTokenTree setTokenTree = new SetTokenTree();
		// first term of the terminology
		String term = "avc sylvien droit";
		ITokenizer tokenizer = ITokenizer.getDefaultTokenizer();
		String[] tokensArray = tokenizer.tokenize(term);
		TokenTree tokenTree = new TokenTree(null,tokensArray,"I63");
		setTokenTree.addTokenTree(tokenTree);
		
		// second term of the terminology
		term = "avc hemorragique";
		tokensArray = tokenizer.tokenize(term);
		tokenTree = new TokenTree(null,tokensArray,"I61");
		setTokenTree.addTokenTree(tokenTree);
		
		// third term of the terminology
		term = "insuffisance cardiaque aigue";
		tokensArray = tokenizer.tokenize(term);
		tokenTree = new TokenTree(null,tokensArray,"I50");
		setTokenTree.addTokenTree(tokenTree);
		
		
		term = "abces";
		tokensArray = tokenizer.tokenize(term);
		tokenTree = new TokenTree(null,tokensArray,"X1");
		setTokenTree.addTokenTree(tokenTree);
		
		term = "abces chambre implantable";
		tokensArray = tokenizer.tokenize(term);
		tokenTree = new TokenTree(null,tokensArray,"X10");
		setTokenTree.addTokenTree(tokenTree);
		
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

		// load the dictionary :
		SetTokenTree tokenTreeSet0 = getSetTokenTreeTest();

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
