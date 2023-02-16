package fr.erias.iamsystem_java.tokenize;

/**
 * Default Token implementation.
 *
 * @author Sebastien Cossin
 */
public class Token implements IToken {

  private final int start;
  private final int end;
  private final String label;
  private final String norm_label;

  /**
   * Create a token.
   *
   * @param start start-offset is the index of the first character.
   * @param end end-offset is the index of the last character **+ 1**, that is to say the first
   *     character to exclude from the returned substring when slicing with [start:end]
   * @param label the label as it is in the document/keyword.
   * @param norm_label the normalized label (used by iamsystem's algorithm to perform entity
   *     linking).
   */
  public Token(int start, int end, String label, String norm_label) {
    this.start = start;
    this.end = end;
    this.label = label;
    this.norm_label = norm_label;
  }

  public int start() {
    return this.start;
  }

  public int end() {
    return this.end;
  }

  public String get_label() {
    return this.label;
  }

  public String get_norm_label() {
    return this.norm_label;
  }
}
