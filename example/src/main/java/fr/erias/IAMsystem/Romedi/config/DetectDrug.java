package fr.erias.IAMsystem.Romedi.config;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.detect.DetectDictionaryEntry;
import fr.erias.IAMsystem.detect.LevenshteinTypoLucene;
import fr.erias.IAMsystem.detect.Synonym;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.load.Loader;
import fr.erias.IAMsystem.tokenizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;

/**
 * Main class used to detect dictionary entries
 * 
 * @author Cossin Sebastien
 *
 */
public class DetectDrug {

	final static Logger logger = LoggerFactory.getLogger(DetectDrug.class);
	private DetectDictionaryEntry detectDictionaryEntry;
	
	public DetectDrug() throws IOException {
		
		// load stopwords file :
		File stopwordsFile = new File(ConfigRomedi.STOPWORDS_FILE);
		StopwordsRomedi stopwordsClef = new StopwordsRomedi();
		stopwordsClef.setStopWords(stopwordsFile);
		
		// Create a tokenizer with a normalizer 
		TokenizerNormalizer tokenizerNormalizer = Loader.getTokenizerNormalizer(stopwordsClef);

		// Add abbreviations : 
		AbbreviationsDrug abbreviationsDrug = new AbbreviationsDrug();
		// load Lucene index to detect typos (create it with IndexBigramLuceneRomedi) 
		File indexFolder = new File(ConfigRomedi.INDEX_FOLDER);
		LevenshteinTypoLucene levenshteinTypoLucene = new LevenshteinTypoLucene(indexFolder,
				ConfigRomedi.CONCATENATION_FIELD, ConfigRomedi.BIGRAM_FIELD);
		
		
		// find "synonyms" with 
		HashSet<Synonym> synonyms = new HashSet<Synonym>();
		   // abbreviations
		synonyms.add(abbreviationsDrug);
		  // typos
		synonyms.add(levenshteinTypoLucene);
		
		// load the dictionary (tree datastructure)
		File CSVFile = new File(ConfigRomedi.romediTermsNormalized);
		SetTokenTree tokenTreeSet0 = Loader.loadTokenTree(CSVFile, stopwordsClef,"\t",3, 0);
		
		// return an instance that detects dictionary entries
		this.detectDictionaryEntry = new DetectDictionaryEntry(tokenTreeSet0,tokenizerNormalizer,synonyms);
		}
	
	public Set<CTcode> getCTcodes(String txtContent) throws UnfoundTokenInSentence, IOException, ParseException{
		this.detectDictionaryEntry.detectCandidateTerm(txtContent);
		return(this.detectDictionaryEntry.getCTcode());
	}
}

