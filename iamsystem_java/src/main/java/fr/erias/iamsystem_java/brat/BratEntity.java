package fr.erias.iamsystem_java.brat;

public class BratEntity
{

	private final String entityId;
	private final String bratType;
	private final String offsets;
	private final String text;

	public BratEntity(String entityId, String bratType, String offsets, String text)
	{
		if (entityId.length() == 0 || entityId.charAt(0) != 'T')
		{
			throw new IllegalArgumentException("entityId must start by T");
		}
		this.entityId = entityId;
		this.bratType = bratType;
		this.offsets = offsets;
		this.text = text;
	}

	public String getBratType()
	{
		return bratType;
	}

	public String getEntityId()
	{
		return entityId;
	}

	public String getOffsets()
	{
		return offsets;
	}

	public String getText()
	{
		return text;
	}

	@Override
	public String toString()
	{
		return String.format("%s\t" + "%s " + "%s\t" + "%s", entityId, bratType, offsets, text);
	}

}
