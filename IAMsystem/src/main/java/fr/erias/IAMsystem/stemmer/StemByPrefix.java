package fr.erias.IAMsystem.stemmer;

public class StemByPrefix implements IStemmer {

	private int nFirstChar;
	
	public StemByPrefix(int nFirstChar) {
		this.nFirstChar = nFirstChar;
	}
	
	@Override
	public String stem(String str) {
		if (str.length() <= nFirstChar) {
			return(str);
		} else {
			return(str.substring(0, nFirstChar));
		}
	}
	
	public static void main(String[] args){
		StemByPrefix stem = new StemByPrefix(5);
		System.out.println(stem.stem("gastroenterite"));
	}
}
