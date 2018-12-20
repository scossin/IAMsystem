package fr.erias.IAMsystem.detect;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.load.Loader;
import fr.erias.IAMsystem.normalizer.StopwordsImpl;
import fr.erias.IAMsystem.tokenizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystem.tree.TokenTree;

public class DetectionTest {

	public static SetTokenTree getSetTokenTreeTest() {
		SetTokenTree setTokenTree = new SetTokenTree();
		// first term of the terminology
		String term = "avc sylvien droit";
		String[] tokensArray = TokenizerNormalizer.tokenizeAlphaNum(term);
		TokenTree tokenTree = new TokenTree(null,tokensArray,"I63");
		setTokenTree.addTokenTree(tokenTree);
		
		// second term of the terminology
		term = "avc hemorragique";
		tokensArray = TokenizerNormalizer.tokenizeAlphaNum(term);
		tokenTree = new TokenTree(null,tokensArray,"I61");
		setTokenTree.addTokenTree(tokenTree);
		
		// third term of the terminology
		term = "insuffisance cardiaque aigue";
		tokensArray = TokenizerNormalizer.tokenizeAlphaNum(term);
		tokenTree = new TokenTree(null,tokensArray,"I50");
		setTokenTree.addTokenTree(tokenTree);
		
		
		term = "abces";
		tokensArray = TokenizerNormalizer.tokenizeAlphaNum(term);
		tokenTree = new TokenTree(null,tokensArray,"X1");
		setTokenTree.addTokenTree(tokenTree);
		
		term = "abces chambre implantable";
		tokensArray = TokenizerNormalizer.tokenizeAlphaNum(term);
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
		TokenizerNormalizer tokenizerNormalizer = Loader.getTokenizerNormalizer(stopwords);

		// Abbreviations : 
		Synonym abbreviations = new Synonym() {
			@Override
			public HashSet<String[]> getSynonyms(String token) {
				HashSet<String[]> synonym = new HashSet<String[]>();
				if (token.equals("insuf")) {
					String[] temp = {"insuffisance"};
					synonym.add(temp);
				}
				return synonym;
			}
		};

		// Levenshtein distance : 
		// simulating a levenshtein distance : 
		Synonym levenshtein = new Synonym() {
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
		HashSet<Synonym> synonyms = new HashSet<Synonym>();
		synonyms.add(abbreviations);
		synonyms.add(levenshtein);

		// load the dictionary :
		SetTokenTree tokenTreeSet0 = getSetTokenTreeTest();

		// class that detects dictionary entries
		DetectDictionaryEntry detectDictionaryEntry = new DetectDictionaryEntry(tokenTreeSet0,
				tokenizerNormalizer,synonyms);
		
		String sentence = "Insuf.            cardiaqu aigue";
		detectDictionaryEntry.detectCandidateTerm(sentence);
		
		// only one match : 
		assertEquals(detectDictionaryEntry.getCTcode().size(), 1);
		CTcode CTdetected = detectDictionaryEntry.getCTcode().iterator().next();
		
		assertEquals(CTdetected.getCandidateTermString(), sentence);
		assertEquals(CTdetected.getCandidateTerm(), "insuf cardiaqu aigue");
		
		// getPreviousCode test : 
		sentence = "Abcès de la chambre anterieure"; // must find abces only 
		detectDictionaryEntry.detectCandidateTerm(sentence);
		// only one match : 
		assertEquals(detectDictionaryEntry.getCTcode().size(), 1);
		CTdetected = detectDictionaryEntry.getCTcode().iterator().next();
		assertEquals(CTdetected.getCandidateTerm(), "abces");
		assertEquals(CTdetected.getCode(), "X1"); // the abces code
	}
}
