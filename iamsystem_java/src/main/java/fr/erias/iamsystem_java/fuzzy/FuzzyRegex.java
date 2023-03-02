package fr.erias.iamsystem_java.fuzzy;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.erias.iamsystem_java.fuzzy.base.ContextFreeAlgo;
import fr.erias.iamsystem_java.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.tokenize.IToken;

/**
 * A to handle regular expressions.
 * Useful when one or multiple tokens of a keyword need to be matched to a regular expression.
 * 
 * @author Sebastien Cossin
 *
 */
public class FuzzyRegex extends ContextFreeAlgo
{

	private final Pattern patternCompiled;
	private final List<SynAlgo> syn;

	/**
	 * Create a fuzzy regex instance.
	 * @param name  a name given to this algorithm.
	 * @param pattern  a regular expression.
	 * @param patternName a name given to this pattern (ex: 'numval').
	 */
	public FuzzyRegex(String name, String pattern, String patternName)
	{
		super(name);
		this.patternCompiled = Pattern.compile(pattern);
		this.syn = this.word2syn(patternName);
	}

	@Override
	public List<SynAlgo> getSynonyms(IToken token)
	{
		Matcher matcher = patternCompiled.matcher(token.label());
		if (!matcher.find())
			return FuzzyAlgo.NO_SYN;
		return syn;
	}

	/**
	 * Test a token label matches the regular expression.
	 * @param token a document's token.
	 * @return True if this token matches this instance's pattern.
	 */
	public boolean tokenMatchesPattern(IToken token)
	{
		return wordMatchesPattern(token.label());
	}

	/**
	 * Test if a word matches the regular expression.
	 * @param label matches the regular expression.
	 * @return True if this word matches this instance's pattern.
	 */
	public boolean wordMatchesPattern(String label)
	{
		return patternCompiled.matcher(label).find();
	}
}
