package fr.erias.iamsystem_java.fuzzy.encoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

import fr.erias.iamsystem_java.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.base.StringDistance;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;

/**
 *
 * Approximate String Algorithm with a {@link StringEncoder} <br>
 *
 * See the list of StringEncoder you can add (Caverphone, DoubleMetaphone,
 * Soundex...) <br>
 * https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/class-use/StringEncoder.html#org.apache.commons.codec.language
 *
 * @author Sebastien Cossin
 *
 */
public class StringEncoderSyn extends StringDistance
{

	private Map<String, Set<String>> encoding2tokens = new HashMap<String, Set<String>>();
	private final StringEncoder stringEncoder;
	private final int minNbChar;
	private String encodedStrSpliter = "\\|";

	/**
	 * Create a new fuzzy algorithm that uses a string encoder algorithm.
	 *
	 * @param name          the algorithm name.
	 * @param stringEncoder a {@link StringEncoder}.
	 * @param minNbChar     the minimum number of characters a word must have in
	 *                      order not to be ignored.
	 */
	public StringEncoderSyn(String name, StringEncoder stringEncoder, int minNbChar)
	{
		super(name, minNbChar);
		this.stringEncoder = stringEncoder;
		this.minNbChar = minNbChar;
	}

	/**
	 * Create a new fuzzy algorithm that uses a string encoder algorithm.
	 *
	 * @param stringEncoder an class that implements {@link StringEncoder} interface
	 *                      <br>
	 *                      https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/class-use/StringEncoder.html#org.apache.commons.codec.language
	 * @param minNbChar     don't encode token less than this length (0 if you want
	 *                      to include them all)
	 */
	public StringEncoderSyn(StringEncoder stringEncoder, int minNbChar)
	{
		this(stringEncoder.getClass().getSimpleName(), stringEncoder, minNbChar);
	}

	/**
	 * Add words to encode (in general unigrams of the dictionary).
	 *
	 * @param unigrams A collection of string.
	 * @throws EncoderException if the algorithm can't encode a word.
	 */
	public void add(Collection<String> unigrams) throws EncoderException
	{
		for (String word : unigrams)
		{
			String encoded = stringEncoder.encode(word);
			add(word, encoded);
		}
	}

	private void add(String word, String encoded)
	{
		for (String encodedString : encodedStrSplit(encoded))
		{
			if (!encoding2tokens.containsKey(encodedString))
			{
				Set<String> temp = new HashSet<String>();
				encoding2tokens.put(encodedString, temp);
			}
			encoding2tokens.get(encodedString).add(word);
		}
	}

	private Collection<String> encodedStrSplit(String encoded)
	{
		if (this.encodedStrSpliter == null)
		{
			List<String> encodedStrings = new ArrayList<String>(1);
			encodedStrings.add(encoded);
			return (encodedStrings);
		} else
		{
			return Arrays.asList(encoded.split(this.encodedStrSpliter));
		}
	}

	private List<SynAlgo> findWordWithSimilarEncoding(String encoded)
	{
		if (encoding2tokens.containsKey(encoded))
		{
			return (this.words2syn(encoding2tokens.get(encoded)));
		} else
		{
			return (FuzzyAlgo.NO_SYN);
		}
	}

	@Override
	public List<SynAlgo> getSynsOfWord(String token)
	{
		if (tokenLengthLessThanMinSize(token))
			return FuzzyAlgo.NO_SYN;
		// we must handle the two cases:
		// 1) the encoded string contains a single string
		// 2) the encoded string contains multiple strings.
		// encodedStr.split handles both cases
		try
		{
			String encodedStr = this.stringEncoder.encode(token);
			return Arrays.asList(encodedStr.split(this.encodedStrSpliter))
					.stream()
					.map(encoded -> this.findWordWithSimilarEncoding(encoded))
					.flatMap(List::stream)
					.collect(Collectors.toList());
		} catch (EncoderException e)
		{
			System.err.println(e.getMessage());
			return (FuzzyAlgo.NO_SYN);
		}
	}

	/**
	 * In case the string encoder returns multiple encoded string, the default
	 * separator is |.
	 *
	 * @param encodedStringSeparator a string separator.
	 */
	public void setEncodedStrSpliter(String encodedStringSeparator)
	{
		this.encodedStrSpliter = encodedStringSeparator;
	}

	private boolean tokenLengthLessThanMinSize(String token)
	{
		return token.length() < this.minNbChar;
	}
}
