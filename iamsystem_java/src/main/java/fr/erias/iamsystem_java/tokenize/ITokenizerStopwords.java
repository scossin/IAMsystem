package fr.erias.iamsystem_java.tokenize;

import fr.erias.iamsystem_java.stopwords.IStopwords;

public interface ITokenizerStopwords<T extends IToken> extends ITokenizer<T>, IStopwords<T> {}
