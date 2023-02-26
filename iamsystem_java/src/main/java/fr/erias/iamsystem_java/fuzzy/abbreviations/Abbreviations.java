package fr.erias.iamsystem_java.fuzzy.abbreviations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.fuzzy.base.ContextFreeAlgo;
import fr.erias.iamsystem_java.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;

public class Abbreviations<T extends IToken> extends ContextFreeAlgo<T>
{

	private TokenIsAnAbbreviation<T> abbsChecker;
	private Map<String, Set<String>> short2longForms;

	public Abbreviations(String name)
	{
		this(name, TokenIsAnAbbFactory.alwaysTrue);
	}

	public Abbreviations(String name, TokenIsAnAbbreviation<? super T> abbsChecker)
	{
		super(name);
		this.abbsChecker = (TokenIsAnAbbreviation<T>) abbsChecker;
		short2longForms = new HashMap<String, Set<String>>();
	}

	/**
	 * Add an abbreviation.
	 *
	 * @param shortForm an abbreviation short form (ex: CHF).
	 * @param longForm  an abbreviation long form. (ex: congestive heart failure).
	 * @param tokenizer a {@link ITokenizer} to tokenize the long form. It is
	 *                  recommended to use the {@link Matcher} tokenizer.
	 */
	public void add(String shortForm, String longForm, ITokenizer<T> tokenizer)
	{
		String shortLower = normShortForm(shortForm);
		List<T> longTokens = tokenizer.tokenize(longForm);
		String longFormNorm = longTokens.stream().map(t -> t.normLabel()).collect(Collectors.joining(" "));
		if (!short2longForms.containsKey(shortLower))
		{
			short2longForms.put(shortLower, new HashSet<String>());
		}
		short2longForms.get(shortLower).add(longFormNorm);
	}

	@Override
	public List<SynAlgo> getSynonyms(T token)
	{
		if (!abbsChecker.isAnAbb(token))
			return FuzzyAlgo.NO_SYN;
		String potentialShortForm = token.normLabel();
		String potentialNorm = normShortForm(potentialShortForm);
		if (short2longForms.containsKey(potentialNorm))
		{
			Set<String> longForms = short2longForms.get(potentialNorm);
			return this.words2syn(longForms);
		} else
		{
			return FuzzyAlgo.NO_SYN;
		}
	}

	private String normShortForm(String label)
	{
		return label.toLowerCase();
	}
}
