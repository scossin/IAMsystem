package fr.erias.iamsystem.annotation;

import java.util.Collection;
import java.util.List;

import fr.erias.iamsystem.keywords.IKeyword;
import fr.erias.iamsystem.stopwords.IStopwords;
import fr.erias.iamsystem.tokenize.IToken;
import fr.erias.iamsystem.tree.INode;

/**
 * Declare the attributes and methods expected by an Annotation.
 *
 * @author Sebastien Cossin
 *
 */
public interface IAnnotation extends ISpan, Comparable<IAnnotation>
{

	/**
	 * True if a is an ancestor of b.
	 *
	 * @param a Annotation a.
	 * @param b Annotation b.
	 * @return True if a is an ancestor of b.
	 */
	public static boolean isAncestorAnnotOf(IAnnotation a, IAnnotation b)
	{
		if (a == b)
			return false;
		if ((a.start() != b.start()) || (a.end() > b.end()))
			return false;
		Collection<INode> ancestors = b.lastState().getAncestors();
		return ancestors.contains(a.lastState());
	}

	/**
	 * For each token, the list of algorithms that matched.
	 *
	 * @return One to several algorithms per token.
	 */
	public List<Collection<String>> getAlgos();

	/**
	 * Keywords linked to this annotation.
	 *
	 * @return in general a single keyword. Multiple keywords if their tokenization
	 *         and normalization have the same output.
	 */
	public Collection<IKeyword> getKeywords();

	/**
	 * Get the text associated to this annotation.
	 *
	 * @return Text annotated
	 */
	public String getText();

	/**
	 * Retrieve the algorithm last state containing the keywords.
	 *
	 * @return a final state.
	 */
	public INode lastState();

	/**
	 * Set the annotated text.
	 * 
	 * @param text the text that was annotated.
	 */
	public void setText(String text);

	/**
	 * Stopwords tokens detected by the Matcher's {@link IStopwords} instance.
	 *
	 * @return a list of {@link IToken} tagged as stopwords.
	 */
	public List<IToken> stopTokens();
}
