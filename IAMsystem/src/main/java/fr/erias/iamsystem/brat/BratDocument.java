package fr.erias.iamsystem.brat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.erias.iamsystem.annotation.IAnnotation;
import fr.erias.iamsystem.annotation.IPrintAnnot;
import fr.erias.iamsystem.keywords.IKeyword;

/**
 * Class representing a Brat Document containing Brat's annotations, namely Brat
 * Entity and Brat Note in this package. A BratDocument should be linked to a
 * single text document. Entities and notes can be serialized in a text file
 * with 'ann' extension, one per line. See https://brat.nlplab.org/standoff.html
 *
 * @author Sebastien Cossin
 *
 * @param <T> Keyword type.
 */
public class BratDocument<T extends IKeyword>
{

	private final List<BratEntity> bratEntities = new ArrayList<BratEntity>();
	private final List<BratNote> bratNotes = new ArrayList<BratNote>();
	private INoteF noteFunction = (annot) -> IPrintAnnot.keywords2Str(annot);
	private IBratFormatterF formatter;

	/**
	 * Create a BratDocument to serialize annotations to Brat ann format.
	 */
	public BratDocument()
	{
		this.formatter = BratFormatters.defaultFormatter;
	}

	/**
	 * Create a BratDocument to serialize annotations to Brat ann format.
	 *
	 * @param formatter The Brat formatter that generates Brat span.
	 */
	public BratDocument(IBratFormatterF formatter)
	{
		this.formatter = formatter;
	}

	/**
	 * Add iamsystem annotations to convert them to Brat format.
	 *
	 * @param annot    matcher annotation output.
	 * @param bratType A string, the Brat entity type.
	 */
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

	/**
	 * Add iamsystem annotations to convert them to Brat format.
	 *
	 * @param annots      matcher output.
	 * @param bratTypeFun A function to retrieve dynamically the brat type from an
	 *                    annotation.
	 */
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

	/**
	 * Add iamsystem annotations to convert them to Brat format.
	 *
	 * @param annots   matcher output.
	 * @param bratType A Brat entity type for all the annotations.
	 */
	public void addAnnots(Iterable<IAnnotation> annots, String bratType)
	{
		for (IAnnotation annot : annots)
		{
			addAnnot(annot, bratType);
		}
	}

	/**
	 * Convert Brat entities to String.
	 *
	 * @return Lines of Brat entities.
	 */
	public String entitiesToString()
	{
		return bratEntities.stream().map(ent -> ent.toString()).collect(Collectors.joining("\n"));
	}

	/**
	 * Get the list of Brat entities.
	 *
	 * @return Brat entities added to this document.
	 */
	public List<BratEntity> getBratEntities()
	{
		return bratEntities;
	}

	/**
	 * Get the list of Brat notes.
	 *
	 * @return Brat notes added to this document.
	 */
	public List<BratNote> getBratNotes()
	{
		return bratNotes;
	}

	private String getEntityId()
	{
		return String.format("T%d", this.bratEntities.size() + 1);
	}

	/**
	 * getNoteFunction
	 *
	 * @return the function that converts an annotation to a Brat Note.
	 */
	public INoteF getNoteFunction()
	{
		return noteFunction;
	}

	private String getNoteId()
	{
		return String.format("#%d", this.bratNotes.size() + 1);
	}

	/**
	 * Convert Brat notes to String.
	 *
	 * @return Lines of Brat note.
	 */
	public String notesToString()
	{
		return bratNotes.stream().map(ent -> ent.toString()).collect(Collectors.joining("\n"));
	}

	/**
	 * Change the function that converts an annotation to a Brat Note.
	 *
	 * @param noteFunction a {@link INoteF}.
	 */
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
