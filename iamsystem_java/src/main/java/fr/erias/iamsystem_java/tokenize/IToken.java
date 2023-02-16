package fr.erias.iamsystem_java.tokenize;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Store the label, normalized label, start and end offsets of a token.
 *
 * @author Sebastien Cossin
 */
public interface IToken extends IOffsets {

  /**
   * The label as it is in the document/keyword.
   *
   * @return label string.
   */
  public String get_label();

  /**
   * The normalized label used by iamsystem's algorithm to perform entity linking.
   *
   * @return A normalized label string.
   */
  public String get_norm_label();

  /**
   * Concatenate the normalized label of each token in the sequence.
   *
   * @param tokens An ordered sequence of tokens.
   * @return Normalized labels separated by a space.
   */
  public static String ConcatNormLabel(Collection<? extends IToken> tokens) {
    return tokens.stream().map(t -> t.get_norm_label()).collect(Collectors.joining(" "));
  }

  /**
   * Concatenate the label of each token in the sequence.
   *
   * @param tokens An ordered sequence of tokens.
   * @return Labels separated by a space.
   */
  public static String ConcatLabel(Collection<? extends IToken> tokens) {
    return tokens.stream().map(t -> t.get_label()).collect(Collectors.joining(" "));
  }
}
