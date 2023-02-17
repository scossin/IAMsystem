package fr.erias.iamsystem_java.keywords;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Terminology implements IStoreKeywords, Iterable<IKeyword> {

	private Collection<IKeyword> keywords = new ArrayList<IKeyword>();

	public Terminology() {}
	;

	@Override
	public Collection<IKeyword> getKeywords() {
		return keywords;
	}

	@Override
	public void addKeyword(IKeyword keyword) {
		this.keywords.add(keyword);
	}

	/**
	 * Get the number of keywords.
	 * @return the size of the collection.
	 */
	public int size() {
		return this.keywords.size();
	}
	@Override
	public Iterator<IKeyword> iterator() {
		return this.keywords.iterator();
	}

}
