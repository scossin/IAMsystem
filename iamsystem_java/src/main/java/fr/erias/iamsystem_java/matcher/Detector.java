package fr.erias.iamsystem_java.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.fuzzy.base.ISynsProvider;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgos;
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.tokenize.IOffsets;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tree.EmptyNode;
import fr.erias.iamsystem_java.tree.INode;

public class Detector
{

	private Annotation createAnnot(LinkedState last_el, List<IToken> stopTokens)
	{
		List<LinkedState> transStates = this.toList(last_el);
		INode lastState = last_el.getNode();
		List<IToken> tokens = transStates.stream().map(t -> t.getToken()).collect(Collectors.toList());
		tokens.sort(Comparator.naturalOrder());
		List<Collection<String>> algos = transStates.stream().map(t -> t.getAlgos()).collect(Collectors.toList());
		return new Annotation(tokens, algos, lastState, stopTokens);
	}

	private LinkedState createStartState(INode initialState)
	{
		return new LinkedState(null, initialState, null, null, -1);
	}

	public List<IAnnotation> detect(List<IToken> tokens, int w, INode initialState, ISynsProvider synsProvider,
			IStopwords stopwords)
	{
		List<IAnnotation> annots = new ArrayList<IAnnotation>();
		// states stores linkedstate instance that keeps track of a tree path
		// and document's tokens that matched.
		Set<LinkedState> states = new HashSet<LinkedState>();
		LinkedState startState = createStartState(initialState);
		states.add(startState);

		// count_not_stopword allows a stopword-independent window size.
		int count_not_stopword = 0;
		List<IToken> stopTokens = new ArrayList<IToken>();
		List<LinkedState> newStates = new ArrayList<LinkedState>();
		List<LinkedState> states2remove = new ArrayList<LinkedState>();

		for (IToken token : tokens)
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

			for (LinkedState state : states)
			{
				if (state.getwBucket() == wBucket)
					states2remove.add(state);

				for (SynAlgos synAlgo : synAlgos)
				{
					INode node = state.getNode().gotoNode(synAlgo.getSynToken());
					if (node == EmptyNode.EMPTYNODE)
						continue;
					LinkedState newState = new LinkedState(state, node, token, synAlgo.getAlgos(), wBucket);
					newStates.add(newState);
					/**
					 * Why 'states.contains(newState)': if node_num is already in the states set, it
					 * means an annotation was already created for this state. For example 'cancer
					 * cancer', if an annotation was created for the first 'cancer' then we don't
					 * want to create a new one for the second 'cancer'.
					 */

					if (node.isAfinalState() && !states.contains(newState))
					{
						IAnnotation annotation = this.createAnnot(newState, stopTokens);
						annots.add(annotation);
					}
				}
			}
			/**
			 * Prepare next iteration: first loop remove out-of-reach states. Second
			 * iteration add new states.
			 */
			for (LinkedState state : states2remove)
			{
				states.remove(state);
			}
			for (LinkedState state : newStates)
			{
				if (states.contains(state))
					states.remove(state);
				states.add(state);
			}
		}
		annots.sort(Comparator.naturalOrder());
		return annots;
	}

	protected List<IAnnotation> rmNestedAnnots(List<IAnnotation> annots, boolean keepAncestors)
	{
		Set<Integer> ancestIndices = new HashSet<Integer>();
		Set<Integer> shortIndices = new HashSet<Integer>();
		for (int i = 0; i < annots.size(); i++)
		{
			IAnnotation annot = annots.get(i);
			for (int y = i + 1; y < annots.size(); y++)
			{
				if (shortIndices.contains(y))
					continue;
				IAnnotation other = annots.get(y);
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
		List<IAnnotation> annots2keep = new ArrayList<IAnnotation>(annots.size() - indices2remove.size());
		for (int i = 0; i < annots.size(); i++)
		{
			if (!indices2remove.contains(i))
			{
				annots2keep.add(annots.get(i));
			}
		}
		return annots2keep;
	}

	private List<LinkedState> toList(LinkedState last_el)
	{
		List<LinkedState> transStates = new ArrayList<>();
		transStates.add(last_el);
		LinkedState parent = last_el.getParent();
		while (!LinkedState.isStartState(parent))
		{
			transStates.add(parent);
			parent = parent.getParent();
		}
		Collections.reverse(transStates);
		return transStates;
	}
}