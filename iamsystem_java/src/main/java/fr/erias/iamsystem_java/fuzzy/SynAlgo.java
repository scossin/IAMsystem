package fr.erias.iamsystem_java.fuzzy;

public class SynAlgo
{

	private final String syn;
	private final String algo;

	public SynAlgo(String syn, String algo)
	{
		this.syn = syn;
		this.algo = algo;
	}

	public String getAlgo()
	{
		return this.algo;
	}

	public String getSyn()
	{
		return syn;
	}
}
