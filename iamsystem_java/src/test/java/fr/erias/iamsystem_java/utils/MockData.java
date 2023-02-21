package fr.erias.iamsystem_java.utils;

import java.util.Arrays;

import fr.erias.iamsystem_java.keywords.Entity;
import fr.erias.iamsystem_java.keywords.IEntity;
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.stopwords.Stopwords;
import fr.erias.iamsystem_java.tokenize.AbstractTokNorm;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.TokStopImp;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;

public class MockData
{

	public static AbstractTokNorm<IToken> getFrenchTokStop()
	{
		ITokenizer<IToken> tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		IStopwords<IToken> stopwords = new Stopwords<IToken>();
		AbstractTokNorm<IToken> tokstop = new TokStopImp<IToken>(tokenizer, stopwords);
		return (tokstop);
	}

	public static Iterable<IEntity> getICG()
	{
		IEntity ent1 = new Entity("Insuffisance Cardiaque", "I50.9");
		IEntity ent2 = new Entity("Insuffisance Cardiaque Gauche", "I50.1");
		return Arrays.asList(ent1, ent2);
	}
}
