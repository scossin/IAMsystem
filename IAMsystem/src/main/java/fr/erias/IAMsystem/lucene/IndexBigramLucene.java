package fr.erias.IAMsystem.lucene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;

/**
 * Create a Lucene Index to detect typos
 * 
 * @author Cossin Sebastien (cossin.sebastien@gmail.com)
 *
 */
public class IndexBigramLucene {

	final static Logger logger = LoggerFactory.getLogger(IndexBigramLucene.class);

	/**
	 * The name of the Lucene that contains the concatenated form (ex : "meningoencephalite")
	 */
	public static final String CONCATENATED_FIELD = "CONCATENATED_FIELD";

	/**
	 * bigramField The name of the Lucene field that contains the bigram form (ex : "meningo encephalite")
	 */
	public static final String BIGRAM_FIELD = "BIGRAM_FIELD";

	/**
	 * bigramField The name of the Lucene field that contains the bigram form (ex : "meningo encephalite")
	 */
	public static final String LUCENE_INDEX_FOLDER = "LUCENE_INDEX_FOLDER";


	/**
	 * Create a Lucene index to search similar strings with Levenshtein distance
	 * @param terminology A {@link Terminology} to index
	 * @param tokenizerNormalizer A {@link ITokenizerNormalizer} to normalize the terms
	 * @throws IOException if the Lucene index can't be created
	 */
	public static void IndexLuceneUniqueTokensBigram(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer) throws IOException {
		IndexLuceneUniqueTokensBigram(terminology, tokenizerNormalizer, new File(LUCENE_INDEX_FOLDER));
	}

	/**
	 * Create a Lucene index to search similar strings with Levenshtein distance
	 * @param terminology A {@link Terminology} to index
	 * @param tokenizerNormalizer A {@link ITokenizerNormalizer} to normalize the terms
	 * @param indexFolder Path to create Lucene Index
	 * @throws IOException if the Lucene index can't be created
	 */
	public static void IndexLuceneUniqueTokensBigram(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer, File indexFolder) throws IOException {
		HashMap<String,String> uniqueTokensBigram = getUniqueTokenBigram(terminology, tokenizerNormalizer);
		IndexLuceneUniqueTokensBigram(uniqueTokensBigram, indexFolder);
	}


	/**
	 * Get unique TokenBigram to index in Lucene in order to detect typo
	 * @param uniqueTokensBigram A map between the collapse form and the uncollapse form (ex "meningoencephalite, meningo encephalite"). Use {@link Loader}
	 * @param indexFolder Path to create Lucene Index
	 * @throws IOException If the index can't be created
	 */
	private static void IndexLuceneUniqueTokensBigram(HashMap<String,String> uniqueTokensBigram, File indexFolder) throws IOException {
		// Indexing in Lucene
		logger.info("Indexing Terminology...");
		Directory directory = FSDirectory.open(indexFolder.toPath());
		IndexWriterConfig config = new IndexWriterConfig();
		config.setOpenMode(OpenMode.CREATE);    //config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		IndexWriter iwriter = new IndexWriter(directory, config);
		int counter = 0;
		Iterator<Entry<String, String>> iter = uniqueTokensBigram.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			String collapse = entry.getKey();
			String bigram = entry.getValue();
			Document doc = new Document();
			IndexableField indexableField = new StringField (CONCATENATED_FIELD, collapse, Field.Store.YES);
			doc.add(indexableField);
			indexableField = new StringField (BIGRAM_FIELD, bigram, Field.Store.YES);
			doc.add(indexableField);
			iwriter.addDocument(doc); // write the document to the index
			counter ++ ;
		}
		logger.info("number of lines indexed : " + counter);
		iwriter.close();
		directory.close();
	}

	/**
	 * @param tokenizerNormalizer a {@link ITokenizerNormalizer} instance
	 * @param fileCSV a CSV file
	 * @param sep the separator of the CSV file (comma, tab...)
	 * @param colLibNormal the ith column of the CSV file corresponding to the normalize label to index
	 * @param tokenizerNormalizer {@link ITokenizerNormalizer} 
	 * @return A map between the collapse form and the uncollapse form (ex "meningoencephalite, meningo encephalite")
	 * @throws IOException Unfound File
	 */
	private static HashMap<String,String> getUniqueTokenBigram(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer) {
		IStopwords stopwords = tokenizerNormalizer.getNormalizer().getStopwords();
		HashMap<String,String> uniqueTokens = new HashMap<String,String>();
		for (Term term : terminology.getTerms()) {
			String normalizeLabel = term.getNormalizedLabel();
			String[] tokensArray = tokenizerNormalizer.getTokenizer().tokenize(normalizeLabel);
			tokensArray = IStopwords.removeStopWords(stopwords, tokensArray);
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
		return(uniqueTokens);
	}

	/**
	 * @deprecated
	 * The difference with {@link getUniqueTokenBigram is we ignore bigram concatenation here
	 * @param terminology
	 * @param tokenizerNormalizer
	 * @return
	 * @throws IOException
	 */
	private static HashMap<String,String> getUniqueToken2index(Terminology terminology, TokenizerNormalizer tokenizerNormalizer) throws IOException{
		IStopwords stopwords = tokenizerNormalizer.getNormalizer().getStopwords();
		HashMap<String,String> uniqueTokens = new HashMap<String,String>();
		for (Term term : terminology.getTerms()) {
			String normalizeLabel = term.getNormalizedLabel();
			String[] tokensArray = tokenizerNormalizer.getTokenizer().tokenize(normalizeLabel);
			tokensArray = IStopwords.removeStopWords(stopwords, tokensArray);
			for (String token : tokensArray) {
				if (token.length() < 5) { // we won't search for a typo index if the word is less than 5 characters
					continue;
				}
				uniqueTokens.put(token, token);
			}
		}
		return(uniqueTokens);
	}
}
