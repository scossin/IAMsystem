package fr.erias.iamsystem_java.matcher;

import fr.erias.iamsystem_java.brat.IBratFormatterF;
import fr.erias.iamsystem_java.keywords.IKeyword;
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.tokenize.IToken;
import java.util.Collection;
import java.util.List;

public interface IAnnotation<T extends IToken> extends ISpan<T> {

  /**
   * For each token, the list of algorithms that matched.
   *
   * @return One to several algorithms per token.
   */
  public List<Collection<String>> getAlgos();

  /**
   * The list of stopwords tokens inside the annotation detected by the Matcher's {@link IStopwords}
   * instance.
   *
   * @return
   */
  public List<T> stopTokens();

  /**
   * Keywords linked to this annotation
   *
   * @return
   */
  public Collection<IKeyword> getKeywords();

  /**
   * A Brat annotation formatter.
   *
   * @return the formatter.
   */
  public IBratFormatterF formatter();
}
