package fr.erias.iamsystem_java.keywords;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * An utility class to store a set of keywords.
 *
 * @author Sebastien Cossin
 *
 */
public class Terminology implements IStoreKeywords, Iterable<IKeyword>
{

	private Collection<IKeyword> keywords = new ArrayList<IKeyword>();

	@Override
	public void addKeyword(IKeyword keyword)
	{
		this.keywords.add(keyword);
	}

	@Override
	public Collection<IKeyword> getKeywords()
	{
		return keywords;
	}

	@Override
	public Iterator<IKeyword> iterator()
	{
		return this.keywords.iterator();
	}

	/**
	 * Get the number of keywords.
	 *
	 * @return the size of the collection.
	 */
	public int size()
	{
		return this.keywords.size();
	}
}
