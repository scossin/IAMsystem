package fr.erias.iamsystem.brat;

import fr.erias.iamsystem.annotation.IAnnotation;

public interface INoteF
{

	/**
	 * Convert an annotation to a Brat Note.
	 *
	 * @param annot an Annotation.
	 * @return the string of the Brat note.
	 */
	public String getNote(IAnnotation annot);
}
