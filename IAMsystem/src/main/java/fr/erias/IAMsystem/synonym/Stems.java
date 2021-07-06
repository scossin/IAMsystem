package fr.erias.IAMsystem.synonym;

import java.util.HashMap;
import java.util.HashSet;

import fr.erias.IAMsystem.detect.DetectOutput;
import fr.erias.IAMsystem.detect.TermDetector;
import fr.erias.IAMsystem.stemmer.FrenchStemmer;
import fr.erias.IAMsystem.stemmer.IStemmer;
import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;

public class Stems implements ISynonym {

	private HashMap<String, HashSet<String[]>> mapStem2tokens = new HashMap<String, HashSet<String[]>>();

	private IStemmer stemmer;

	/**
	 * This class stores all stems of a terminology. Stems are extracted from all tokens of a terminology 
	 * @param stemmer
	 * @param terminology
	 * @param tokenizerNormalizer
	 */
	public Stems(IStemmer stemmer, Terminology terminology, ITokenizerNormalizer tokenizerNormalizer) {
		this.stemmer = stemmer;
		this.stemTokensOfTerminology(terminology, tokenizerNormalizer);
	}
	
	public IStemmer getStemmer(){
		return(this.stemmer);
	}
	
	@Override
	public HashSet<String[]> getSynonyms(String token){
		HashSet<String[]> arraySynonyms = new HashSet<String[]>();
		String stem = this.stemmer.stem(token);
		if (mapStem2tokens.containsKey(stem)) {
			arraySynonyms = mapStem2tokens.get(stem);
			return(arraySynonyms);
		}
		return(arraySynonyms);
	}
	
	private void stemTokensOfTerminology(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer) {
		HashSet<String> tokens = getUniqueToken(terminology, tokenizerNormalizer);
		for (String token : tokens) {
			String stem = stemmer.stem(token);
			addStem(token, stem);
		}
	}

	private void addStem(String token, String stem) {
		createEmptyHashSetIfStemDoesNotExist(stem);
		String[] tokenArray = new String[] {token};
		mapStem2tokens.get(stem).add(tokenArray);
	}

	private void createEmptyHashSetIfStemDoesNotExist(String stem) {
		if (!mapStem2tokens.containsKey(stem)) {
			HashSet<String[]> newEmptyHashSet = new HashSet<String[]>();
			mapStem2tokens.put(stem, newEmptyHashSet);
		}
	}

	private HashSet<String> getUniqueToken(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer) {
		IStopwords stopwords = tokenizerNormalizer.getNormalizer().getStopwords();
		HashSet<String> uniqueTokens = new HashSet<String> ();
		for (Term term : terminology.getTerms()) {
			String normalizeLabel = term.getNormalizedLabel();
			String[] tokensArray = tokenizerNormalizer.getTokenizer().tokenize(normalizeLabel);
			tokensArray = IStopwords.removeStopWords(stopwords, tokensArray);
			for (String token : tokensArray) {
				if (isAtoken2ignore(token)) {
					continue;
				}
				uniqueTokens.add(token);
			}
		}
		return(uniqueTokens);
	}
	
	private boolean isAtoken2ignore(String token) {
		return (tokenHasLessThan5characters(token));
	}
	
	private boolean tokenHasLessThan5characters (String token){
		return(token.length() < 5);
	}

	public static void main(String[] args) {
		TermDetector termDetector = new TermDetector();
		//
		String sentence = "le patient est diabétique";
		// add Levenshtein distance to detect it:
		Terminology terminology = new Terminology(); // can be loaded from a CSV file see: new Terminology(in, sep, colLabel, colCode) 
		terminology.addTerm("diabete", "E11", termDetector.getTokenizerNormalizer().getNormalizer());
		termDetector.addTerminology(terminology);

		DetectOutput detectOutput = termDetector.detect(sentence);
		System.out.println(detectOutput.toString());
		
		IStemmer stemmer = new FrenchStemmer();

		Stems stems = new Stems(stemmer, terminology, termDetector.getTokenizerNormalizer());
		termDetector.addSynonym(stems);
		detectOutput = termDetector.detect(sentence);
		System.out.println(detectOutput.toString());

		//		System.out.println(stems.stem("diabétique"));
		//		System.out.println(stems.stem("diabete"));
		//		for (String[] synonyms : stems.getSynonyms("diabetique")) {
		//			System.out.println(ITokenizer.arrayToString(synonyms, ";".charAt(0)));
		//		}

		sentence = "aspects scannographiques";
		System.out.println(stems.getStemmer().stem("scannographiques"));
	}
}
