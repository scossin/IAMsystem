package fr.erias.iamsystem_java.tokenize;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link ITokenizer} implementation. Class responsible for the tokenization and normalization of
 * tokens.
 *
 * @author Sebastien Cossin
 */
public class TokenizerImp implements ITokenizer<IToken> {

  private final INormalizeF normalizeF;
  private final ISplitF splitF;

  public TokenizerImp(INormalizeF normalizeF, ISplitF splitF) {
    this.normalizeF = normalizeF;
    this.splitF = splitF;
  }

  public List<IToken> tokenize(String text) {
    return this.splitF.split(text).stream()
        .map(
            offset -> {
              int start = offset.start();
              int end = offset.end();
              String label = text.substring(start, end);
              String norm_label = this.normalizeF.normalize(label);
              return new Token(start, end, label, norm_label);
            })
        .collect(Collectors.toList());
  }
}
