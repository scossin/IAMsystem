package fr.erias.IAMsystem.tree;

import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;

public class SetTokenTreeBuilder {
	
	/**
	 * Create a tree data structure from a terminology 
	 * @param terminology a representation of a {@link Terminology}
	 * @param tokenTreeSet0 a previous {@link SetTokenTree} or an empty one (new SetTokenTree())
	 * @param tokenizerNormalizer an instance to normalize and tokenize {@link TokenizerNormalizer}
	 * @return
	 */
	public static SetTokenTree loadTokenTree(Terminology terminology, SetTokenTree tokenTreeSet0, ITokenizerNormalizer tokenizerNormalizer) {
		IStopwords stopwords = tokenizerNormalizer.getNormalizer().getStopwords();
		for (Term term : terminology.getTerms()) {
			String normalizedLabel = term.getNormalizedLabel();
			if (stopwords.isStopWord(normalizedLabel)) {
				continue;
			}
			String code = term.getCode();
			String[] tokensArray = tokenizerNormalizer.getTokenizer().tokenize(normalizedLabel);
			tokensArray = IStopwords.removeStopWords(stopwords, tokensArray);
			if (tokensArray.length == 0) {
				continue;
			}
			TokenTree tokenTree = new TokenTree(null,tokensArray, code);
			tokenTreeSet0.addTokenTree(tokenTree);
		}
		return(tokenTreeSet0);
	}
	
	/**
	 * Create a tree data structure from a terminology 
	 * @param terminology a representation of a {@link Terminology}
	 * @param tokenizerNormalizer an instance to normalize and tokenize {@link TokenizerNormalizer}
	 * @return
	 */
	public static SetTokenTree loadTokenTree(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer) {
		SetTokenTree setTokenTree0 = new SetTokenTree();
		return(loadTokenTree(terminology, setTokenTree0, tokenizerNormalizer));
	}
}
