package fr.erias.iamsystem_java.fuzzy.base;

public class NoWord2ignore implements IWord2ignore
{

	@Override
	public boolean isWord2ignore(String word)
	{
		return false;
	}

}
