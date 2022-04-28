package fr.erias.IAMsystem.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.normalizer.Normalizer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.stopwords.StopwordsImpl;
import fr.erias.IAMsystem.terminology.Terminology;

public class Ex3_loadFiles {

	public static IStopwords getStopwords() throws IOException {
		InputStream in = new FileInputStream(new File("./stopwordsRomedi.txt"));
		StopwordsImpl stopwords = new StopwordsImpl();
		stopwords.setStopWords(in, new Normalizer());
		in.close();
		return(stopwords);
	}

	// terminology loaded from a file:
	public static Terminology getTerminology(INormalizer normalizer) throws IOException {
		InputStream in = new FileInputStream(new File("./romediTermsINPINBN.csv")); // small terminology containing about 9400 terms
		String sep = "\t";
		int colLabel = 2;
		int colCode = 0;
		Terminology terminology = new Terminology(in, sep, colLabel, colCode, normalizer, false);
		return(terminology);
	}

	public static void main(String[] args) throws IOException {
		// Create a tokenizer with a normalizer 
		IStopwords stopwords = getStopwords();
		TermDetector termDetector = new TermDetector(stopwords);
		// NB: important to load the stopwords in the normalizer before loading the terminology
		// the normalizer will remove stopwords in the terminology

		INormalizer normalizer = termDetector.getTokenizerNormalizer();
		Terminology terminology = getTerminology(normalizer);
		termDetector.addTerminology(terminology);
		String document = "le patient prend de l'acide acetylsalicilique, du paracetamol codéiné et du kardegik";
		DetectOutput detectOutput = termDetector.detect(document);

//		1 terms detected.
//		term number 1:
//			 label in terminology: 'PARACETAMOL CODEINE'
//			 written exactly like this in the sentence: 'paracetamol codéiné'
//			 code in terminology: http://www.romedi.fr/romedi#BNr241jd2ks3q3869ijloh5d0d3cv8jrb3
//			 starting at position:50
//			 end at position:68
//			 first token number 8
//			 last token number 9
		System.out.println("------------------ Detection Example --------------------------");
		System.out.println(detectOutput.toString());

	}
}


