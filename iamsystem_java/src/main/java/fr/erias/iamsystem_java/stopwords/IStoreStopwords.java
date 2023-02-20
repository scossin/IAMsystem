package fr.erias.iamsystem_java.stopwords;

import fr.erias.iamsystem_java.tokenize.IToken;
import java.util.Collection;

public interface IStoreStopwords<T extends IToken> extends IStopwords<T> {

  /**
   * Add stopwords.
   *
   * @param words A collection of stopwords.
   */
  public void add(Collection<String> words);

  /**
   * Add stopword.
   *
   * @param words A stopword
   */
  public void add(String word);
}
