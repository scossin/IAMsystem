package fr.erias.IAMsystem.detect;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizer.Tokenizer;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystem.tree.TokenTree;

public class DetectionBackwardTest {

	public static SetTokenTree getSetTokenTreeTest() {
		SetTokenTree setTokenTree = new SetTokenTree();
		// first term of the terminology
		String term = "saignement";
		ITokenizer tokenizer = ITokenizer.getDefaultTokenizer();
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
		ISynonym synonym = new ISynonym() {
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
		HashSet<ISynonym> synonyms = new HashSet<ISynonym>();
		synonyms.add(synonym);
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer();
		DetectDictionaryEntry detectDictionaryEntry = new DetectDictionaryEntry(getSetTokenTreeTest(),
				tokenizerNormalizer,synonyms);
		
		String sentence = "saignements abondants";
		DetectOutput detectOutput = detectDictionaryEntry.detectCandidateTerm(sentence);
		// only one match : 
		assertEquals(detectOutput.getCTcodes().size(), 1);
		CTcode CTdetected = detectOutput.getCTcodes().iterator().next();
		assertEquals(CTdetected.getCandidateTerm(), "saignements");
		assertEquals(CTdetected.getCandidateTermString(),"saignements");
		assertEquals(CTdetected.getCode(), "X1"); // the abces code
		System.out.println(CTdetected.getJSONobject().toString());
	}
	
	@Test
	public void DetectionSaignementMenopauseTest() throws IOException, UnfoundTokenInSentence, ParseException{
		ISynonym synonym = new ISynonym() {
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
		HashSet<ISynonym> synonyms = new HashSet<ISynonym>();
		synonyms.add(synonym);
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer();
		DetectDictionaryEntry detectDictionaryEntry = new DetectDictionaryEntry(getSetTokenTreeTest(),
				tokenizerNormalizer,synonyms);
		
		String sentence = "saignements abondants de la       ménopause suite à";
		DetectOutput detectOutput = detectDictionaryEntry.detectCandidateTerm(sentence);
		CTcode CTdetected = detectOutput.getCTcodes().iterator().next();
		System.out.println(CTdetected.getJSONobject().toString());
		// only one match : 
		assertEquals(detectOutput.getCTcodes().size(), 1);
		assertEquals(CTdetected.getCandidateTerm(), "saignements abondants de la menopause");
		assertEquals(CTdetected.getCandidateTermString(),"saignements abondants de la       ménopause");
		assertEquals(CTdetected.getCode(), "X2"); // the abces code
	}
	
	@Test
	public void DetectionSaignementMenopauseStopwordsTest() throws IOException, UnfoundTokenInSentence, ParseException{
		ISynonym synonym = new ISynonym() {
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
		HashSet<ISynonym> synonyms = new HashSet<ISynonym>();
		synonyms.add(synonym);
		StopwordsImpl stopwords = new StopwordsImpl();
		stopwords.addStopwords("de");
		stopwords.addStopwords("la");
		stopwords.addStopwords("suite");
		stopwords.addStopwords("a");
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer(stopwords);
		DetectDictionaryEntry detectDictionaryEntry = new DetectDictionaryEntry(getSetTokenTreeTest(),
				tokenizerNormalizer,synonyms);
		
		String sentence = "saignements abondants de la       ménopause suite à un traitement";
		DetectOutput detectOutput = detectDictionaryEntry.detectCandidateTerm(sentence);
		CTcode CTdetected = detectOutput.getCTcodes().iterator().next();
		System.out.println(CTdetected.getJSONobject().toString());
		// only one match : 
		assertEquals(detectOutput.getCTcodes().size(), 1);
		assertEquals(CTdetected.getCandidateTerm(), "saignements abondants de la menopause");
		assertEquals(CTdetected.getCandidateTermString(),"saignements abondants de la       ménopause");
		assertEquals(CTdetected.getCode(), "X2"); // the abces code
	}
}
