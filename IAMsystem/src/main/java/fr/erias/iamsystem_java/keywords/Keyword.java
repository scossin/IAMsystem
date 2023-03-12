package fr.erias.iamsystem_java.keywords;

/**
 * The simplest implementation of a {@link IKeyword}.
 *
 * @author Sebastien Cossin
 */
public class Keyword implements IKeyword
{

	private final String label;

	/**
	 * Constructor.
	 *
	 * @param label the label of this keyword.
	 */
	public Keyword(String label)
	{
		this.label = label;
	}

	@Override
	public String label()
	{
		return this.label;
	}

	@Override
	public String toString()
	{
		return this.label;
	}
}
