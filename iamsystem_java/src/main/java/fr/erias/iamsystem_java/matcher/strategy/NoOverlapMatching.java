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
import fr.erias.iamsystem_java.tokenize.Token;
import fr.erias.iamsystem_java.tree.EmptyNode;
import fr.erias.iamsystem_java.tree.INode;

/**
 * The old matching strategy that was in used till 2022. The 'w' parameter has
 * no effect. It annotates the longest path and outputs no overlapping
 * annotation except in case of ambiguity. It's the fastest strategy. Algorithm
 * formalized in https://ceur-ws.org/Vol-3202/livingner-paper11.pdf
 *
 * @author Sebastien Cossin
 *
 */
public class NoOverlapMatching implements IMatchingStrategy
{
	private final static Token END_TOKEN = new Token(-1, -1, 
			"IAMSYSTEM_END_TOKEN", "IAMSYSTEM_END_TOKEN", -1);

	/**
	 * Create annotations and mutate annots list.
	 * 
	 * @param annots     the list of annotations.
	 * @param states     the current algorithm's states.
	 * @param startedAt  the 'i' token at which the algorithm started a search.
	 * @param stopTokens stopwords.
	 * @return the last annotation 'i' value or started_at if no annotation
	 *         generated.
	 */
	private int addAnnots(List<IAnnotation> annots, Set<StateTransition> states, int startedAt, List<IToken> stopTokens)
	{
		int lastAnnotI = -1;
		for (StateTransition state : states)
		{
			StateTransition currentState = state;
			while (!currentState.getNode().isAfinalState() && !StateTransition.isFirstTrans(currentState))
			{
				currentState = currentState.getPreviousTrans();
			}
			if (currentState.getNode().isAfinalState())
			{
				IAnnotation annotation = StrategyUtils.createAnnot(currentState, stopTokens);
				annots.add(annotation);
				lastAnnotI = Math.max(annotation.end_i(), lastAnnotI);
			}
		}
		return Math.max(startedAt, lastAnnotI);
	}

	@Override
	public List<IAnnotation> detect(List<IToken> tokens, int w, INode initialState, ISynsProvider synsProvider,
			IStopwords stopwords)
	{
		List<IAnnotation> annots = new ArrayList<IAnnotation>();
		Set<StateTransition> states = new HashSet<>();
		StateTransition startState = StrategyUtils.createStartState(initialState);
		states.add(startState);
		List<IToken> stopTokens = new ArrayList<IToken>();
		int i = 0;
		int startedAt = 0;
		while (i < tokens.size() + 1)
		{
			IToken token = (i == tokens.size())? NoOverlapMatching.END_TOKEN : tokens.get(i);
			if (stopwords.isTokenAStopword(token))
			{
				stopTokens.add(token);
				i += 1;
				startedAt += 1;
				continue;
			}
			Set<StateTransition> newStates = new HashSet<StateTransition>();
			Collection<SynAlgos> synAlgos = synsProvider.getSynonyms(tokens, token, states);
			for (StateTransition state : states)
			{
				for (SynAlgos synAlgo : synAlgos)
				{
					INode node = state.getNode().gotoNode(synAlgo.getSynToken());
					if (node == EmptyNode.EMPTYNODE)
						continue;
					StateTransition newState = new StateTransition(state, node, token, synAlgo.getAlgos(), -1);
					newStates.add(newState);
				}
			}
			if (newStates.size() != 0)
			{
				states = newStates;
				i += 1;
			} else
			{
				if ((states.size() == 1) && states.contains(startState))
				{
					i += 1;
					startedAt += 1;
					continue;
				}
				int lastI = this.addAnnots(annots, states, startedAt, stopTokens);
				i = lastI + 1;
				startedAt = startedAt + 1;
				states.clear();
				states.add(startState);
			}
		}
		this.addAnnots(annots, states, startedAt, stopTokens);
		annots.sort(Comparator.naturalOrder());
		return annots;
	}
}
