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
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.tokenizer.ITokenizer;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystem.tree.TokenTree;

@Deprecated
public class DetectionBackwardTest {

	public static TokenTree getTokenTree(String label, String code) {
		Term term = new Term(label,code);
		ITokenizer tokenizer = ITokenizer.getDefaultTokenizer();
		String[] tokensArray = tokenizer.tokenize(term.getLabel());
		TokenTree tokenTree = new TokenTree(null,tokensArray,term);
		return(tokenTree);
	}
	
	public static SetTokenTree getSetTokenTreeTest() {
		SetTokenTree setTokenTree = new SetTokenTree();
		// first term of the terminology
		setTokenTree.addTokenTree(getTokenTree("saignement", "X1"));
		
		// second term of the terminology
		setTokenTree.addTokenTree(getTokenTree("saignement abondants de la menopause","X2"));
		
		// third term of the terminology
		setTokenTree.addTokenTree(getTokenTree(
				"saignement abondants de la menopause suite à un traitement anticoagulant","X3"));
		
		// fourth term of the terminology
		setTokenTree.addTokenTree(getTokenTree("anticoagulant","X4"));
		return(setTokenTree);
	}

	@Test
	public void DetectionSaignementTest() throws IOException, UnfoundTokenInSentence, ParseException{
		ISynonym synonym = new ISynonym() {
			@Override
			public Set<List<String>> getSynonyms(String token) {
				Set<List<String>> synonym = new HashSet<List<String>>();
				if (token.equals("saignements")) {
					String[] temp = {"saignement"};
					synonym.add(Arrays.asList(temp));
				}
				return synonym;
			}
		};
		HashSet<ISynonym> synonyms = new HashSet<ISynonym>();
		synonyms.add(synonym);
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer();
		IDetectCT detectDictionaryEntry = new DetectDictionaryEntry(getSetTokenTreeTest(),
				tokenizerNormalizer,synonyms);
		
		String sentence = "saignements abondants";
		DetectOutput detectOutput = detectDictionaryEntry.detectCandidateTerm(sentence);
		// only one match : 
		assertEquals(detectOutput.getCTcodes().size(), 1);
		CTcode CTdetected = detectOutput.getCTcodes().iterator().next();
		assertEquals(CTdetected.getCandidateTerm(), "saignements");
		assertEquals(CTdetected.getCandidateTermString(),"saignements");
		assertEquals(CTdetected.getCode(), "X1"); // the abces code
	}
	
	@Test
	public void DetectionSaignementMenopauseTest() throws IOException, UnfoundTokenInSentence, ParseException{
		ISynonym synonym = new ISynonym() {
			@Override
			public Set<List<String>> getSynonyms(String token) {
				Set<List<String>> synonym = new HashSet<List<String>>();
				if (token.equals("saignements")) {
					String[] temp = {"saignement"};
					synonym.add(Arrays.asList(temp));
				}
				return synonym;
			}
		};
		HashSet<ISynonym> synonyms = new HashSet<ISynonym>();
		synonyms.add(synonym);
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer();
		IDetectCT detectDictionaryEntry = new DetectDictionaryEntry(getSetTokenTreeTest(),
				tokenizerNormalizer,synonyms);
		
		String sentence = "saignements abondants de la       ménopause suite à";
		DetectOutput detectOutput = detectDictionaryEntry.detectCandidateTerm(sentence);
		CTcode CTdetected = detectOutput.getCTcodes().iterator().next();
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
			public Set<List<String>> getSynonyms(String token) {
				Set<List<String>> synonym = new HashSet<List<String>>();
				if (token.equals("saignements")) {
					String[] temp = {"saignement"};
					synonym.add(Arrays.asList(temp));
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
		IDetectCT detectDictionaryEntry = new DetectDictionaryEntry(getSetTokenTreeTest(),
				tokenizerNormalizer,synonyms);
		
		String sentence = "saignements abondants de la       ménopause suite à un traitement";
		DetectOutput detectOutput = detectDictionaryEntry.detectCandidateTerm(sentence);
		CTcode CTdetected = detectOutput.getCTcodes().iterator().next();
		// only one match : 
		assertEquals(detectOutput.getCTcodes().size(), 1);
		assertEquals(CTdetected.getCandidateTerm(), "saignements abondants de la menopause");
		assertEquals(CTdetected.getCandidateTermString(),"saignements abondants de la       ménopause");
		assertEquals(CTdetected.getCode(), "X2"); // the abces code
	}
	
	ISynonym levenshtein = new ISynonym() {
		@Override
		public Set<List<String>> getSynonyms(String token) {
			Set<List<String>> synonyms = new HashSet<List<String>>();
			if (token.equals("cardiaqu")) {
				String[] longForm = {"cardiaque"};
				synonyms.add(Arrays.asList(longForm));
			}
			return synonyms;
		}
	};
	@Test
	public void DetectionAfterBackwardTest() throws IOException, UnfoundTokenInSentence, ParseException{
		ISynonym synonym = new ISynonym() {
			@Override
			public Set<List<String>> getSynonyms(String token) {
				Set<List<String>> synonym = new HashSet<List<String>>();
				if (token.equals("saignements")) {
					String[] temp = {"saignement"};
					synonym.add(Arrays.asList(temp));
				}
				if (token.equals("anticoag")) {
					String[] temp = {"anticoagulant"};
					synonym.add(Arrays.asList(temp));
					String[] temp2 = {"suite"}; // anticoag has 2 meanings possibles
					synonym.add(Arrays.asList(temp2));
				}
				return synonym;
			}
		};
		HashSet<ISynonym> synonyms = new HashSet<ISynonym>();
		synonyms.add(synonym);
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer();
		IDetectCT detectDictionaryEntry = new DetectDictionaryEntry(getSetTokenTreeTest(),
				tokenizerNormalizer,synonyms);
		//term = "saignement abondants de la menopause suite à un traitement anticoagulant";
		String sentence = "saignements abondants de la ménopause anticoag fin";
		DetectOutput detectOutput = detectDictionaryEntry.detectCandidateTerm(sentence);
		assertEquals(detectOutput.getCTcodes().size(), 2);
		
		// Test for these lines: 
		//int diff = treeLocation.getMonitorCandidates().getDiff();
		//treeLocation.setCurrentI(treeLocation.getCurrentI() - diff);
		
		// Explanation: anticoag is a path toward X2, so the algorithm takes it but it's a dead end
		// so it needs to come back just after menopause and enters this new path : anticoagulant word (not a dead end)
	}
}
