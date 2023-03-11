package fr.erias.iamsystem_java.brat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.erias.iamsystem_java.annotation.IAnnotation;
import fr.erias.iamsystem_java.keywords.IKeyword;

public class BratDocument<T extends IKeyword>
{

	private final List<BratEntity> bratEntities = new ArrayList<BratEntity>();
	private final List<BratNote> bratNotes = new ArrayList<BratNote>();
	private INoteF noteFunction = NoteFunctions.keywordsToString;
	private IBratFormatterF formatter;

	public BratDocument()
	{
		this.formatter = BratFormatters.defaultFormatter;
	}

	public BratDocument(IBratFormatterF formatter)
	{
		this.formatter = formatter;
	}

	public void addAnnot(IAnnotation annot, String bratType)
	{
		BratFormat bratFormat = this.formatter.getFormat(annot);
		String entityId = getEntityId();
		BratEntity entity = new BratEntity(entityId, bratType, bratFormat.getOffsets(),
				bratFormat.getTextEscapeNewLine());
		bratEntities.add(entity);
		BratNote bratNote = new BratNote(getNoteId(), entityId, noteFunction.getNote(annot));
		bratNotes.add(bratNote);
	}

	public void addAnnots(Iterable<IAnnotation> annots, IBratTypeF<T> bratTypeFun)
	{
		for (IAnnotation annot : annots)
		{
			@SuppressWarnings("unchecked")
			T firstKeyword = (T) annot.getKeywords().iterator().next();
			String bratType = bratTypeFun.getBratType(firstKeyword);
			addAnnot(annot, bratType);
		}
	}

	public void addAnnots(Iterable<IAnnotation> annots, String bratType)
	{
		for (IAnnotation annot : annots)
		{
			addAnnot(annot, bratType);
		}
	}

	public String entitiesToString()
	{
		return bratEntities.stream().map(ent -> ent.toString()).collect(Collectors.joining("\n"));
	}

	public List<BratEntity> getBratEntities()
	{
		return bratEntities;
	}

	public List<BratNote> getBratNotes()
	{
		return bratNotes;
	}

	private String getEntityId()
	{
		return String.format("T%d", this.bratEntities.size() + 1);
	}

	public INoteF getNoteFunction()
	{
		return noteFunction;
	}

	private String getNoteId()
	{
		return String.format("#%d", this.bratNotes.size() + 1);
	}

	public String notesToString()
	{
		return bratNotes.stream().map(ent -> ent.toString()).collect(Collectors.joining("\n"));
	}

	public void setNoteFunction(INoteF noteFunction)
	{
		this.noteFunction = noteFunction;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(entitiesToString());
		sb.append("\n");
		sb.append(notesToString());
		return sb.toString().strip(); // stip in case lists are empty.
	}
}
