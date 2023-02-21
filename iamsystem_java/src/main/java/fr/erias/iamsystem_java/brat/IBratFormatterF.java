package fr.erias.iamsystem_java.brat;

import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.tokenize.IToken;

public interface IBratFormatterF
{

	public BratFormat getFormat(IAnnotation<? extends IToken> annot);
}
