package fr.erias.iamsystem_java.stopwords;

import fr.erias.iamsystem_java.tokenize.IToken;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Like a negative image (a total inversion, in which light areas appear dark and vice versa), every
 * token is a stopword until proven otherwise.
 *
 * @author Sebastien Cossin
 */
interface IWord2keepF {
  /**
   * Check if a word should be kept.
   *
   * @param word A word to check.
   * @return True if the word should be kept.
   */
  public boolean isAword2keep(String word);
}

public class NegativeStopwords implements IStopwords<IToken> {

  private Set<String> words2keep = new HashSet<String>();
  private List<IWord2keepF> funs = new ArrayList<IWord2keepF>();

  /**
   * Constructor with words2keep.
   *
   * @param words2keep a Collection of words to keep.
   */
  public NegativeStopwords(Collection<String> words2keep) {
    this.add(words2keep);
  }

  public NegativeStopwords() {}

  /**
   * Add words to keep.
   *
   * @param words A collection of words.
   */
  public void add(Collection<String> words) {
    words.forEach((w) -> this.words2keep.add(w));
  }

  /**
   * Add a function that checks if a word should be kept.
   *
   * @param word2keepFunction a function that check if a word should be kept.
   */
  public void add(IWord2keepF word2keepFunction) {
    this.funs.add(word2keepFunction);
  }

  @Override
  public boolean isTokenAStopword(IToken token) {
    return !this.isAword2keep(token.normLabel());
  }

  private boolean isAword2keep(String word) {
    return words2keep.contains(word) || funs.stream().anyMatch(f -> f.isAword2keep(word));
  }
}
