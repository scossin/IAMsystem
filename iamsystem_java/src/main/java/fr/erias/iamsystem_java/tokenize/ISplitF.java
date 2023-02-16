package fr.erias.iamsystem_java.tokenize;

import java.util.List;

public interface ISplitF {

  /**
   * Split a string into (start,end) offsets.
   *
   * @param text A string to split.
   * @return An ordered sequence of offsets.
   */
  public List<IOffsets> split(String text);
}
