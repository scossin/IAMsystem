package fr.erias.IAMsystem.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.normalizer.NormalizerTerm;
import fr.erias.IAMsystem.normalizer.Stopwords;
import fr.erias.IAMsystem.tokenizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystem.tree.TokenTree;

/**
 * Class providing static function to load files
 * @author Cossin Sebastien
 *
 */
public class Loader {

	final static Logger logger = LoggerFactory.getLogger(Loader.class);

	/**
	 * Tokenize all the terms and keep a set of unique token
	 * @param fileCSV a CSV file libNormal in the 4th column and \t as separator
	 * @param sep the separator of the CSV file (ex : "\t")
	 * @param colLibNormal the ith column containing the libnormal (normalized label of the term)
	 * @return a set of unique tokens in the vocabulary
	 * @throws IOException if the file can't be found
	 */
	public static HashSet<String> getUniqueToken(File fileCSV, String sep, int colLibNormal) throws IOException{
		HashSet<String> uniqueTokens = new HashSet<String>();
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(fileCSV));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] columns = line.split(sep);
			String libNormal = columns[colLibNormal];
			String[] tokens = TokenizerNormalizer.tokenizeAlphaNum(libNormal);
			for (String token : tokens) {
				uniqueTokens.add(token);
			}
		}
		br.close();
		return(uniqueTokens);
	}

	/**
	 * @param stopwords a {@link Stopwords} instance
	 * @param fileCSV a CSV file
	 * @param sep the separator of the CSV file (comma, tab...)
	 * @param colLibNormal the ith column of the CSV file corresponding to the normalize label to index
	 * @return A map between the collapse form and the uncollapse form (ex "meningoencephalite, meningo encephalite")
	 * @throws IOException Unfound File
	 */
	public static HashMap<String,String> getUniqueTokenBigram(Stopwords stopwords, File fileCSV, String sep, int colLibNormal) throws IOException{
		TokenizerNormalizer tokenizerNormalizer = Loader.getTokenizerNormalizer(stopwords);
		HashMap<String,String> uniqueTokens = new HashMap<String,String>();
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(fileCSV));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] columns = line.split(sep);
			String libNormal = columns[colLibNormal];
			if (tokenizerNormalizer.getNormalizerTerm().isStopWord(libNormal)) {
				continue;
			}
			String[] tokensArray = TokenizerNormalizer.tokenizeAlphaNum(libNormal);
			tokensArray = Loader.removeStopWords(stopwords, tokensArray);
			for (int i =0; i<(tokensArray.length-1);i++) {
				// as we use a Levenshtein distance, there is no point to concatenate a 1 character token
				// the Levenshtein distance will find a bigram with the first token alone
				// for example "vitamine K", "vitamine" will be matched "vitamine K" with concatenation and lucene distance
				// we want to avoid this situation :
				if (tokensArray[i+1].length() == 1) {
					continue;
				}
				String collapse = tokensArray[i] + tokensArray[i+1];
				String bigram = tokensArray[i] + " " + tokensArray[i+1];
				uniqueTokens.put(collapse, bigram);
			}
			for (String token : tokensArray) {
				uniqueTokens.put(token, token);
			}
		}
		br.close();
		return(uniqueTokens);
	}

	/**
	 * Remove stopwords in an array of tokens
	 * @param tokensArray an array of tokens (string) that may contain stopwords
	 * @param stopwords : a {@link Stopwords} instance
	 * @return an array of tokens without stopwords
	 */
	public static String[] removeStopWords(Stopwords stopwords, String[] tokensArray){
		String[] newTokensArray = new String[tokensArray.length];
		int numberOfStopwords = 0;
		for (int i = 0 ; i<tokensArray.length; i++) {
			String token = tokensArray[i];
			if (stopwords.isStopWord(token)) {
				numberOfStopwords = numberOfStopwords + 1;
				continue;
			} else {
				newTokensArray[i-numberOfStopwords] = token;
			}
		}
		newTokensArray = Arrays.copyOfRange(newTokensArray, 0, 
				tokensArray.length - numberOfStopwords);
		return(newTokensArray);
	}

	/**
	 * Get a tree datastructure of the terminology given a CSV file containing normalized labels
	 * @param fileCSV a CSV containing a column with normalized labels
	 * @param stopwords a file containing a list of stopword ; one by line
	 * @param sep the separator of the CSV file (ex : "\t")
	 * @param colLibNormal the ith column containing the libnormal (normalized label of the term)
	 * @param colCode the ith column containing the code (or uri) of the term
	 * @return A tree datastructure of the terminology
	 * @throws IOException File not found
	 */
	public static SetTokenTree loadTokenTree(File fileCSV, Stopwords stopwords, String sep, int colLibNormal, int colCode) throws IOException {
		//ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		//File file = new File(classLoader.getResource(fileName).getFile());
		SetTokenTree tokenTreeSet0 = new SetTokenTree();
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(fileCSV));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] columns = line.split(sep);
			String libNormal = columns[colLibNormal];
			if (stopwords.isStopWord(libNormal)) {
				continue;
			}
			String code = columns[colCode];
			String[] tokensArray = TokenizerNormalizer.tokenizeAlphaNum(libNormal);
			tokensArray = removeStopWords(stopwords, tokensArray);
			if (tokensArray.length == 0) {
				continue;
			}
			TokenTree tokenTree = new TokenTree(null,tokensArray, code);
			tokenTreeSet0.addTokenTree(tokenTree);
		}
		br.close();
		logger.info("tokenTreeSet0 size : " + tokenTreeSet0.getAvailableTokens().size());
		return(tokenTreeSet0);
	}

	/**
	 * Get a {@link TokenizerNormalizer} 
	 * @return The tokenizerNormalizer
	 */
	public static TokenizerNormalizer getTokenizerNormalizer(Stopwords stopwords){
		NormalizerTerm normalizerTerm = new NormalizerTerm(stopwords);
		TokenizerNormalizer tokenizerNormalizer = new TokenizerNormalizer(normalizerTerm);
		return(tokenizerNormalizer);
	}
}
