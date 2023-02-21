package fr.erias.iamsystem_java.keywords;

/**
 * The simplest implementation of a {@link IEntity}.
 *
 * @author Sebastien Cossin
 */
public class Entity extends Keyword implements IEntity
{

	private final String kbid;

	/**
	 * Create an entity.
	 *
	 * @param label The label of the entity.
	 * @param kbid  The knowledge base id of this entity in a knowledge graph.
	 */
	public Entity(String label, String kbid)
	{
		super(label);
		this.kbid = kbid;
	}

	@Override
	public String kbid()
	{
		return this.kbid;
	}

	@Override
	public String toString()
	{
		return String.format("%s (%s)", this.label(), this.kbid);
	}
}
