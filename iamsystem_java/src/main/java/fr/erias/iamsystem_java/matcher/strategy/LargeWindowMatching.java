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
import fr.erias.iamsystem_java.matcher.StateTransition;
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

	private INode initialState = null;
	private final Map<Integer, StateTransition> transitions = new HashMap<>();
	private final Map<String, Set<Integer>> availableTransitions = new HashMap<>();

	private void addTransition(StateTransition state, Map<String, Set<Integer>> availableTransitions)
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
		if (initialState == null || this.initialState != initialState)
		{
			this.initialize(initialState);
		}
		List<IAnnotation> annots = new ArrayList<IAnnotation>();

		int countNotStopWord = 0;
		List<IToken> stopTokens = new ArrayList<IToken>();
		Set<StateTransition> newTransitions = new HashSet<StateTransition>();

		Map<Integer, StateTransition> transitionsCopy = new HashMap<>(this.transitions);
		Map<String, Set<Integer>> availableTransitionsCopy = new HashMap<>(availableTransitions);

		for (IToken token : tokens)
		{
			if (stopwords.isTokenAStopword(token))
			{
				stopTokens.add(token);
				continue;
			}
			newTransitions.clear();
			countNotStopWord += 1;
			Collection<SynAlgos> synAlgos = synsProvider.getSynonyms(tokens, token, transitionsCopy.values());

			for (SynAlgos synAlgo : synAlgos)
			{
				String firstSynToken = synAlgo.getSynToken()[0];
				if (!availableTransitionsCopy.containsKey(firstSynToken))
					continue;
				Set<Integer> transitionsId = availableTransitionsCopy.get(firstSynToken);
				Set<Integer> newTransId = new HashSet<Integer>();
				for (int transId : transitionsId)
				{
					if (!transitionsCopy.containsKey(transId))
						continue;
					StateTransition trans = transitionsCopy.get(transId);
					if (trans.isObsolete(countNotStopWord, w))
					{
						transitionsCopy.remove(transId);
						continue;
					}
					newTransId.add(transId);
					INode node = trans.getNode().gotoNode(synAlgo.getSynToken());
					if (node == EmptyNode.EMPTYNODE)
						continue;
					StateTransition nextTrans = new StateTransition(trans, node, token, synAlgo.getAlgos(),
							countNotStopWord);
					newTransitions.add(nextTrans);
				}
				availableTransitionsCopy.put(firstSynToken, newTransId);
			}
			for (StateTransition trans : newTransitions)
			{
				if (trans.getNode().isAfinalState())
				{
					StateTransition oldtrans = transitionsCopy.get(trans.getId());
					if (oldtrans == null || oldtrans.isObsolete(countNotStopWord, w))
					{
						IAnnotation annot = StrategyUtils.createAnnot(trans, stopTokens);
						annots.add(annot);
					}
				}
				this.addTransition(trans, availableTransitionsCopy);
				transitionsCopy.put(trans.getId(), trans);
			}
		}
		return annots;
	}

	private Set<String> getChildTokens(INode node)
	{
		return node.getChildrenNodes().stream().map(childNode -> childNode.getToken()).collect(Collectors.toSet());
	}

	/**
	 * Initialize hashtable to avoid repeating this operation multiple times.
	 *
	 * @param initialState the initial state (eg. root node).
	 */
	private void initialize(INode initialState)
	{
		StateTransition firstTrans = StateTransition.createFristTrans(initialState);
		transitions.put(firstTrans.getId(), firstTrans);
		addTransition(firstTrans, availableTransitions);
	}
}
