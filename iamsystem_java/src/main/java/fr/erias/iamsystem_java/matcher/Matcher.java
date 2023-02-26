package fr.erias.iamsystem_java.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.fuzzy.ExactMatch;
import fr.erias.iamsystem_java.fuzzy.FuzzyAlgo;
import fr.erias.iamsystem_java.fuzzy.ISynsProvider;
import fr.erias.iamsystem_java.fuzzy.SynAlgos;
import fr.erias.iamsystem_java.fuzzy.SynsProvider;
import fr.erias.iamsystem_java.keywords.IKeyword;
import fr.erias.iamsystem_java.keywords.IStoreKeywords;
import fr.erias.iamsystem_java.keywords.Keyword;
import fr.erias.iamsystem_java.keywords.Terminology;
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.tokenize.AbstractTokNorm;
import fr.erias.iamsystem_java.tokenize.IOffsets;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.TokStopImp;
import fr.erias.iamsystem_java.tree.EmptyNode;
import fr.erias.iamsystem_java.tree.INode;
import fr.erias.iamsystem_java.tree.Trie;

class Detector<T extends IToken>
{

	private Annotation<T> createAnnot(LinkedState<T> last_el, List<T> stopTokens)
	{
		List<LinkedState<T>> transStates = this.toList(last_el);
		INode lastState = last_el.getNode();
		List<T> tokens = transStates.stream().map(t -> t.getToken()).collect(Collectors.toList());
		tokens.sort(Comparator.naturalOrder());
		List<Collection<String>> algos = transStates.stream().map(t -> t.getAlgos()).collect(Collectors.toList());
		return new Annotation<T>(tokens, algos, lastState, stopTokens);
	}

	private LinkedState<T> createStartState(INode initialState)
	{
		return new LinkedState<T>(null, initialState, null, null, -1);
	}

	public List<IAnnotation<T>> detect(List<T> tokens, int w, INode initialState, ISynsProvider<T> synsProvider,
			IStopwords<T> stopwords)
	{
		List<IAnnotation<T>> annots = new ArrayList<IAnnotation<T>>();
		// states stores linkedstate instance that keeps track of a tree path
		// and document's tokens that matched.
		Set<LinkedState<T>> states = new HashSet<LinkedState<T>>();
		LinkedState<T> startState = createStartState(initialState);
		states.add(startState);

		// count_not_stopword allows a stopword-independent window size.
		int count_not_stopword = 0;
		List<T> stopTokens = new ArrayList<T>();
		List<LinkedState<T>> newStates = new ArrayList<LinkedState<T>>();
		List<LinkedState<T>> states2remove = new ArrayList<LinkedState<T>>();

		for (T token : tokens)
		{
			if (stopwords.isTokenAStopword(token))
			{
				stopTokens.add(token);
				continue;
			}
			// w_bucket stores when a state will be out-of-reach given window size
			// 'count_not_stopword % w' has range [0 ; w-1]
			int wBucket = count_not_stopword % w;
			newStates.clear();
			states2remove.clear();
			count_not_stopword++;

			Collection<SynAlgos> synAlgos = synsProvider.getSynonyms(tokens, token, states);

			for (LinkedState<T> state : states)
			{
				if (state.getwBucket() == wBucket)
					states2remove.add(state);

				for (SynAlgos synAlgo : synAlgos)
				{
					INode node = state.getNode().gotoNode(synAlgo.getSynToken());
					if (node == EmptyNode.EMPTYNODE)
						continue;
					LinkedState<T> newState = new LinkedState<T>(state, node, token, synAlgo.getAlgos(), wBucket);
					newStates.add(newState);
					/**
					 * Why 'states.contains(newState)': if node_num is already in the states set, it
					 * means an annotation was already created for this state. For example 'cancer
					 * cancer', if an annotation was created for the first 'cancer' then we don't
					 * want to create a new one for the second 'cancer'.
					 */

					if (node.isAfinalState() && !states.contains(newState))
					{
						IAnnotation<T> annotation = this.createAnnot(newState, stopTokens);
						annots.add(annotation);
					}
				}
			}
			/**
			 * Prepare next iteration: first loop remove out-of-reach states. Second
			 * iteration add new states.
			 */
			for (LinkedState<T> state : states2remove)
			{
				states.remove(state);
			}
			for (LinkedState<T> state : newStates)
			{
				if (states.contains(state))
					states.remove(state);
				states.add(state);
			}
		}
		annots.sort(Comparator.naturalOrder());
		return annots;
	}

