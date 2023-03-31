package fr.erias.iamsystem.matcher.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.erias.iamsystem.annotation.IAnnotation;
import fr.erias.iamsystem.fuzzy.base.ISynsProvider;
import fr.erias.iamsystem.fuzzy.base.SynAlgos;
import fr.erias.iamsystem.matcher.StateTransition;
import fr.erias.iamsystem.stopwords.IStopwords;
import fr.erias.iamsystem.tokenize.IToken;
import fr.erias.iamsystem.tree.EmptyNode;
import fr.erias.iamsystem.tree.INode;

/**
 * Default matching strategy. It keeps track of all states within a window range
 * and can be produce overlapping/nested annotations. If you want to use a large
 * window with a large dictionary, it is recommended to use
 * {@link LargeWindowMatching} instead.
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
		Set<StateTransition> transitions = new HashSet<StateTransition>();
		StateTransition fistTrans = StateTransition.createFristTrans(initialState);
		transitions.add(fistTrans);
		// count_not_stopword allows a stopword-independent window size.
		int countNotStopword = 0;
		List<IToken> stopTokens = new ArrayList<IToken>();
		List<StateTransition> newTransitions = new ArrayList<StateTransition>();
		List<StateTransition> trans2remove = new ArrayList<StateTransition>();

		for (IToken token : tokens)
		{
			if (stopwords.isTokenAStopword(token))
			{
				stopTokens.add(token);
				continue;
			}
			newTransitions.clear();
			trans2remove.clear();
			countNotStopword++;

			Collection<SynAlgos> synAlgos = synsProvider.getSynonyms(tokens, token, transitions);

			for (StateTransition trans : transitions)
			{
				if (trans.isObsolete(countNotStopword, w))
				{
					trans2remove.add(trans);
					continue;
				}

				for (SynAlgos synAlgo : synAlgos)
				{
					INode node = trans.getNode().gotoNode(synAlgo.getSynToken());
					if (node == EmptyNode.EMPTYNODE)
						continue;
					StateTransition nextTrans = new StateTransition(trans, node, token, synAlgo.getAlgos(),
							countNotStopword);
					newTransitions.add(nextTrans);
					if (node.isAfinalState())
					{
						IAnnotation annotation = StrategyUtils.createAnnot(nextTrans, stopTokens);
						annots.add(annotation);
					}
				}
			}
			for (StateTransition trans : trans2remove)
			{
				transitions.remove(trans);
			}
			for (StateTransition trans : newTransitions)
			{
				if (transitions.contains(trans))
					transitions.remove(trans);
				transitions.add(trans);
			}
		}
		annots.sort(Comparator.naturalOrder());
		return annots;
	}

}
