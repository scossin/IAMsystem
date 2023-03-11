package fr.erias.iamsystem_java.matcher;

import java.util.List;

import fr.erias.iamsystem_java.annotation.Annotation;
import fr.erias.iamsystem_java.annotation.IAnnotation;
import fr.erias.iamsystem_java.tokenize.IToken;

/**
 * Declare the API methods expected by a IAMsystem matcher.
 *
 * @author Sebastien Cossin
 *
 */
public interface IBaseMatcher
{
	/**
	 * Annotate a document passing its tokens produced by an external tokenizer.
	 *
	 * @param tokens Document's tokens to annotate with IAMsystem algorithm.
	 * @return A list of {@link Annotation}.
	 */
	public List<IAnnotation> annot(List<IToken> tokens);

	/**
	 * Annotate a document with the matcher's tokenizer.
	 *
	 * @param text A document to annotate with IAMsystem algorithm.
	 * @return A list of {@link Annotation}.
	 */
	public List<IAnnotation> annot(String text);
}
