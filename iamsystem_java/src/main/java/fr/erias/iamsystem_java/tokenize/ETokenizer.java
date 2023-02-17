package fr.erias.iamsystem_java.tokenize;

public enum ETokenizer {
  FRENCH {
    public TokenizerImp getInstance() {
      return new TokenizerImp(NormFunctions.lowerNoAccents, SplitFunctions.splitAlphaNum);
    }
  },
  ENGLISH {
    public TokenizerImp getInstance() {
      return new TokenizerImp(NormFunctions.lowerCase, SplitFunctions.splitAlphaNum);
    }
  };

  private ETokenizer() {}
  ;

  public abstract TokenizerImp getInstance();
}
