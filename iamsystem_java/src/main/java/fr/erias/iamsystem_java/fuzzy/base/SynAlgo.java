package fr.erias.iamsystem_java.fuzzy.base;

/**
 * Main fuzzy algorithms output.
 *
 * @author Sebastien Cossin
 *
 */
public class SynAlgo
{

	private final String syn;
	private final String algo;

	/**
	 * Create a synonym generated by a {@link FuzzyAlgo}.
	 *
	 * @param syn  a synonym.
	 * @param algo the fuzzy algorithm name.
	 */
	public SynAlgo(String syn, String algo)
	{
		this.syn = syn;
		this.algo = algo;
	}

	/**
	 * algorithm name getter.
	 *
	 * @return the fuzzy algorithm name.
	 */
	public String getAlgo()
	{
		return this.algo;
	}

	/**
	 * synonym getter.
	 *
	 * @return the synonym produced by the fuzzy algorithm.
	 */
	public String getSyn()
	{
		return syn;
	}

	@Override
	public String toString()
	{
		return String.format("SynAlgo=(syn='%s', algo='%s')", syn, algo);
	}
}
