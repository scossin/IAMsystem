package fr.erias.iamsystem_java.brat;

public class BratNote
{
	public static String TYPE = "IAMSYSTEM";
	private final String noteId;
	private final String refId;
	private final String note;

	public BratNote(String noteId, String refId, String note)
	{
		if (noteId.length() == 0 || noteId.charAt(0) != '#')
		{
			throw new IllegalArgumentException("noteId must start by #");
		}
		if (refId.length() == 0 || refId.charAt(0) != 'T')
		{
			throw new IllegalArgumentException("refId must start by T");
		}
		this.noteId = noteId;
		this.refId = refId;
		this.note = note;
	}

	public String getNodeId()
	{
		return noteId;
	}

	public String getNote()
	{
		return note;
	}

	public String getRefId()
	{
		return refId;
	}

	@Override
	public String toString()
	{
		return String.format("%s\t" + "%s " + "%s\t" + "%s", noteId, BratNote.TYPE, refId, note);
	}
}
