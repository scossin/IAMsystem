package fr.erias.iamsystem_java.stopwords;

import fr.erias.iamsystem_java.tokenize.IToken;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple Stopword implementation.
 *
 * @author Sebastien Cossin
 */
public class Stopwords<T extends IToken> extends SimpleStopwords<T> implements IStoreStopwords<T> {

  private Set<String> stopwords = new HashSet<String>();

  /**
   * Constructor with stopwords.
   *
   * @param stopwords a Collection of stopwords.
   */
  public Stopwords(Collection<String> stopwords) {
    this.add(stopwords);
  }

  public Stopwords() {}

  @Override
  public boolean isStopword(String word) {
    return word.isBlank() || stopwords.contains(word.toLowerCase());
  }

  @Override
  public void add(Collection<String> words) {
    words.forEach((w) -> this.add(w));
  }

  @Override
  public void add(String word) {
    this.stopwords.add(word.toLowerCase());
  }
}
