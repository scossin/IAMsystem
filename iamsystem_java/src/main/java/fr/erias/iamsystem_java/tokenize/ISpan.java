package fr.erias.iamsystem_java.tokenize;

import java.util.List;

/**
 * Span interface: a class that stores a sequence of tokens.
 *
 * @author Sebastien Cossin
 * @param <T> A generic/custom token type.
 */
public interface ISpan<T extends IToken> {

  /**
   * Get the sequence of tokens.
   *
   * @return A sequence of {@link IToken} type.
   */
  public List<T> get_tokens();
}
