package fr.erias.iamsystem_java.tokenize;

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
