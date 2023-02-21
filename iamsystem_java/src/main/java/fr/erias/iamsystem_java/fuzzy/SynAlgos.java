package fr.erias.iamsystem_java.fuzzy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SynAlgos
{

	private final String syn;
	private final Set<String> algos;

	public SynAlgos(String syn, String algo)
	{
		this.syn = syn;
		this.algos = new HashSet<String>();
		this.algos.add(algo);
	}

	public void addAlgo(String algo)
	{
		this.algos.add(algo);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}

		if (!(o instanceof SynAlgos))
		{
			return false;
		}

		SynAlgos c = (SynAlgos) o;
		return syn.equals(c.getSyn());
	}

	public Collection<String> getAlgos()
	{
		return algos;
	}

	public String getSyn()
	{
		return syn;
	}

	public String[] getSynToken()
	{
		return syn.split(" ");
	}

	@Override
	public int hashCode()
	{
		return syn.hashCode();
	}
}
