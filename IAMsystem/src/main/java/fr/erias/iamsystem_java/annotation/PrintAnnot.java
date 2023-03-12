package fr.erias.iamsystem_java.annotation;

import fr.erias.iamsystem_java.brat.BratFormat;
import fr.erias.iamsystem_java.brat.BratFormatters;
import fr.erias.iamsystem_java.brat.IBratFormatterF;

/**
 * Default IPrintAnnot implementation.
 *
 * @author Sebastien Cossin
 *
 */
public class PrintAnnot implements IPrintAnnot
{

	private IBratFormatterF bratFormatter;

	/**
	 * Create an instance with the default BratFormatter.
	 */
	public PrintAnnot()
	{
		this.bratFormatter = BratFormatters.defaultFormatter;
	};

	/**
	 * Create an instance with a custom BratFormatter.
	 *
	 * @param bratFormatter A {@link IBratFormatterF}.
	 */
	public PrintAnnot(IBratFormatterF bratFormatter)
	{
		this.bratFormatter = bratFormatter;
	}

	@Override
	public String toString(IAnnotation annot)
	{
		IBratFormatterF bratFormatter = this.bratFormatter;
		if (annot.getText() == null)
		{
			bratFormatter = BratFormatters.tokenFormatter;
		}
		BratFormat format = bratFormatter.getFormat(annot);
		String kwStr = IPrintAnnot.keywords2Str(annot);
		return String.format("%s\t%s\t%s", format.getTextEscapeNewLine(), format.getOffsets(), kwStr);
	}
}
