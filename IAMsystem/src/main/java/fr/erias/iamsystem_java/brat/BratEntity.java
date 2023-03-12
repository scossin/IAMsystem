package fr.erias.iamsystem_java.brat;

public class BratEntity
{

	private final String entityId;
	private final String bratType;
	private final String offsets;
	private final String text;

	/**
	 * Create a Brat Entity.
	 *
	 * @param entityId a unique ID (^T[0-9]+$).
	 * @param bratType A Brat entity type (see Brat documentation).
	 * @param offsets  (start,end) offsets.
	 * @param text     document substring using (start,end) offsets.
	 */
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

	/**
	 * The Brat type of this entity.
	 *
	 * @return brat type.
	 */
	public String getBratType()
	{
		return bratType;
	}

	/**
	 * The unique id of this entity.
	 *
	 * @return a string id.
	 */
	public String getEntityId()
	{
		return entityId;
	}

	/**
	 * Brat offsets of the entity in the document.
	 *
	 * @return (start,end) offsets.
	 */
	public String getOffsets()
	{
		return offsets;
	}

	/**
	 * Brat text-span of this entity.
	 *
	 * @return a substring of the document.
	 */
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
