package fr.erias.IAMsystem.tokenizernormalizer;

import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.tokenizer.ITokenizer;

/**
 * Interface for the TokenizerNormalizer class
 * @author Cossin Sebastien
 *
 */
public interface ITokenizerNormalizer {

	/**
	 * Normalize and tokenize a sentence 
	 * @param sentence a text to tokenize and normalize
	 * @return a {@link TNoutput}
	 */
	public TNoutput tokenizeNormalize(String sentence);

	/**
	 * Retrieve the normalizer
	 * @return a {@link INormalizer}
	 */
	public INormalizer getNormalizer();

	/**
	 * Retrieve the tokenizer 
	 * @return a {@link ITokenizer}
	 */
	public ITokenizer getTokenizer();

	/**
	 * Set the tokenizer 
	 * @param tokenizer a {@link ITokenizer}
	 */
	public void setTokenizer(ITokenizer tokenizer);

	/**
	 * Set the normalizer a {@link INormalizer}
	 * @param normalizer a {@link INormalizer}
	 */
	public void setNormalizer(INormalizer normalizer);

}
