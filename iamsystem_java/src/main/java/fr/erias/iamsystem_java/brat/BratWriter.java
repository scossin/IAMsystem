package fr.erias.iamsystem_java.brat;

import fr.erias.iamsystem_java.keywords.IKeyword;

public class BratWriter
{

	public static void saveDocuentNotes(BratDocument<? extends IKeyword> document, IBratWriterF writer)
	{
		writer.write(document.toString());
	}

	public static void saveEntities(Iterable<BratEntity> entities, IBratWriterF writer)
	{
		for (BratEntity entity : entities)
		{
			writer.write(entity.toString());
			writer.write("\n");
		}
	}

	public static void saveNotes(Iterable<BratNote> notes, IBratWriterF writer)
	{
		for (BratNote note : notes)
		{
			writer.write(note.toString());
			writer.write("\n");
		}
	}
}
