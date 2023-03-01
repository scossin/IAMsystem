package fr.erias.iamsystem_java.brat;

import java.util.stream.Collectors;

public class NoteFunctions
{

	public static INoteF keywordsToString = (
			annot) -> annot.getKeywords().stream().map(k -> k.toString()).collect(Collectors.joining("\n"));
}
