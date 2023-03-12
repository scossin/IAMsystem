package fr.erias.iamsystem.brat;

public class BratNote
{
	public static String TYPE = "IAMSYSTEM";
	private final String noteId;
	private final String refId;
	private final String note;

	/**
	 * Class representing a Brat Note. https://brat.nlplab.org/standoff.html. Brat
	 * notes are used to store additionnal information on a detected entity. Format:
	 * #ID \t TYPE REFID \t NOTE
	 *
	 * @param noteId a unique ID (^#[0-9]+$).
	 * @param refId  a unique ID. For a BratEntity, the format is (^T[0-9]+$).
	 * @param note   any string comment.
	 */
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

	/**
	 * Retrieve note uniqeu id.
	 *
	 * @return a string id.
	 */
	public String getNodeId()
	{
		return noteId;
	}

	/**
	 * Retrieve the Brat note (text comment).
	 *
	 * @return a Brat note.
	 */
	public String getNote()
	{
		return note;
	}

	/**
	 * Get the id of the Brat annotation linked to this note.
	 *
	 * @return A unique string id.
	 */
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
