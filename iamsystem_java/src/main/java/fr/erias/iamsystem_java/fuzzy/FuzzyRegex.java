package fr.erias.iamsystem_java.fuzzy;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.erias.iamsystem_java.fuzzy.base.ContextFreeAlgo;
import fr.erias.iamsystem_java.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.tokenize.IToken;

public class FuzzyRegex extends ContextFreeAlgo
{

	private final Pattern patternCompiled;
	private final List<SynAlgo> syn;

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

	public boolean tokenMatchesPattern(IToken token)
	{
		return wordMatchesPattern(token.label());
	}

	public boolean wordMatchesPattern(String label)
	{
		return patternCompiled.matcher(label).find();
	}
}
