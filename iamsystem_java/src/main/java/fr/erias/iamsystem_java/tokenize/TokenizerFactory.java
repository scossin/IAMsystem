package fr.erias.iamsystem_java.tokenize;

enum ETokenizer {
  FRENCH {
    public TokenizerImp getInstance() {
      return new TokenizerImp(NormFunctions.lowerNoAccents, SplitFunctions.splitAlphaNum);
    }
  },
  ENGLISH {
    public TokenizerImp getInstance() {
      return new TokenizerImp(NormFunctions.lowerNoAccents, SplitFunctions.splitAlphaNum);
    }
  };

  private ETokenizer() {}
  ;

  public abstract TokenizerImp getInstance();
}

public class TokenizerFactory {

  public static ITokenizer<Token> getTokenizer(ETokenizer tokenizer) {
    return tokenizer.getInstance();
  }
}
