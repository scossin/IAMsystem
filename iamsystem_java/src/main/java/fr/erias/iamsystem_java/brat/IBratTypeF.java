package fr.erias.iamsystem_java.brat;

import fr.erias.iamsystem_java.keywords.IKeyword;

public interface IBratTypeF<T extends IKeyword>
{

	public String getBratType(T keyword);
}
