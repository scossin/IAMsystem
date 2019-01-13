package fr.erias.IAMsystem.Romedi.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;

import com.pengyifan.brat.BratDocument;

import fr.erias.IAMsystem.brat.BratDocumentWriter;
import fr.erias.IAMsystem.brat.CTbrat;
import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.exceptions.InvalidArraysLength;
import fr.erias.IAMsystem.exceptions.ProcessSentenceException;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;


/**
 * 1) Execute NormalizedLabelsRomedi to normalize the labels of the terminology
 * 2) Execute IndexBigramLuceneRomedi to create a Lucene Index to detect typos with Levenshtein distance
 * 3) Execute this example
 * 
 * @author Cossin Sebastien
 *
 */
public class Example {

	public static void drugDetectionExample(String sentence)  throws IOException, UnfoundTokenInSentence, ParseException {
		System.out.println("------------------ DrugDetection Example --------------------------");
		DetectDrug detectDrug = new DetectDrug();
		Set<CTcode> results = detectDrug.getCTcodes(sentence);
		for (CTcode codes : results) {
			System.out.println("--------- New Entry detected --------- : \t " + codes.getCandidateTermString());
			System.out.println("\t start position : " + codes.getStartPosition());
			System.out.println("\t end position : " + codes.getEndPosition());
			System.out.println("\t code : " + codes.getCode());
		}
		System.out.println("------------------ End --------------------------");
	}
	
	public static void bratOutputExample(String sentence)  throws IOException, UnfoundTokenInSentence, ParseException {
		System.out.println("------------------ Brat Output Example --------------------------");
		DetectDrug detectDrug = new DetectDrug();
		Set<CTcode> results = detectDrug.getCTcodes(sentence);
		String bratType = "drug";
		
		// output to the console
		BratDocumentWriter bratDocumentWriter = new BratDocumentWriter(new PrintWriter(System.out));
		BratDocument doc = new BratDocument();
		for (CTcode codes : results) {
			CTbrat ctbrat = new CTbrat(codes, bratType);
			doc.addAnnotation(ctbrat.getBratEntity());
		}
		bratDocumentWriter.write(doc);
		bratDocumentWriter.close();
		System.out.println("------------------ End --------------------------");
	}
	
	// example
	public static void main(String[] args) throws IOException, UnfoundTokenInSentence, ParseException, ProcessSentenceException, InvalidArraysLength {
		// ac and KDG are detected with abbreviations
		// acetylsalicilique and kardegik contain typos
		
		String sentence = "le patient prend de l'ac acetylsalicilique, du paracetamol codéiné et du kardegik (KDG)";
		drugDetectionExample(sentence);
		
		bratOutputExample(sentence);
	}
}
