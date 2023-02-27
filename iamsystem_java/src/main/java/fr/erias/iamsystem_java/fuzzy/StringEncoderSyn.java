package fr.erias.iamsystem_java.fuzzy;

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
import fr.erias.iamsystem_java.fuzzy.base.NormLabelAlgo;
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
public class StringEncoderSyn extends NormLabelAlgo
{

	private Map<String, Set<String>> encoding2tokens = new HashMap<String, Set<String>>();
	private final StringEncoder stringEncoder;
	private final int minTokenLength;
	private String encodedStrSpliter = "\\|";

	public StringEncoderSyn(String name, StringEncoder stringEncoder, int minTokenLength)
	{
		super(name);
		this.stringEncoder = stringEncoder;
		this.minTokenLength = minTokenLength;
	}

	/**
	 * Encode all the unique tokens of your terminology for approximate string
	 * matching
	 *
	 * @param stringEncoder     an method that implements {@link StringEncoder} <br>
	 *                          https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/class-use/StringEncoder.html#org.apache.commons.codec.language
	 * @param minTokenLength    don't encode token less than this length (0 if you
	 *                          want to include them all)
	 * @param encodedStrSpliter if the algorithm outputs a concatenated string, you
	 *                          want to split it with this separator
	 */
	public StringEncoderSyn(StringEncoder stringEncoder, int minTokenLength)
	{
		this(stringEncoder.getClass().getSimpleName(), stringEncoder, minTokenLength);
	}

	public void add(Collection<String> tokens) throws EncoderException
	{
		for (String token : tokens)
		{
			String encoded = stringEncoder.encode(token);
			add(token, encoded);
		}
	}

	private void add(String token, String encoded)
	{
		for (String encodedString : encodedStrSplit(encoded))
		{
			if (!encoding2tokens.containsKey(encodedString))
			{
				Set<String> temp = new HashSet<String>();
				encoding2tokens.put(encodedString, temp);
			}
			encoding2tokens.get(encodedString).add(token);
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
			return Arrays.asList(encodedStr.split(this.encodedStrSpliter)).stream()
					.map(encoded -> this.findWordWithSimilarEncoding(encoded)).flatMap(List::stream)
					.collect(Collectors.toList());
		} catch (EncoderException e)
		{
			System.err.println(e.getMessage());
			return (FuzzyAlgo.NO_SYN);
		}
	}

	public void setEncodedStrSpliter(String encodedStrSpliter)
	{
		this.encodedStrSpliter = encodedStrSpliter;
	}

	private boolean tokenLengthLessThanMinSize(String token)
	{
		return token.length() < this.minTokenLength;
	}
}
