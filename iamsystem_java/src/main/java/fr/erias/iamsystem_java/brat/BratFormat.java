package fr.erias.iamsystem_java.brat;

public class BratFormat
{

	private final String text;
	private final String offsets;

	public BratFormat(String text, String offsets)
	{
		this.text = text;
		this.offsets = offsets;
	}

	public String getOffsets()
	{
		return offsets;
	}

	public String getText()
	{
		return text;
	}

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
