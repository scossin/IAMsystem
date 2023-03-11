package fr.erias.iamsystem_java.matcher;

import fr.erias.iamsystem_java.brat.BratFormat;
import fr.erias.iamsystem_java.brat.BratFormatters;
import fr.erias.iamsystem_java.brat.IBratFormatterF;

public class PrintAnnot implements IPrintAnnot
{

	private IBratFormatterF bratFormatter;

	public PrintAnnot()
	{
		this.bratFormatter = BratFormatters.contSeqFormatter;
	};

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
		return String.format("%s\t%s\t%s", format.getText(), format.getOffsets(), kwStr);
	}
}
