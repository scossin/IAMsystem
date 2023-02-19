package fr.erias.iamsystem_java.tokenize;

import java.util.Arrays;
import java.util.List;

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
    List<IOffsets> offsets = this.splitF.split(text);
    IToken[] tokens = new Token[offsets.size()];
    for (int i = 0; i < offsets.size(); i++) {
      IOffsets offset = offsets.get(i);
      int start = offset.start();
      int end = offset.end();
      String label = text.substring(start, end);
      String norm_label = this.normalizeF.normalize(label);
      IToken token = new Token(start, end, label, norm_label, i);
      tokens[i] = token;
    }
    return Arrays.asList(tokens);
  }
}
