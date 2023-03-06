package fr.erias.iamsystem_java.matcher.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.fuzzy.base.ISynsProvider;
import fr.erias.iamsystem_java.fuzzy.base.SynAlgos;
import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.LinkedState;
import fr.erias.iamsystem_java.stopwords.IStopwords;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tree.EmptyNode;
import fr.erias.iamsystem_java.tree.INode;

/**
 * A large window strategy suited for a large window (ex: w=1000) and a large
 * dictionary. This strategy is faster than the Window strategy if the
 * dictionary is large, otherwise it's slower. It trades space for time
 * complexity (space memory increases but matching speed increases).
 *
 * @author Sebastien Cossin
 *
 */
public class LargeWindowMatching implements IMatchingStrategy
{

	private boolean isInitialized = false;
	private final Map<Integer, LinkedState> states = new HashMap<>();
	private final Map<String, Set<Integer>> availableTransitions = new HashMap<>();

	private void addTransition(LinkedState state, Map<String, Set<Integer>> availableTransitions)
	{
		Set<String> childTokens = getChildTokens(state.getNode());
		childTokens.stream().forEach(token ->
		{
			if (!availableTransitions.containsKey(token))
			{
				availableTransitions.put(token, new HashSet<Integer>());
			}
			availableTransitions.get(token).add(state.getId());
		});
	}

	@Override
	public List<IAnnotation> detect(List<IToken> tokens, int w, INode initialState, ISynsProvider synsProvider,
			IStopwords stopwords)
	{
		if (!isInitialized)
		{
			this.initialize(initialState);
			isInitialized = true;
		}
		List<IAnnotation> annots = new ArrayList<IAnnotation>();

		int countNotStopWord = 0;
		List<IToken> stopTokens = new ArrayList<IToken>();
		Set<LinkedState> newStates = new HashSet<LinkedState>();

		Map<Integer, LinkedState> statesCopy = new HashMap<>(this.states);
		Map<String, Set<Integer>> availableTransitionsCopy = new HashMap<>(availableTransitions);

		for (IToken token : tokens)
		{
			if (stopwords.isTokenAStopword(token))
			{
				stopTokens.add(token);
				continue;
			}
			newStates.clear();
			countNotStopWord += 1;
			Collection<SynAlgos> synAlgos = synsProvider.getSynonyms(tokens, token, statesCopy.values());

			for (SynAlgos synAlgo : synAlgos)
			{
				String firstSynToken = synAlgo.getSynToken()[0];
				if (!availableTransitionsCopy.containsKey(firstSynToken))
					continue;
				Set<Integer> statesId = availableTransitionsCopy.get(firstSynToken);
				Set<Integer> newStatesTransitions = new HashSet<Integer>();
				for (int stateId : statesId)
				{
					if (!statesCopy.containsKey(stateId))
						continue;
					LinkedState state = statesCopy.get(stateId);
					if (state.isObsolete(countNotStopWord, w))
					{
						statesCopy.remove(stateId);
						continue;
					}
					newStatesTransitions.add(stateId);
					INode node = state.getNode().gotoNode(synAlgo.getSynToken());
					if (node == EmptyNode.EMPTYNODE)
						continue;
					LinkedState newState = new LinkedState(state, node, token, synAlgo.getAlgos(), countNotStopWord);
					newStates.add(newState);
				}
				availableTransitionsCopy.put(firstSynToken, newStatesTransitions);
			}
			for (LinkedState state : newStates)
			{
				if (state.getNode().isAfinalState())
				{
					LinkedState oldState = statesCopy.get(state.getId());
					if (oldState == null || oldState.isObsolete(countNotStopWord, w))
					{
						IAnnotation annot = StrategyUtils.createAnnot(state, stopTokens);
						annots.add(annot);
					}
				}
				this.addTransition(state, availableTransitionsCopy);
				statesCopy.put(state.getId(), state);
			}
		}
		return annots;
	}

	private Set<String> getChildTokens(INode node)
	{
		return node.getChildNodes().stream().map(childNode -> childNode.getToken()).collect(Collectors.toSet());
	}

	/**
	 * Initialize hashtable to avoid repeating this operation multiple times.
	 *
	 * @param initialState the initial state (eg. root node).
	 */
	private void initialize(INode initialState)
	{
		LinkedState startState = StrategyUtils.createStartState(initialState);
		states.put(startState.getId(), startState);
		addTransition(startState, availableTransitions);
	}
}
