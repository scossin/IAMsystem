package fr.erias.iamsystem_java.stopwords;

public interface IWord2keepF
{
	/**
	 * Check if a word should be kept.
	 *
	 * @param word A word to check.
	 * @return True if the word should be kept.
	 */
	public boolean isAword2keep(String word);
}
