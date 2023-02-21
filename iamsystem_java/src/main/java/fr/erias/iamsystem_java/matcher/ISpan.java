package fr.erias.iamsystem_java.matcher;

import java.util.List;

import fr.erias.iamsystem_java.tokenize.IOffsets;
import fr.erias.iamsystem_java.tokenize.IToken;

public interface ISpan<T extends IToken> extends IOffsets
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
	public List<T> tokens();
}
