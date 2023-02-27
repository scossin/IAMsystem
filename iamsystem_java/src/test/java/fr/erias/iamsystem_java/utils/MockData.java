package fr.erias.iamsystem_java.utils;

import java.util.Arrays;

import fr.erias.iamsystem_java.keywords.Entity;
import fr.erias.iamsystem_java.keywords.IEntity;
import fr.erias.iamsystem_java.keywords.IKeyword;
import fr.erias.iamsystem_java.keywords.Keyword;
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.stopwords.Stopwords;
import fr.erias.iamsystem_java.tokenize.AbstractTokNorm;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.TokStopImp;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;

public class MockData
{

	public static AbstractTokNorm getFrenchTokStop()
	{
		ITokenizer tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		IStopwords stopwords = new Stopwords();
		AbstractTokNorm tokstop = new TokStopImp(tokenizer, stopwords);
		return (tokstop);
	}

	public static Iterable<IEntity> getICG()
	{
		IEntity ent1 = new Entity("Insuffisance Cardiaque", "I50.9");
		IEntity ent2 = new Entity("Insuffisance Cardiaque Gauche", "I50.1");
		return Arrays.asList(ent1, ent2);
	}

	public static Iterable<IKeyword> getNorthSouthAmer()
	{
		IKeyword kw1 = new Keyword("north AMERICA");
		IKeyword kw2 = new Keyword("south AMERICA");
		return Arrays.asList(kw1, kw2);
	}
}
