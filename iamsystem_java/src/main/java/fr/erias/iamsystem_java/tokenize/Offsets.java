package fr.erias.iamsystem_java.tokenize;

/**
 * Store the start and end offsets of a token
 *
 * @author Sebastien Cossin
 */
public class Offsets implements IOffsets {

  private int start;
  private int end;

  /**
   * Create an offset.
   *
   * @param start start-offset is the index of the first character.
   * @param end end-offset is the index of the last character.
   */
  public Offsets(int start, int end) {
    this.start = start;
    this.end = end;
  }

  @Override
  public int start() {
    return this.start;
  }

  @Override
  public int end() {
    return this.end;
  }

  @Override
  public String toString() {
    return String.format("start=%d,end=%d", this.start, this.end);
  }
}
