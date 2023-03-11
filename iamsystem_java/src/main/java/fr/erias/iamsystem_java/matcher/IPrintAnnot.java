package fr.erias.iamsystem_java.matcher;

import java.util.stream.Collectors;

public interface IPrintAnnot
{

	/**
	 * Utility function to retrieve keywords' label.
	 *
	 * @return
	 */
	public static String keywords2Str(IAnnotation annotation)
	{
		return annotation.getKeywords().stream().map(k -> k.toString()).collect(Collectors.joining(";"));
	}

	/**
	 * Return a string representation of an annotation.
	 * 
	 * @param annotation an {@link IAnnotation} produced by IAMsystem.
	 * @return a human readable string.
	 */
	public String toString(IAnnotation annotation);

}
