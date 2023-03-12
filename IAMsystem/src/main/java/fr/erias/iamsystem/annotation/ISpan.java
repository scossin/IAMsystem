package fr.erias.iamsystem.annotation;

import java.util.List;

import fr.erias.iamsystem.tokenize.IOffsets;
import fr.erias.iamsystem.tokenize.IToken;

/**
 * A class that represents a sequence of tokens in a document.
 *
 * @author Sebastien Cossin
 *
 */
public interface ISpan extends IOffsets
{

	/**
	 * The index of the last token within the parent document.
	 *
	 * @return an index.
	 */
	public int end_i();

	/**
	 * The index of the first token within the parent document.
	 *
	 * @return an index.
	 */
	public int start_i();

	/**
	 * The sequence of tokens of this span.
	 *
	 * @return A list of generic Token.
	 */
	public List<IToken> tokens();
}
