package fr.erias.iamsystem_java.tokenize;

/**
 * A function that removes diacritics, normalize to unicode.
 *
 * @author Sebastien Cossin
 */
public class RemoveAccents implements INormalizeF {

  @Override
  public String normalize(String text) {
    // https://github.com/gcardone/junidecode
    String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
    String accentsgone = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    return accentsgone;
  }
}
