package fr.erias.iamsystem.stopwords;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.erias.iamsystem.fuzzy.CacheFuzzyAlgos;
import fr.erias.iamsystem.fuzzy.ExactMatch;
import fr.erias.iamsystem.fuzzy.base.ContextFreeAlgo;
import fr.erias.iamsystem.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem.fuzzy.base.SynAlgo;
import fr.erias.iamsystem.tokenize.IToken;

public class Word2KeepFuzzy implements IWord2keepF
{

	private final List<ContextFreeAlgo> contextFreeAlgos = new ArrayList<>();
	private final List<CacheFuzzyAlgos> caches = new ArrayList<>();
	private final IStopwords stopwords;

	public Word2KeepFuzzy(IStopwords stopwords, Iterable<FuzzyAlgo> fuzzyAlgos)
	{
		this.stopwords = stopwords;
		for (FuzzyAlgo algo : fuzzyAlgos)
		{
			if (algo instanceof CacheFuzzyAlgos)
			{
				caches.add((CacheFuzzyAlgos) algo);
			}
			if (algo instanceof ContextFreeAlgo && !(algo instanceof ExactMatch))
			{
				contextFreeAlgos.add((ContextFreeAlgo) algo);
			}
		}
	}

	@Override
	public boolean isAword2keep(IToken token)
	{
		{
			if (this.stopwords.isTokenAStopword(token))
				return false;
			List<SynAlgo> synsContextFreeAlgos = this.contextFreeAlgos.stream()
					.map(algo -> algo.getSynonyms(token))
					.flatMap(List::stream)
					.filter(synalgo -> synalgo != FuzzyAlgo.NO_SYN)
					.collect(Collectors.toList());
			List<SynAlgo> synsCaches = this.caches.stream()
					.map(algo -> algo.getSynonyms(token))
					.flatMap(List::stream)
					.filter(synalgo -> synalgo != FuzzyAlgo.NO_SYN)
					.collect(Collectors.toList());
			return synsContextFreeAlgos.size() != 0 || synsCaches.size() != 0;
		}
	}
}
