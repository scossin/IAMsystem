package fr.erias.iamsystem_java.matcher;

import fr.erias.iamsystem_java.tokenize.IToken;
import java.util.List;

public interface ISpan<T extends IToken> {

  /**
   * The index of the first token within the parent document.
   *
   * @return an index.
   */
  public int start_i();

  /**
   * The index of the last token within the parent document.
   *
   * @return an index.
   */
  public int end_i();

  /**
   * The sequence of tokens of this span.
   *
   * @return A list of generic Token.
   */
  public List<T> tokens();
}
