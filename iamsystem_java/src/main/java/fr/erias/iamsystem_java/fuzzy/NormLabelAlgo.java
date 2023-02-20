package fr.erias.iamsystem_java.fuzzy;

import fr.erias.iamsystem_java.tokenize.IToken;

public abstract class NormLabelAlgo<T extends IToken> extends ContextFreeAlgo<T> {

  public NormLabelAlgo(String name) {
    super(name);
  }

  @Override
  public String[] getSynonyms(T token) {
    return this.getSynsOfWord(token.normLabel());
  }

  public abstract String[] getSynsOfWord(String word);
}
