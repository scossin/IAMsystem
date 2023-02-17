package fr.erias.iamsystem_java.tokenize;

/**
 * List of normalizing functions.
 *
 * @author Sebastien Cossin
 */
public class NormFunctions {

  public static INormalizeF rmAccents = new RemoveAccents();
  public static INormalizeF lowerCase = (s) -> s.toLowerCase();
  public static INormalizeF lowerNoAccents =
      (s) -> NormFunctions.rmAccents.normalize(s.toLowerCase());
  public static INormalizeF noNormalization = (s) -> s;
}
