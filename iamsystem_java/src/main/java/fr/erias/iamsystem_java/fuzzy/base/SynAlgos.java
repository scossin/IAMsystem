package fr.erias.iamsystem_java.fuzzy.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A class that merge {@link FuzzyAlgo} outputs.
 *
 * @author Sebastien Cossin
 *
 */
public class SynAlgos
{

	private final String syn;
	private final Set<String> algos;

	/**
	 * Constructor.
	 * 
	 * @param syn  a synonym.
	 * @param algo a fuzzy algorithm name.
	 */
	public SynAlgos(String syn, String algo)
	{
		this.syn = syn;
		this.algos = new HashSet<String>();
		this.algos.add(algo);
	}

	/**
	 * Add another algorithm name that generated the same synonym.
	 * 
	 * @param algo a fuzzy algorithm name.
	 */
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

	/**
	 * Retrieve all algorithms that generated this synonym.
	 * 
	 * @return algorithm names.
	 */
	public Collection<String> getAlgos()
	{
		return algos;
	}

	/**
	 * synonym getter.
	 * 
	 * @return the synonym.
	 */
	public String getSyn()
	{
		return syn;
	}

	/**
	 * In case the synonym is composed of multiple tokens (ex: 'ic' -> 'insuffisance
	 * cardiaque'), split the tokens.
	 * 
	 * @return the synonym in an array.
	 */
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
