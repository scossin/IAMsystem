package fr.erias.IAMsystem.synonym;

import java.util.HashMap;
import java.util.HashSet;

import fr.erias.IAMsystem.stemmer.IStemmer;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;
import fr.erias.IAMsystem.utils.IFilterToken;
import fr.erias.IAMsystem.utils.Utils;


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
		this.stemTokensOfTerminology(terminology, tokenizerNormalizer, new NoFilter());
	}
	
	public Stems(IStemmer stemmer, Terminology terminology, ITokenizerNormalizer tokenizerNormalizer, IFilterToken filter) {
		this.stemmer = stemmer;
		this.stemTokensOfTerminology(terminology, tokenizerNormalizer, filter);
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

	private void stemTokensOfTerminology(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer, IFilterToken filter) {
		HashSet<String> tokens = Utils.getUniqueToken(terminology, tokenizerNormalizer,	filter);
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
}	

final class NoFilter implements IFilterToken {

	@Override
	public boolean isAtokenToIgnore(String token) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
