package fr.erias.iamsystem_java.matcher.strategy;

import java.util.List;

import fr.erias.iamsystem_java.fuzzy.base.ISynsProvider;
import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tree.INode;

public interface IMatchingStrategy
{
	/**
	 * Main internal function that implements iamsystem's algorithm.
	 * 
	 * @param tokens       a sequence of document's tokens.
	 * @param w            window, how many previous tokens can the algorithm look
	 *                     at.
	 * @param initialState a node/state in the trie, i.e. the root node.
	 * @param synsProvider a class that provides synonyms for each token.
	 * @param stopwords    an instance of {@link IStopwords} that checks if a token
	 *                     is a stopword.
	 * @return An ordered list of {@link IAnnotation}.
	 */
	public List<IAnnotation> detect(List<IToken> tokens, int w, INode initialState, ISynsProvider synsProvider,
			IStopwords stopwords);
}
