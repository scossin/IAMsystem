package fr.erias.iamsystem_java.brat;

import fr.erias.iamsystem_java.annotation.IAnnotation;

@FunctionalInterface
public interface IBratFormatterF
{
	/**
	 * Convert an iamsystem annotation to a Brat annotation (text-span and its
	 * offsets).
	 * 
	 * @param annot an {@link IAnnotation}.
	 * @return a BratFormat of the annotation.
	 */
	public BratFormat getFormat(IAnnotation annot);
}
