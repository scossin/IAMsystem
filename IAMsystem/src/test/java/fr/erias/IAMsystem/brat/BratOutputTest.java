package fr.erias.IAMsystem.brat;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.TreeSet;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;

import fr.erias.IAMsystem.ct.CT;
import fr.erias.IAMsystem.detect.DetectDictionaryEntry;
import fr.erias.IAMsystem.detect.Synonym;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.load.Loader;
import fr.erias.IAMsystem.normalizer.StopwordsImpl;
import fr.erias.IAMsystem.tokenizer.Tokenizer;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystem.tree.TokenTree;

public class BratOutputTest {
	public static SetTokenTree getSetTokenTreeTest() {
		SetTokenTree setTokenTree = new SetTokenTree();
		// first term of the terminology
		String term = "avc sylvien droit";
		Tokenizer tokenizer = new Tokenizer();
		String[] tokensArray = tokenizer.tokenize(term);
		TokenTree tokenTree = new TokenTree(null,tokensArray,"I63");
		setTokenTree.addTokenTree(tokenTree);

		// second term of the terminology
		term = "insuffisance cardiaque aigue";
		tokensArray = tokenizer.tokenize(term);
		tokenTree = new TokenTree(null,tokensArray,"I50");
		setTokenTree.addTokenTree(tokenTree);
		
		return(setTokenTree);
	}

	@Test
	public void BratOutputCTTest() throws IOException, UnfoundTokenInSentence, ParseException{
		// Tokenizer
		StopwordsImpl stopwords = new StopwordsImpl();
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
		
		String sentence = "Insuf.            cardiaqu aigue et AVC hémorragiQUE";
		
		detectDictionaryEntry.detectCandidateTerm(sentence);
		// output : 
		TreeSet<CTbrat> setCTbrat = new TreeSet<CTbrat>();
		for (CT candidateTerm : detectDictionaryEntry.getCTcode()) {
			setCTbrat.add(new CTbrat(candidateTerm, "disease"));
		}
		
		HashSet<String> expectedBratLines = new HashSet<String>();
		expectedBratLines.add("T1	disease 0 32	Insuf.            cardiaqu aigue");
		expectedBratLines.add("T2	disease 36 52	AVC hémorragiQUE");
		
		for (CTbrat ct : setCTbrat) {
			assertTrue(expectedBratLines.contains(ct.getBratEntity().toString()));
		}
	}
}
