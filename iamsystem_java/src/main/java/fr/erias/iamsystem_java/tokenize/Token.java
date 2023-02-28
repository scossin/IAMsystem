package fr.erias.iamsystem_java.tokenize;

/**
 * Default Token implementation.
 *
 * @author Sebastien Cossin
 */
public class Token implements IToken
{

	private final int start;
	private final int end;
	private final String label;
	private final String norm_label;
	private final int i;

	/**
	 * Create a token.
	 *
	 * @param start      start-offset is the index of the first character.
	 * @param end        end-offset is the index of the last character **+ 1**, that
	 *                   is to say the first character to exclude from the returned
	 *                   substring when slicing with [start:end]
	 * @param label      the label as it is in the document/keyword.
	 * @param norm_label the normalized label (used by iamsystem's algorithm to
	 *                   perform entity linking).
	 */
	public Token(int start, int end, String label, String norm_label, int i)
	{
		this.start = start;
		this.end = end;
		this.label = label;
		this.norm_label = norm_label;
		this.i = i;
	}

	@Override
	public int compareTo(IToken o)
	{
		return this.i - o.i();
	}

	@Override
	public int end()
	{
		return this.end;
	}

	@Override
	public int i()
	{
		return i;
	}

	@Override
	public String label()
	{
		return this.label;
	}

	@Override
	public String normLabel()
	{
		return this.norm_label;
	}

	@Override
	public int start()
	{
		return this.start;
	}
	
	@Override
	public String toString()
	{
		return String.format("Token(label='%s', norm_label='%s', start=%d, end=%d, i=%d)", 
				label, norm_label, start, end, i);
	}
}
