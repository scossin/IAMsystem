package fr.erias.iamsystem_java.brat;

import fr.erias.iamsystem_java.keywords.IKeyword;
import fr.erias.iamsystem_java.keywords.Keyword;

public interface IBratTypeF<T extends IKeyword>
{

	/**
	 * Retrieve the brat type in a Keyword.
	 *
	 * @param keyword a {@link Keyword}
	 * @return the Brat type.
	 */
	public String getBratType(T keyword);
}
