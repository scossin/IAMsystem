package fr.erias.iamsystem_java.brat;

/**
 * Utility class to generate Brat text-span and its offsets in the document.
 *
 * @author Sebastien Cossin
 *
 */
public class BratFormat
{

	private final String text;
	private final String offsets;

	/**
	 * Create a BratFormat.
	 * 
	 * @param text    Brat text-span
	 * @param offsets (start end) offsets in the document.
	 */
	public BratFormat(String text, String offsets)
	{
		this.text = text;
		this.offsets = offsets;
	}

	/**
	 * Get offsets.
	 * 
	 * @return (start end) offsets in the document.
	 */
	public String getOffsets()
	{
		return offsets;
	}

	/**
	 * Get Brat text-span.
	 * 
	 * @return a substring of the document.
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Get Brat text-span and escape newlines.
	 * 
	 * @return a substring of the document.
	 */
	public String getTextEscapeNewLine()
	{
		return text.replace("\n", "\\n");
	}

	@Override
	public String toString()
	{
		return String.format("%s\t%s", text, offsets);
	}
}
