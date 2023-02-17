package fr.erias.iamsystem_java.keywords;

import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface IStoreKeywords {

  /**
   * Get the stored keywords.
   *
   * @return A collection of {@link IKeyword}.
   */
  public Collection<IKeyword> getKeywords();

  /**
   * Add a keyword to the store.
   *
   * @param keyword a {@link IKeyword}.
   */
  public void addKeyword(IKeyword keyword);

  /**
   * Get all the unigrams (single words excluding stopwords) in the keywords.
   *
   * @param keywords a collection of {@link IKeyword}
   * @param tokenizer a {@link ITokenizer}
   * @param stopwords a {@link IStopwords}
   * @return
   */
  public static Set<String> getUnigrams(
      Iterable<IKeyword> keywords,
      ITokenizer<? extends IToken> tokenizer,
      IStopwords<IToken> stopwords) {
    Set<String> unigrams = new HashSet<String>();
    for (IKeyword kw : keywords) {
      List<? extends IToken> tokens = tokenizer.tokenize(kw.label());
      tokens.stream()
          .filter(t -> !stopwords.isTokenAStopword(t))
          .forEach(t -> unigrams.add(t.normLabel()));
    }
    return unigrams;
  }
}
