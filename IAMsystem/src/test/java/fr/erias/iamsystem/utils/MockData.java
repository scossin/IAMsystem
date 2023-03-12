package fr.erias.iamsystem.utils;

import java.util.Arrays;

import fr.erias.iamsystem.keywords.Entity;
import fr.erias.iamsystem.keywords.IEntity;
import fr.erias.iamsystem.keywords.IKeyword;
import fr.erias.iamsystem.keywords.Keyword;

public class MockData
{

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
