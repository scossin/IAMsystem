package fr.erias.iamsystem_java.fuzzy.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SimpleWords2ignore implements IWord2ignore
{

	private final Set<String> words2ignore = new HashSet<String>();

	public SimpleWords2ignore()
	{
	}

	public SimpleWords2ignore(Collection<String> words2ignore)
	{
		this.addWords2ignore(words2ignore);
	}

	public void addWord2ignore(String word2ignore)
	{
		this.words2ignore.add(word2ignore);
	}

	public void addWords2ignore(Collection<String> words2ignore)
	{
		this.words2ignore.addAll(words2ignore);
	}

	@Override
	public boolean isWord2ignore(String word)
	{
		return this.words2ignore.contains(word);
	}
}
