package fr.erias.iamsystem_java.matcher.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.erias.iamsystem_java.fuzzy.base.ISynsProvider;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgos;
import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.StateTransition;
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tree.EmptyNode;
import fr.erias.iamsystem_java.tree.INode;

/**
 * Default matching strategy. It keeps track of all states within a window range
 * and can be produce overlapping/nested annotations. If you want to use a large
 * window with a large dictionary, it is recommended to use
 * 'LargeWindowMatching' instead.
 *
 * @author Sebastien Cossin
 *
 */
public class WindowMatching implements IMatchingStrategy
{

	@Override
	public List<IAnnotation> detect(List<IToken> tokens, int w, INode initialState, ISynsProvider synsProvider,
			IStopwords stopwords)
	{
		List<IAnnotation> annots = new ArrayList<IAnnotation>();
		// states stores linkedstate instance that keeps track of a tree path
		// and document's tokens that matched.
		Set<StateTransition> states = new HashSet<StateTransition>();
		StateTransition startState = StrategyUtils.createStartState(initialState);
		states.add(startState);

		// count_not_stopword allows a stopword-independent window size.
		int countNotStopword = 0;
		List<IToken> stopTokens = new ArrayList<IToken>();
		List<StateTransition> newStates = new ArrayList<StateTransition>();
		List<StateTransition> states2remove = new ArrayList<StateTransition>();

		for (IToken token : tokens)
		{
			if (stopwords.isTokenAStopword(token))
			{
				stopTokens.add(token);
				continue;
			}
			// w_bucket stores when a state will be out-of-reach given window size
			// 'count_not_stopword % w' has range [0 ; w-1]
			int wBucket = countNotStopword % w;
			newStates.clear();
			states2remove.clear();
			countNotStopword++;

			Collection<SynAlgos> synAlgos = synsProvider.getSynonyms(tokens, token, states);

			for (StateTransition state : states)
			{
				if (state.getCountNotStopword() == wBucket)
					states2remove.add(state);

				for (SynAlgos synAlgo : synAlgos)
				{
					INode node = state.getNode().gotoNode(synAlgo.getSynToken());
					if (node == EmptyNode.EMPTYNODE)
						continue;
					StateTransition newState = new StateTransition(state, node, token, synAlgo.getAlgos(), wBucket);
					newStates.add(newState);
					/**
					 * Why 'states.contains(newState)': if node_num is already in the states set, it
					 * means an annotation was already created for this state. For example 'cancer
					 * cancer', if an annotation was created for the first 'cancer' then we don't
					 * want to create a new one for the second 'cancer'.
					 */

					if (node.isAfinalState() && !states.contains(newState))
					{
						IAnnotation annotation = StrategyUtils.createAnnot(newState, stopTokens);
						annots.add(annotation);
					}
				}
			}
			/**
			 * Prepare next iteration: first loop remove out-of-reach states. Second
			 * iteration add new states.
			 */
			for (StateTransition state : states2remove)
			{
				states.remove(state);
			}
			for (StateTransition state : newStates)
			{
				if (states.contains(state))
					states.remove(state);
				states.add(state);
			}
		}
		annots.sort(Comparator.naturalOrder());
		return annots;
	}

}
