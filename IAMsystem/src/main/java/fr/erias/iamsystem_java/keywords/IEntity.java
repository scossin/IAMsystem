package fr.erias.iamsystem_java.keywords;

/**
 * An interface to represent an entity in a knowledge base.
 *
 * @author Sebastien Cossin
 */
public interface IEntity extends IKeyword
{

	/**
	 * Get the entity id.
	 * 
	 * @return the knowledge base id of this entity.
	 */
	public String kbid();
}
