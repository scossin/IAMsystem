package fr.erias.IAMsystem.stemmer;

/**
 * Stem a string by taking the n first characters
 * Is used with the ISynonym Stems class
 * 
 * @author Cossin Sebastien
 *
 */
public class StemByPrefix implements IStemmer {

	private int prefixLength;
	
	public StemByPrefix(int prefixLength) {
		this.prefixLength = prefixLength;
	}
	
	@Override
	public String stem(String str) {
		if (str.length() <= prefixLength) {
			return(str);
		} else {
			return(str.substring(0, prefixLength));
		}
	}
}
