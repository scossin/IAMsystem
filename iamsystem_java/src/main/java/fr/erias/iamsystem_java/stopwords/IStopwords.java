package fr.erias.iamsystem_java.stopwords;

import fr.erias.iamsystem_java.tokenize.IToken;

/**
 * Stopword interface.
 *
 * @author Sebastien Cossin
 * @param <T> a generic Token.
 */
public interface IStopwords<T extends IToken> {

  /**
   * Check if a token is a stopword.
   *
   * @param token a generic Token created by a tokenizer.
   * @return True if this token is a stopword.
   */
  public boolean isTokenAStopword(T token);
}
