package fr.erias.iamsystem.tokenize;

/**
 * The list of normalizing functions used in this package.
 *
 * @author Sebastien Cossin
 */
public class NormFunctions
{

	public static INormalizeF rmAccents = (s) ->
	{
		// https://github.com/gcardone/junidecode
		String normalized = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
		String accentsgone = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		return accentsgone;
	};
	public static INormalizeF lowerCase = (s) -> s.toLowerCase();
	public static INormalizeF lowerNoAccents = (s) -> NormFunctions.rmAccents.normalize(s.toLowerCase());
	public static INormalizeF normFrench = (s) -> NormFunctions.lowerNoAccents.normalize(s)
			.replace("œ", "oe") // cœur -> coeur
			.replace("æ", "ae") // nævus -> naevus
			.replace("ø", "o") // maladie de Følling -> maladie de Folling
			.replace("μ", "u") // μg -> ug
	;
	public static INormalizeF noNormalization = (s) -> s;
}
