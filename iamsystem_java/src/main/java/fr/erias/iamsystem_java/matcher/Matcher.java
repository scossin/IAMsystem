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

	private Annotation<T> createAnnot(TransitionState<T> last_el, List<T> stopTokens)
	{
		List<TransitionState<T>> transStates = this.toList(last_el);
		INode lastState = last_el.getNode();
		List<T> tokens = transStates.stream().map(t -> t.getToken()).collect(Collectors.toList());
		tokens.sort(Comparator.naturalOrder());
		List<Collection<String>> algos = transStates.stream().map(t -> t.getAlgos()).collect(Collectors.toList());
		return new Annotation<T>(tokens, algos, lastState, stopTokens);
	}

	public List<IAnnotation<T>> detect(List<T> tokens, int w, INode initialState, ISynsProvider<T> synsProvider,
			IStopwords<T> stopwords)
	{
		List<IAnnotation<T>> annots = new ArrayList<IAnnotation<T>>();
		TransitionState<T> startState = new TransitionState<T>(null, initialState, null, null);
		int w_states_size = w + 2;
		List<TransitionState<T>>[] w_states = new ArrayList[w_states_size];
		for (int i = 0; i < w_states_size; i++)
		{
			w_states[i] = new ArrayList<TransitionState<T>>();
		}
		w_states[w_states_size - 2].add(startState);
		List<TransitionState<T>> tempTransStates = w_states[w_states_size - 1];

		int count_not_stopword = 0;
		List<T> stopTokens = new ArrayList<T>();
		for (T token : tokens)
		{
			if (stopwords.isTokenAStopword(token))
			{
				stopTokens.add(token);
				continue;
			}
			count_not_stopword++;
			tempTransStates.clear();
			Iterable<SynAlgos> synAlgos = synsProvider.getSynonyms(tokens, token, w_states);
			for (SynAlgos synAlgo : synAlgos)
			{
				for (int i = 0; i < w_states_size - 1; i++)
				{
					List<TransitionState<T>> transStates = w_states[i];
					for (TransitionState<T> transState : transStates)
					{
						INode node = transState.getNode().gotoNode(synAlgo.getSynToken());
						if (node == EmptyNode.EMPTYNODE)
							continue;
						TransitionState<T> newTransState = new TransitionState<T>(transState, node, token,
								synAlgo.getAlgos());
						if (node.isAfinalState())
						{
							IAnnotation<T> annotation = this.createAnnot(newTransState, stopTokens);
							annots.add(annotation);
						}
						tempTransStates.add(newTransState);
					}
				}
			}
			w_states[count_not_stopword % w].clear();
			w_states[count_not_stopword % w].addAll(tempTransStates);
			// TODO: test performance creating tempTransStates at each iteration
			// vs doing clear and addAll operation.
		}
		annots.sort(Comparator.naturalOrder());
		return annots;
	}

	protected List<IAnnotation<T>> rmNestedAnnots(List<IAnnotation<T>> annots, boolean keepAncestors)
	{
		Set<Integer> ancestIndices = new HashSet<Integer>();
		Set<Integer> shortIndices = new HashSet<Integer>();

		for (int i = 0; i < annots.size(); i++)
		{
			IAnnotation<T> annot = annots.get(i);
			for (int y = i + 1; y < annots.size(); y++)
			{
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
				if (Span.isShorterSpanOf(annot, other))
				{
					shortIndices.add(y);
				}
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

	private List<TransitionState<T>> toList(TransitionState<T> last_el)
	{
		List<TransitionState<T>> transStates = new ArrayList<>();
		transStates.add(last_el);
		TransitionState<T> parent = last_el.getParent();
		while (!TransitionState.isStartState(parent))
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
	public Collection<SynAlgos> getSynonyms(List<T> tokens, T token, List<TransitionState<T>>[] wStates)
	{
		return synsProvider.getSynonyms(tokens, token, wStates);
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