	protected List<IAnnotation<T>> rmNestedAnnots(List<IAnnotation<T>> annots, boolean keepAncestors)
	{
		Set<Integer> ancestIndices = new HashSet<Integer>();
		Set<Integer> shortIndices = new HashSet<Integer>();
		int count = 0;
		for (int i = 0; i < annots.size(); i++)
		{
			IAnnotation<T> annot = annots.get(i);
			for (int y = i + 1; y < annots.size(); y++)
			{
				if (shortIndices.contains(y))
					continue;
				IAnnotation<T> other = annots.get(y);
				if (!IOffsets.offsetsOverlap(annot, other))
					break;
				if (Span.isShorterSpanOf(annot, other))
				{
					shortIndices.add(i);
					if (IAnnotation.isAncestorAnnotOf(annot, other))
					{
						ancestIndices.add(i);
					}
				}
				if (Span.isShorterSpanOf(other, annot))
				{
					shortIndices.add(y);
				}
				count++;
			}
		}
		Set<Integer> indices2remove;
		if (!keepAncestors)
		{
			indices2remove = shortIndices;
		} else
		{
			indices2remove = shortIndices.stream().filter(i -> !ancestIndices.contains(i)).collect(Collectors.toSet());
		}
		List<IAnnotation<T>> annots2keep = new ArrayList<IAnnotation<T>>(annots.size() - indices2remove.size());
		for (int i = 0; i < annots.size(); i++)
		{
			if (!indices2remove.contains(i))
			{
				annots2keep.add(annots.get(i));
			}
		}
		return annots2keep;
	}

	private List<LinkedState<T>> toList(LinkedState<T> last_el)
	{
		List<LinkedState<T>> transStates = new ArrayList<>();
		transStates.add(last_el);
		LinkedState<T> parent = last_el.getParent();
		while (!LinkedState.isStartState(parent))
		{
			transStates.add(parent);
			parent = parent.getParent();
		}
		Collections.reverse(transStates);
		return transStates;
	}
}

public class Matcher<T extends IToken>
		implements IMatcher<T>, IStoreKeywords, ITokenizer<T>, IStopwords<T>, ISynsProvider<T>
{

	private ITokenizer<T> tokenizer;
	private IStopwords<T> stopwords;
	private final Detector<T> detector;
	private int w = 1;
	private final Trie trie = new Trie();
	private Terminology termino = new Terminology();
	private boolean removeNestedAnnot = true;
	private TokStopImp<T> tokstop; // TODO set stopwords/tokenizer
	private List<FuzzyAlgo<T>> fuzzyAlgos = new ArrayList<FuzzyAlgo<T>>();
	private SynsProvider<T> synsProvider;

	public Matcher(ITokenizer<T> tokenizer, IStopwords<T> stopwords)
	{
		this.setTokenizer(tokenizer);
		this.setStopwords(stopwords);
		this.detector = new Detector<T>();
		this.tokstop = new TokStopImp<T>(tokenizer, stopwords);
		fuzzyAlgos.add(new ExactMatch<T>());
		this.synsProvider = new SynsProvider<T>(fuzzyAlgos);
	}

	public void addFuzzyAlgo(FuzzyAlgo<T> fuzzyAlgo)
	{
		this.fuzzyAlgos.add(fuzzyAlgo);
	}

	@Override
	public void addKeyword(IKeyword keyword)
	{
		termino.addKeyword(keyword);
		trie.addIKeyword(keyword, tokstop);
	}

	public void addKeyword(Iterable<? extends IKeyword> keywords)
	{
		for (IKeyword kw : keywords)
		{
			this.addKeyword(kw);
		}
	}

	/**
	 * Add keywords by providing a String iterable.
	 *
	 * @param keywords A collection of keywords to add.
	 */
	public void addKeyword(String[] keywords)
	{
		for (String kw : keywords)
		{
			Keyword k = new Keyword(kw);
			this.addKeyword(k);
		}
	}

	public List<IAnnotation<T>> annot(List<T> tokens)
	{
		List<IAnnotation<T>> annots = detector.detect(tokens, w, trie.getInitialState(), this, stopwords);
		if (this.removeNestedAnnot)
		{
			annots = this.detector.rmNestedAnnots(annots, false);
		}
		return annots;
	}

	public List<IAnnotation<T>> annot(String text)
	{
		List<T> tokens = tokenize(text);
		List<IAnnotation<T>> annots = this.annot(tokens);
		return annots;
	}

	@Override
	public Collection<IKeyword> getKeywords()
	{
		return termino.getKeywords();
	}

	public IStopwords<T> getStopwords()
	{
		return stopwords;
	}

	@Override
	public Collection<SynAlgos> getSynonyms(List<T> tokens, T token, Set<LinkedState<T>> states)
	{
		return synsProvider.getSynonyms(tokens, token, states);
	}

	public ITokenizer<T> getTokenizer()
	{
		return tokenizer;
	}

	public AbstractTokNorm<T> getTokStop()
	{
		return this.tokstop;
	}

	public Set<String> getUnigrams()
	{
		return IStoreKeywords.getUnigrams(this.getKeywords(), tokstop);
	}

	public int getW()
	{
		return w;
	}

	@Override
	public boolean isTokenAStopword(T token)
	{
		return stopwords.isTokenAStopword(token);
	}

	public boolean removeNestedAnnot()
	{
		return removeNestedAnnot;
	}

	public void setRemoveNestedAnnot(boolean removeNestedAnnot)
	{
		this.removeNestedAnnot = removeNestedAnnot;
	}

	public void setStopwords(IStopwords<T> stopwords)
	{
		this.stopwords = stopwords;
	}

	public void setTokenizer(ITokenizer<T> tokenizer)
	{
		this.tokenizer = tokenizer;
	}

	public void setW(int w)
	{
		this.w = w;
	}

	@Override
	public List<T> tokenize(String text)
	{
		return tokenizer.tokenize(text);
	}
}
