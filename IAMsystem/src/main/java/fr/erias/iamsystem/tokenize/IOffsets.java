package fr.erias.iamsystem.tokenize;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Store the start and end offsets of a token.
 *
 * @author Sebastien Cossin
 */
public interface IOffsets
{

	/**
	 * Get maximum end value.
	 *
	 * @param offsets A collection of objects having (start,end) values.
	 * @return The maximum end value.
	 */
	public static int getMaxEnd(Collection<? extends IOffsets> offsets)
	{
		return offsets.stream().map(o -> o.end()).reduce(Math::max).get();
	}

	/**
	 * Get minimum start value.
	 *
	 * @param offsets A collection of objects having (start,end) values.
	 * @return The minimum start value.
	 */
	public static int getMinStart(Collection<? extends IOffsets> offsets)
	{
		return offsets.stream().map(o -> o.start()).reduce(Math::min).get();
	}

	/**
	 * Create a unique id for a sequence of offsets
	 *
	 * @param offsets A collection of objects having (start,end) values.
	 * @return A String id.
	 */
	public static String getSpanSeqId(Collection<? extends IOffsets> offsets)
	{
		return offsets.stream().map(o -> String.format("(%d,%d)", o.start(), o.end())).collect(Collectors.joining(";"));
	}

	/**
	 * True if a and b have indices in common in their range (start:end).
	 *
	 * @param a first Offset
	 * @param b second Offset
	 * @return True if overlaps.
	 */
	public static boolean offsetsOverlap(IOffsets a, IOffsets b)
	{
		if (a == b)
			return false;
		return (b.start() <= a.start() && a.start() <= b.end()) || a.start() <= b.start() && b.start() <= a.end();
	}

	/**
	 * End-offset is the index of the last character **+ 1**, that is to say the
	 * first character to exclude from the returned substring when slicing with
	 * [start:end]
	 *
	 * @return end value.
	 */
	public int end();

	/**
	 * Start-offset is the index of the first character.
	 *
	 * @return start value.
	 */
	public int start();
}
