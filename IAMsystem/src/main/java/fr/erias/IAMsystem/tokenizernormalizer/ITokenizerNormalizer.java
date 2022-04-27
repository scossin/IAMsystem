package fr.erias.IAMsystem.tokenizernormalizer;

import fr.erias.IAMsystem.normalizer.INormalizer;
import fr.erias.IAMsystem.tokenizer.ITokenizer;

/**
 * Interface for the TokenizerNormalizer. 
 * There is not a unique way to implement TNoutput tokenizeNormalize :
 * The order (tokenization first, normalization after or the reverse) matters. 
 * For generality, this interface was created. 
 * 
 * @author Cossin Sebastien
 *
 */
public interface ITokenizerNormalizer extends ITokenizer, INormalizer {

	/**
	 * Normalize and tokenize a sentence 
	 * @param sentence a text to tokenize and normalize
	 * @return a {@link TNoutput}
	 */
	public TNoutput tokenizeNormalize(String sentence);
}
