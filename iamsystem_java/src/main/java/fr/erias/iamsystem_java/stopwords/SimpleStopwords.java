package fr.erias.iamsystem_java.stopwords;

import fr.erias.iamsystem_java.tokenize.IToken;

public abstract class SimpleStopwords implements ISimpleStopwords<IToken> {

  @Override
  public boolean isTokenAStopword(IToken token) {
    return isStopword(token.label());
  }

  @Override
  public abstract boolean isStopword(String word);
}
