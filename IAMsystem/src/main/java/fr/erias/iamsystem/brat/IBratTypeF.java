package fr.erias.iamsystem.brat;

import fr.erias.iamsystem.keywords.IKeyword;
import fr.erias.iamsystem.keywords.Keyword;

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
