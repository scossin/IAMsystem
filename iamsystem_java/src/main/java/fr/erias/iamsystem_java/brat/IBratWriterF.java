package fr.erias.iamsystem_java.brat;

/**
 * Declare BratWriter function API.
 *
 * @author Sebastien Cossin
 *
 */
public interface IBratWriterF
{

	/**
	 * Write a line in the Brat ann format (in general to a *.ann file).
	 * 
	 * @param annLine a line of an *.ann file.
	 */
	public void write(String annLine);
}
