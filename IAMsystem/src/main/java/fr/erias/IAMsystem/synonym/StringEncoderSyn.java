package fr.erias.IAMsystem.synonym;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

import fr.erias.IAMsystem.stopwords.IStopwords;
import fr.erias.IAMsystem.terminology.Term;
import fr.erias.IAMsystem.terminology.Terminology;
import fr.erias.IAMsystem.tokenizernormalizer.ITokenizerNormalizer;

/**
 * 
 * Approximate String Algorithm with a {@link StringEncoder} <br>
 * 
 * See the list of StringEncoder you can add (Caverphone, DoubleMetaphone, Soundex...) <br>
 * https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/class-use/StringEncoder.html#org.apache.commons.codec.language
 * 
 * @author Sebastien Cossin
 *
 */
public class StringEncoderSyn implements ISynonym {

	private Map<String, Set<List<String>>> encoding2tokens = new HashMap<String, Set<List<String>>>();
	private final StringEncoder stringEncoder;
	private final int minTokenLength;
	private final String encodedStrSpliter;
	
	/**
	 * Encode all the unique tokens of your terminology for approximate string matching
	 * @param stringEncoder an method that implements {@link StringEncoder} <br>
	 * https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/class-use/StringEncoder.html#org.apache.commons.codec.language
	 * @param minTokenLength don't encode token less than this length (0 if you want to include them all)
	 */
	public StringEncoderSyn(StringEncoder stringEncoder, int minTokenLength) {
		this(stringEncoder, minTokenLength,null);
	}
	
	/**
	 * Encode all the unique tokens of your terminology for approximate string matching
	 * @param stringEncoder an method that implements {@link StringEncoder} <br>
	 * https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/class-use/StringEncoder.html#org.apache.commons.codec.language
	 * @param minTokenLength don't encode token less than this length (0 if you want to include them all)
	 * @param encodedStrSpliter if the algorithm outputs a concatenated string, you want to split it with this separator
	 */
	public StringEncoderSyn(StringEncoder stringEncoder, int minTokenLength, String encodedStrSpliter) {
		this.stringEncoder = stringEncoder;
		this.minTokenLength = minTokenLength;
		this.encodedStrSpliter = encodedStrSpliter;
	}
	
	/**
	 * Add all the tokens of a terminology
	 * For each term of the terminology, retrieve all unique tokens and stem them
	 * @param terminology a {@link Terminology}
	 * @param tokenizerNormalizer a {@link ITokenizerNormalizer}
	 * @throws EncoderException in the {@link StringEncoder} interface the encode method throws this exception.  
	 */
	public void addTerminology(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer) throws EncoderException {
		Set<String> tokens = getUniqueToken(terminology, tokenizerNormalizer);
		add(tokens);
	}
	
	/**
	 * Add a term (all its tokens) <br>
	 * Retrieve all unique tokens of this term and encode them
	 * @param term a {@link Term}
	 * @param tokenizerNormalizer a {@link ITokenizerNormalizer}
	 * @throws EncoderException in the {@link StringEncoder} interface the encode method throws this exception.  
	 */
	public void addTerm(Term term, ITokenizerNormalizer tokenizerNormalizer) throws EncoderException {
		Set<String> tokens = getUniqueToken(term, tokenizerNormalizer);
		add(tokens);
	}
	
	private void add(Set<String> tokens) throws EncoderException {
		for (String token : tokens) {
			String encoded = stringEncoder.encode(token);
			add(token, encoded);
		}
	}

	private void add(String token, String encoded) {
		List<String> tokenList = new ArrayList<String>(1);
		tokenList.add(token);
		for (String encodedString : encodedStrSplit(encoded)) {
			if (!encoding2tokens.containsKey(encodedString)) {
				Set<List<String>> temp = new HashSet<List<String>>();
				encoding2tokens.put(encodedString, temp);
			} 
			encoding2tokens.get(encodedString).add(tokenList);
		}
	}
	
	private Collection<String> encodedStrSplit(String encoded) {
		if (this.encodedStrSpliter == null) {
			List<String> encodedStrings = new ArrayList<String>(1);
			encodedStrings.add(encoded);
			return(encodedStrings);
		} else {
			return Arrays.asList(encoded.split(this.encodedStrSpliter));
		}
	}

	private Set<String> getUniqueToken(Terminology terminology, ITokenizerNormalizer tokenizerNormalizer) {
		Set<String> uniqueTokens = new HashSet<String> ();
		for (Term term : terminology.getTerms()) {
			uniqueTokens.addAll(getUniqueToken(term, tokenizerNormalizer));
		}
		return(uniqueTokens);
	}

	private Set<String> getUniqueToken(Term term, ITokenizerNormalizer tokenizerNormalizer) {
		IStopwords stopwords = tokenizerNormalizer.getNormalizer().getStopwords();
		Set<String> uniqueTokens = new HashSet<String> ();
		String normalizeLabel = term.getNormalizedLabel();
		String[] tokensArray = tokenizerNormalizer.getTokenizer().tokenize(normalizeLabel);
		tokensArray = IStopwords.removeStopWords(stopwords, tokensArray);
		for (String token : tokensArray) {
			if (tokenLengthLessThanMinSize(token)) {
				continue;
			}
			uniqueTokens.add(token);
		}
		return(uniqueTokens);
	}

	@Override
	public Set<List<String>> getSynonyms(String token) {
		if (tokenLengthLessThanMinSize(token)) return ISynonym.no_synonyms;
		
		try {
			String encoded = this.stringEncoder.encode(token);
			return (retrieveSynonyms(encoded));
		} catch (EncoderException e) {
			return(ISynonym.no_synonyms);
		}
	}
	
	private boolean tokenLengthLessThanMinSize(String token) {
		return token.length() < this.minTokenLength;
	}
	
	// we must handle the two cases: 
	// 1) the encoded string contains a single string
	// 2) the encoded string contains multiple strings
	private Set<List<String>> retrieveSynonyms(String encoded) {
		if (this.encodedStrSpliter == null) {
			return(justRetrieveSynonyms(encoded));
		} else {
			return(splitAndRetrieveSynonyms(encoded));
		}
	}
	
	private Set<List<String>> justRetrieveSynonyms(String encoded) {
		if (encoding2tokens.containsKey(encoded)) {
			return(encoding2tokens.get(encoded));
		} else {
			return(ISynonym.no_synonyms);
		}
	}
	
	private Set<List<String>> splitAndRetrieveSynonyms(String encoded2split) {
		Set<List<String>> tokens = new HashSet<List<String>>();
		List<String> encodedStrings = Arrays.asList(encoded2split.split(this.encodedStrSpliter));
		for (String encoded : encodedStrings) {
			tokens.addAll(justRetrieveSynonyms(encoded));
		}	
		return(tokens);
	} 
}
