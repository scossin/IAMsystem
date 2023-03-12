package fr.erias.iamsystem.keywords;

/**
 * Main Keyword API.
 *
 * @author Sebastien Cossin
 */
public interface IKeyword
{

	/**
	 * A string to search in a document (ex: "heart failure").
	 *
	 * @return The label of keyword.
	 */
	public String label();
}
