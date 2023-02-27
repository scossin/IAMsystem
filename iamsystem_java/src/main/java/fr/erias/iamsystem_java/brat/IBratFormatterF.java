package fr.erias.iamsystem_java.brat;

import fr.erias.iamsystem_java.matcher.IAnnotation;

@FunctionalInterface
public interface IBratFormatterF
{

	public BratFormat getFormat(IAnnotation annot);
}
