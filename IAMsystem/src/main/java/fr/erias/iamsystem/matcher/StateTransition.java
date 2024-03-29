package fr.erias.iamsystem.matcher;

import java.util.Collection;

import fr.erias.iamsystem.tokenize.IToken;
import fr.erias.iamsystem.tree.INode;

/**
 * Utility class to keep track of state transitions during a matching strategy.
 *
 * @author Sebastien Cossin
 *
 */
public class StateTransition
{

	/**
	 * Create the first transition with the initial state.
	 *
	 * @param initialState In general the root node of the trie.
	 * @return A transition with no parent.
	 */
	public static StateTransition createFristTrans(INode initialState)
	{
		return new StateTransition(null, initialState, null, null, -1);
	}

	/**
	 * Check if a transition is the first transition (no previous transition).
	 *
	 * @param trans a transition is check.
	 * @return True if it's the first.
	 */
	public static boolean isFirstTrans(StateTransition trans)
	{
		return trans.previousTrans == null;
	}

	private final INode node;
	private final StateTransition previousTrans;
	private final Collection<String> algos;
	private final IToken token;
	private final int countNotStopword;

	/**
	 *
	 * @param previousTrans    the previous transition.
	 * @param node             the current state.
	 * @param token            A document's token.
	 * @param algos            the algorthim(s) that matched the token to the node.
	 * @param countNotStopword token ith position minus the number of stopwords.
	 */
	public StateTransition(StateTransition previousTrans, INode node, IToken token, Collection<String> algos,
			int countNotStopword)
	{
		this.node = node;
		this.previousTrans = previousTrans;
		this.algos = algos;
		this.token = token;
		this.countNotStopword = countNotStopword;
	}

	@Override
	public boolean equals(Object o)
	{
		StateTransition c = (StateTransition) o;
		return this.node.getNodeNumber() == c.node.getNodeNumber();
	}

	/**
	 * Get algorithms.
	 *
	 * @return the algorthim(s) that matched the token to the node.
	 */
	public Collection<String> getAlgos()
	{
		return algos;
	}

	/**
	 * Get countNotStopword value.
	 *
	 * @return countNotStopword
	 */
	public int getCountNotStopword()
	{
		return countNotStopword;
	}

	/**
	 * Get transition id.
	 *
	 * @return the node number.
	 */
	public int getId()
	{
		return this.node.getNodeNumber();
	}

	/**
	 * Get the node/state.
	 *
	 * @return the current node of this state transition.
	 */
	public INode getNode()
	{
		return node;
	}

	public StateTransition getPreviousTrans()
	{
		return previousTrans;
	}

	/**
	 * Get document's token.
	 *
	 * @return document's token
	 */
	public IToken getToken()
	{
		return token;
	}

	@Override
	public int hashCode()
	{
		return (this.node.getNodeNumber());
	}

	/**
	 * Check if a state transition is obsolete.
	 *
	 * @param countNotStopWord The current ith token that is not a stopword.
	 * @param w                the window parameter.
	 * @return True if this transition is too far from the current token.
	 */
	public boolean isObsolete(int countNotStopWord, int w)
	{
		if (isFirstTrans(this))
			return false;
		int distance2currentToken = countNotStopWord - this.countNotStopword;
		return w - distance2currentToken < 0;
	}
}
