package fr.erias.IAMsystem.utils;

import java.util.HashSet;

import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;

public class Utils {

	public static HashSet<String> getUniqueToken(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer,
			IFilter filterToken) {
		IStopwords stopwords = tokenizerNormalizer.getNormalizer().getStopwords();
		HashSet<String> uniqueTokens = new HashSet<String> ();
		for (Term term : terminology.getTerms()) {
			String normalizeLabel = term.getNormalizedLabel();
			String[] tokensArray = tokenizerNormalizer.getTokenizer().tokenize(normalizeLabel);
			tokensArray = IStopwords.removeStopWords(stopwords, tokensArray);
			for (String token : tokensArray) {
				if (filterToken.isAtokenToIgnore(token)) {
					continue;
				}
				uniqueTokens.add(token);
			}
		}
		return(uniqueTokens);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
