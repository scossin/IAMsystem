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
import fr.erias.IAMsystem.tokenizer.Tokenizer;
import fr.erias.IAMsystem.tokenizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystem.tree.TokenTree;

public class DetectionBackwardTest {

	public static SetTokenTree getSetTokenTreeTest() {
		SetTokenTree setTokenTree = new SetTokenTree();
		// first term of the terminology
		String term = "saignement";
		Tokenizer tokenizer = new Tokenizer();
		String[] tokensArray = tokenizer.tokenize(term);
		TokenTree tokenTree = new TokenTree(null,tokensArray,"X1");
		setTokenTree.addTokenTree(tokenTree);
		
		// second term of the terminology
		term = "saignement abondants de la menopause";
		tokensArray = tokenizer.tokenize(term);
		tokenTree = new TokenTree(null,tokensArray,"X2");
		setTokenTree.addTokenTree(tokenTree);
		
		// second term of the terminology
		term = "saignement abondants de la menopause suite à un traitement anticoagulant";
		tokensArray = tokenizer.tokenize(term);
		tokenTree = new TokenTree(null,tokensArray,"X3");
		setTokenTree.addTokenTree(tokenTree);
		
		return(setTokenTree);
	}

	@Test
	public void DetectionSaignementTest() throws IOException, UnfoundTokenInSentence, ParseException{
		Synonym synonym = new Synonym() {
			@Override
			public HashSet<String[]> getSynonyms(String token) {
				HashSet<String[]> synonym = new HashSet<String[]>();
				if (token.equals("saignements")) {
					String[] temp = {"saignement"};
					synonym.add(temp);
				}
				return synonym;
			}
		};
		HashSet<Synonym> synonyms = new HashSet<Synonym>();
		synonyms.add(synonym);
		TokenizerNormalizer tokenizerNormalizer = Loader.getTokenizerNormalizer(new StopwordsImpl());
		DetectDictionaryEntry detectDictionaryEntry = new DetectDictionaryEntry(getSetTokenTreeTest(),
				tokenizerNormalizer,synonyms);
		
		detectDictionaryEntry.detectCandidateTerm("saignements abondants");
		// only one match : 
		assertEquals(detectDictionaryEntry.getCTcode().size(), 1);
		CTcode CTdetected = detectDictionaryEntry.getCTcode().iterator().next();
		assertEquals(CTdetected.getCandidateTerm(), "saignements");
		assertEquals(CTdetected.getCandidateTermString(),"saignements");
		assertEquals(CTdetected.getCode(), "X1"); // the abces code
		System.out.println(CTdetected.getJSONobject().toString());
	}
	
	@Test
	public void DetectionSaignementMenopauseTest() throws IOException, UnfoundTokenInSentence, ParseException{
		Synonym synonym = new Synonym() {
			@Override
			public HashSet<String[]> getSynonyms(String token) {
				HashSet<String[]> synonym = new HashSet<String[]>();
				if (token.equals("saignements")) {
					String[] temp = {"saignement"};
					synonym.add(temp);
				}
				return synonym;
			}
		};
		HashSet<Synonym> synonyms = new HashSet<Synonym>();
		synonyms.add(synonym);
		TokenizerNormalizer tokenizerNormalizer = Loader.getTokenizerNormalizer(new StopwordsImpl());
		DetectDictionaryEntry detectDictionaryEntry = new DetectDictionaryEntry(getSetTokenTreeTest(),
				tokenizerNormalizer,synonyms);
		
		detectDictionaryEntry.detectCandidateTerm("saignements abondants de la       ménopause suite à");
		CTcode CTdetected = detectDictionaryEntry.getCTcode().iterator().next();
		System.out.println(CTdetected.getJSONobject().toString());
		// only one match : 
		assertEquals(detectDictionaryEntry.getCTcode().size(), 1);
		assertEquals(CTdetected.getCandidateTerm(), "saignements abondants de la menopause");
		assertEquals(CTdetected.getCandidateTermString(),"saignements abondants de la       ménopause");
		assertEquals(CTdetected.getCode(), "X2"); // the abces code
	}
	
	@Test
	public void DetectionSaignementMenopauseStopwordsTest() throws IOException, UnfoundTokenInSentence, ParseException{
		Synonym synonym = new Synonym() {
			@Override
			public HashSet<String[]> getSynonyms(String token) {
				HashSet<String[]> synonym = new HashSet<String[]>();
				if (token.equals("saignements")) {
					String[] temp = {"saignement"};
					synonym.add(temp);
				}
				return synonym;
			}
		};
		HashSet<Synonym> synonyms = new HashSet<Synonym>();
		synonyms.add(synonym);
		StopwordsImpl stopwords = new StopwordsImpl();
		stopwords.addStopwords("de");
		stopwords.addStopwords("la");
		stopwords.addStopwords("suite");
		stopwords.addStopwords("a");
		TokenizerNormalizer tokenizerNormalizer = Loader.getTokenizerNormalizer(stopwords);
		DetectDictionaryEntry detectDictionaryEntry = new DetectDictionaryEntry(getSetTokenTreeTest(),
				tokenizerNormalizer,synonyms);
		
		detectDictionaryEntry.detectCandidateTerm("saignements abondants de la       ménopause suite à un traitement");
		CTcode CTdetected = detectDictionaryEntry.getCTcode().iterator().next();
		System.out.println(CTdetected.getJSONobject().toString());
		// only one match : 
		assertEquals(detectDictionaryEntry.getCTcode().size(), 1);
		assertEquals(CTdetected.getCandidateTerm(), "saignements abondants de la menopause");
		assertEquals(CTdetected.getCandidateTermString(),"saignements abondants de la       ménopause");
		assertEquals(CTdetected.getCode(), "X2"); // the abces code
	}
}
