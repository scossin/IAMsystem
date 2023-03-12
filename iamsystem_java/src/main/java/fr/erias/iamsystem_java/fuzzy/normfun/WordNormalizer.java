package fr.erias.iamsystem_java.fuzzy.normfun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.erias.iamsystem_java.fuzzy.base.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.base.NormLabelAlgo;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgo;
import fr.erias.iamsystem_java.tokenize.INormalizeF;

/**
 * A fuzzy algorithm that calls a normalization function such as stemming and
 * lemmatization.
 *
 * @author Sebastien Cossin
 *
 */
public class WordNormalizer extends NormLabelAlgo
{

	private INormalizeF normfun;
	private Map<String, List<SynAlgo>> norm2syns = new HashMap<String, List<SynAlgo>>();

	/**
	 * Constructor.
	 *
	 * @param name    a name given to this algorithm (ex: 'english stemmer').
	 * @param normfun a normalizing function, for example a stemming function or
	 *                lemmatization function.
	 */
	public WordNormalizer(String name, INormalizeF normfun)
	{
		super(name);
		this.normfun = normfun;
	}

	/**
	 * A list of possible word synonyms, in general all the tokens of your keywords.
	 *
	 * @param unigrams A list of words to normalize and store.
	 */
	public void addWords(Iterable<String> unigrams)
	{
		for (String unigram : unigrams)
		{
			String normalized = this.normfun.normalize(unigram);
			if (!norm2syns.containsKey(normalized))
			{
				norm2syns.put(normalized, new ArrayList<SynAlgo>());
			}
			SynAlgo synsAlgo = new SynAlgo(unigram, this.getName());
			norm2syns.get(normalized).add(synsAlgo);
		}
	}

	@Override
	public List<SynAlgo> getSynsOfWord(String normLabel)
	{
		String normalized = this.normfun.normalize(normLabel);
		if (!norm2syns.containsKey(normalized))
			return FuzzyAlgo.NO_SYN;
		return norm2syns.get(normalized);
	}

}
