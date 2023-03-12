package fr.erias.iamsystem_java.brat;

import fr.erias.iamsystem_java.annotation.IAnnotation;

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
