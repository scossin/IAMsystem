package fr.erias.IAMsystem.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import com.pengyifan.brat.BratDocument;
import fr.erias.IAMsystem.brat.BratDocumentWriter;
import fr.erias.IAMsystem.brat.CTbrat;
import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.detect.DetectDictionaryEntry;
import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.lucene.IndexBigramLucene;
import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.normalizer.Normalizer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.synonym.Abbreviations;
import fr.erias.IAMsystem.synonym.ISynonym;
import fr.erias.IAMsystem.synonym.LevenshteinTypoLucene;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystem.tree.SetTokenTreeBuilder;

/**
 * In this example we:
 * 1) load stopwords from a simple text file
 * 2) load a terminology from a CSV file
 * 3) index the terminology in a Lucene index for Levenshtein detection
 * 4) manually create abbreviations
 * 5) transform the terminology to a tree datastructure
 * 6) manually add a term to the tree
 * 7) show how the detection works and BratOutput 
 * 
 * @author Cossin Sebastien
 *
 */
public class Example2 {

	// abbreviations manually added
	public static Abbreviations getAbbreviations(TokenizerNormalizer tokenizerNormalizer) {
		Abbreviations abbreviations = new Abbreviations();
		// adding abbreviations :
		abbreviations.addAbbreviation("acide", "ac",tokenizerNormalizer);
		abbreviations.addAbbreviation("kardegic", "kdg",tokenizerNormalizer);
		abbreviations.addAbbreviation("accident vasculaire cérébral", "avc",tokenizerNormalizer);
		return(abbreviations);
	}

	// stopwords loaded from a file:
	public static IStopwords getStopwords() throws IOException {
		InputStream in = new FileInputStream(new File("./stopwordsRomedi.txt"));
		StopwordsImpl stopwords = new StopwordsImpl();
		stopwords.setStopWords(in, getNormalizer());
		in.close();
		return(stopwords);
	}

	public static INormalizer getNormalizer() {
		return(new Normalizer());
	}

	// terminology loaded from a file:
	public static Terminology getTerminology() throws IOException {
		InputStream in = new FileInputStream(new File("./romediTermsINPINBN.csv")); // small terminology containing about 9400 terms
		String sep = "\t";
		int colLabel = 2;
		int colCode = 0;
		Terminology terminology = new Terminology(in, sep, colLabel, colCode, getNormalizer());
		return(terminology);
	}

	public static void main(String[] args) throws IOException {
		// Create a tokenizer with a normalizer 
		IStopwords stopwords = getStopwords();
		TokenizerNormalizer tokenizerNormalizer = TokenizerNormalizer.getDefaultTokenizerNormalizer(stopwords);

		// create a Lucene Index:
		Terminology terminology = getTerminology(); // load the terminology
		IndexBigramLucene.IndexLuceneUniqueTokensBigram(terminology, tokenizerNormalizer); // create the index ; do it only once

		// create Synonyms with abbreviations and Lucene:
		HashSet<ISynonym> synonyms = new HashSet<ISynonym>();
		LevenshteinTypoLucene levenshteinTypoLucene = new LevenshteinTypoLucene(); // open the index
		synonyms.add(levenshteinTypoLucene);
		synonyms.add(getAbbreviations(tokenizerNormalizer));

		// transform the terminology to a tree datastructure
		SetTokenTree tokenTreeSet0 = SetTokenTreeBuilder.loadTokenTree(terminology,tokenizerNormalizer);

		// add another term to the terminology
		Term term = new Term("accident vasculaire cérébral","I64",tokenizerNormalizer.getNormalizer());
		SetTokenTreeBuilder.addTerm(term, tokenTreeSet0, tokenizerNormalizer);

		// create a dectector: 
		DetectDictionaryEntry detectDictionaryEntry = new DetectDictionaryEntry(tokenTreeSet0,tokenizerNormalizer,synonyms);

		// detect terms in the sentence
		String sentence = "le patient prend de l'ac acetylsalicilique, du paracetamol codéiné et du kardegik (KDG) pour un avc";

		// detectOutput:
		DetectOutput detectOutput = detectDictionaryEntry.detectCandidateTerm(sentence);


		System.out.println("------------------ Detection Example --------------------------");
		System.out.println(detectOutput.toString());

		// kardegic -> kardegik with Levenshtein
		// KDG -> kardegic with abbreviations
		// ac -> acide with abbreviations
		// avc -> accident vasculaire cerebral with abbreviations

		System.out.println("------------------ End --------------------------");

		// Brat output:

		System.out.println("------------------ Brat Output Example --------------------------");
		String bratType = "drug";
		// output to the console
		BratDocumentWriter bratDocumentWriter = new BratDocumentWriter(new PrintWriter(System.out));
		BratDocument doc = new BratDocument();
		for (CTcode codes : detectOutput.getCTcodes()) {
			CTbrat ctbrat = new CTbrat(codes, bratType);
			doc.addAnnotation(ctbrat.getBratEntity());
		}
		bratDocumentWriter.write(doc);
		bratDocumentWriter.close();
		System.out.println("------------------ End --------------------------");
	}
}


