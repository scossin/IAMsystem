package fr.erias.iamsystem.brat;

import fr.erias.iamsystem.keywords.IKeyword;

/**
 * Utility class to serialize Brat botes and entities.
 *
 * @author Sebastien Cossin
 *
 */
public class BratWriter
{

	/**
	 * Serialize a Brat document.
	 *
	 * @param document A BratDocument containing iamsystem annotations converting to
	 *                 brat entities.
	 * @param writer   Any writer.
	 */
	public static void saveDocument(BratDocument<? extends IKeyword> document, IBratWriterF writer)
	{
		writer.write(document.toString());
	}

	/**
	 * Serialize Brat entities.
	 *
	 * @param entities Brat entities.
	 * @param writer   Any writer.
	 */
	public static void saveEntities(Iterable<BratEntity> entities, IBratWriterF writer)
	{
		for (BratEntity entity : entities)
		{
			writer.write(entity.toString());
			writer.write("\n");
		}
	}

	/**
	 * Serialize Brat notes.
	 *
	 * @param notes  Brat notes.
	 * @param writer Any writer.
	 */
	public static void saveNotes(Iterable<BratNote> notes, IBratWriterF writer)
	{
		for (BratNote note : notes)
		{
			writer.write(note.toString());
			writer.write("\n");
		}
	}
}
