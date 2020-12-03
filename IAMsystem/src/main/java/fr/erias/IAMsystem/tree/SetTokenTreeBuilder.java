package fr.erias.IAMsystem.tree;

import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;
import fr.erias.IAMsystem.tokenizernormalizer.TokenizerNormalizer;

public class SetTokenTreeBuilder {
	
	/**
	 * Add a terminology to a tree 
	 * @param terminology a representation of a {@link Terminology}
	 * @param tokenTreeSet0 a previous {@link SetTokenTree} or an empty one (new SetTokenTree())
	 * @param tokenizerNormalizer an instance to normalize and tokenize {@link TokenizerNormalizer}
	 * @return a {@link SetTokenTree} 
	 */
	public static SetTokenTree loadTokenTree(Terminology terminology, SetTokenTree tokenTreeSet0, ITokenizerNormalizer tokenizerNormalizer) {
		for (Term term : terminology.getTerms()) {
			addTerm(term, tokenTreeSet0, tokenizerNormalizer);
		}
		return(tokenTreeSet0);
	}
	
	/**
	 * Add a term to an existing tree
	 * @param term a {@link Term} to add to the terminology
	 * @param tokenTreeSet0 a previous {@link SetTokenTree} or an empty one (new SetTokenTree())
	 * @param tokenizerNormalizer an instance to normalize and tokenize {@link TokenizerNormalizer}
	 * @return a {@link SetTokenTree}
	 */
	public static SetTokenTree addTerm(Term term, SetTokenTree tokenTreeSet0, ITokenizerNormalizer tokenizerNormalizer) {
		IStopwords stopwords = tokenizerNormalizer.getNormalizer().getStopwords();
		String normalizedLabel = term.getNormalizedLabel();
		if (stopwords.isStopWord(normalizedLabel)) {
			return(tokenTreeSet0);
		}
		String code = term.getCode();
		String[] tokensArray = tokenizerNormalizer.getTokenizer().tokenize(normalizedLabel);
		tokensArray = IStopwords.removeStopWords(stopwords, tokensArray);
		if (tokensArray.length == 0) {
			return(tokenTreeSet0);
		}
		TokenTree tokenTree = new TokenTree(null,tokensArray, code);
		tokenTreeSet0.addTokenTree(tokenTree);
		return(tokenTreeSet0);
	}
	
	/**
	 * Create a tree data structure from a terminology 
	 * @param terminology a representation of a {@link Terminology}
	 * @param tokenizerNormalizer an instance to normalize and tokenize {@link TokenizerNormalizer}
	 * @return a {@link SetTokenTree}
	 */
	public static SetTokenTree loadTokenTree(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer) {
		SetTokenTree setTokenTree0 = new SetTokenTree();
		return(loadTokenTree(terminology, setTokenTree0, tokenizerNormalizer));
	}
}
