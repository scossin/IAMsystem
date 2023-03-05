package fr.erias.iamsystem_java.matcher.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.matcher.Annotation;
import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.LinkedState;
import fr.erias.iamsystem_java.matcher.Span;
import fr.erias.iamsystem_java.tokenize.IOffsets;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tree.INode;

public class StrategyUtils
{
	/**
	 * Main function to create an {@link Annotation}
	 *
	 * @param last_el    the last linked state that stores a final state containing
	 *                   keywords.
	 * @param stopTokens The list of stopwords tokens detected in the document.
	 * @return An annotation.
	 */
	public static Annotation createAnnot(LinkedState last_el, List<IToken> stopTokens)
	{
		List<LinkedState> transStates = toList(last_el);
		INode lastState = last_el.getNode();
		List<IToken> tokens = transStates.stream().map(t -> t.getToken()).collect(Collectors.toList());
		tokens.sort(Comparator.naturalOrder());
		List<Collection<String>> algos = transStates.stream().map(t -> t.getAlgos()).collect(Collectors.toList());
		return new Annotation(tokens, algos, lastState, stopTokens);
	}

	/**
	 * Create the initial state used in each detection sequence.
	 *
	 * @param initialState In general the root node of the trie.
	 * @return A linked state with no parent.
	 */
	public static LinkedState createStartState(INode initialState)
	{
		return new LinkedState(null, initialState, null, null, -1);
	}

	/**
	 * In case of two nested annotations, remove the shorter one. For example, if we
	 * have "prostate" and "prostate cancer" annnotations, "prostate" annotation is
	 * removed.
	 *
	 * @param annots        a list of annotations.
	 * @param keepAncestors Whether to keep the nested annotations that are
	 *                      ancestors and remove only other cases.
	 * @return a filtered list of annotations.
	 */
	public static List<IAnnotation> rmNestedAnnots(List<IAnnotation> annots, boolean keepAncestors)
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

	/**
	 * Convert a linked list to a list.
	 * 
	 * @param last_el the last linked state that stores a final state containing
	 *                keywords.
	 * @return A list of {@link LinkedState}.
	 */
	private static List<LinkedState> toList(LinkedState last_el)
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
