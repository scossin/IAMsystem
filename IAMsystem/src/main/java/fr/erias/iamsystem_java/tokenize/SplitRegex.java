package fr.erias.iamsystem_java.tokenize;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenize with a regular expression.
 *
 * @author Cossin Sebastien
 */
public class SplitRegex implements ISplitF
{

	private final Pattern compiled_pattern;

	/**
	 * Create a split function with a regular expression.
	 *
	 * @param pattern A valid regex.
	 */
	public SplitRegex(String pattern)
	{
		this.compiled_pattern = Pattern.compile(pattern);
	}

	@Override
	public List<IOffsets> split(String text)
	{
		List<IOffsets> offsets = new ArrayList<IOffsets>();
		Matcher matcher = this.compiled_pattern.matcher(text);
		while (matcher.find())
		{
			offsets.add(new Offsets(matcher.start(), matcher.end()));
		}
		return offsets;
	}
}
