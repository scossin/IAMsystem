package fr.erias.IAMsystem.Romedi.config;


/**
 * Example of a configuration file for the project
 * @author Cossin Sebastien
 *
 */
public class ConfigRomedi {
	
	/**
	 * Dictionary file
	 */
	
	public static final String romediTerms = "romediTermsINPINBN.csv";
	
	/**
	 * See NormalizeLabelRomedi to produce this file : 
	 * normalized File
	 */
	public static final String romediTermsNormalized = "romediTermsNormalizedINPINBN.csv";
	
	
	/**
	 * The name of the stopwords file 
	 */
	public static final String STOPWORDS_FILE = "stopwordsRomedi.txt";
	
	
	
	
	
	
	/******************************** No need to change LUCENE and fieldnames : *****************************/
	
	/**
	 * The name of the Lucene index folder to perform fuzzy queries (Levenshtein distance)
	 */
	public static final String INDEX_FOLDER = "IndexUniqueTokens";
	
	/**
	 * The fieldname that contains bigram. ex : "meningo encephalite"
	 */
	public static final String BIGRAM_FIELD = "bigram";
	
	/**
	 * The fieldname that contains the concatenation of the bigram. ex : "meningoencephalite"
	 */
	
	public static final String CONCATENATION_FIELD = "collapse";
	
}
