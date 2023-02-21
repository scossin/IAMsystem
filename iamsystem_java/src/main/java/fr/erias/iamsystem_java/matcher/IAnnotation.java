package fr.erias.iamsystem_java.matcher;

import java.util.Collection;
import java.util.List;

import fr.erias.iamsystem_java.brat.IBratFormatterF;
import fr.erias.iamsystem_java.keywords.IKeyword;
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tree.INode;

public interface IAnnotation<T extends IToken> extends ISpan<T>, Comparable<IAnnotation<T>>
{

	/**
	 * True if a is an ancestor of b.
	 * 
	 * @param a Annotation a.
	 * @param b Annotation b.
	 * @return True if a is an ancestor of b.
	 */
	public static boolean isAncestorAnnotOf(IAnnotation<? extends IToken> a, IAnnotation<? extends IToken> b)
	{
		if (a == b)
			return false;
		if ((a.start() != b.start()) || (a.end() > b.end()))
			return false;
		Collection<INode> ancestors = b.lastState().getAncestors();
		return ancestors.contains(a.lastState());
	}

	/**
	 * A Brat annotation formatter.
	 *
	 * @return the formatter.
	 */
	public IBratFormatterF formatter();

	/**
	 * For each token, the list of algorithms that matched.
	 *
	 * @return One to several algorithms per token.
	 */
	public List<Collection<String>> getAlgos();

	/**
	 * Keywords linked to this annotation
	 *
	 * @return
	 */
	public Collection<IKeyword> getKeywords();

	/**
	 * Retrieve the algorithm last state containing the keywords.
	 * 
	 * @return a final state.
	 */
	public INode lastState();

	/**
	 * The list of stopwords tokens inside the annotation detected by the Matcher's
	 * {@link IStopwords} instance.
	 *
	 * @return
	 */
	public List<T> stopTokens();
}
