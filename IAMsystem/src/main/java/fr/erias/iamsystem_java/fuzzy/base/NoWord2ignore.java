package fr.erias.iamsystem_java.fuzzy.base;

/**
 * Utility class used when no word is ignored.
 *
 * @author Sebastien Cossin
 *
 */
public class NoWord2ignore implements IWord2ignore
{

	@Override
	public boolean isWord2ignore(String word)
	{
		return false;
	}

}
