package fr.erias.iamsystem_java.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.erias.iamsystem_java.brat.BratDocument;
import fr.erias.iamsystem_java.brat.BratWriter;
import fr.erias.iamsystem_java.brat.IBratTypeF;
import fr.erias.iamsystem_java.brat.IBratWriterF;
import fr.erias.iamsystem_java.keywords.Entity;
import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.matcher.MatcherBuilder;

class BratDocumentTest
{

	@BeforeEach
	void setUp() throws Exception
	{
	}

	@Test
	void testBratDocKeyword()
	{
		IBratTypeF<Entity> getKbId = (entity) -> entity.kbid();
		Entity ent1 = new Entity("North America", "CONTINENT");
		Matcher matcher = new MatcherBuilder().keywords(ent1).w(3).build();
		List<IAnnotation> annots = matcher.annot("North and South America");
		assertEquals(1, annots.size());
		BratDocument<Entity> document = new BratDocument<Entity>();
		document.addAnnots(annots, getKbId);
		assertEquals(document.toString(),
				"T1\tCONTINENT 0 5;16 23\tNorth America\n#1\tIAMSYSTEM T1\tNorth America (CONTINENT)");
	}

	@Test
	void testBratDocument()
	{
		Entity ent1 = new Entity("North America", "NA");
		Matcher matcher = new MatcherBuilder().keywords(ent1).w(3).build();
		List<IAnnotation> annots = matcher.annot("North and South America");
		assertEquals(1, annots.size());
		BratDocument<Entity> document = new BratDocument<Entity>();
		document.addAnnots(annots, "CONTINENT");
		assertEquals(document.toString(),
				"T1\tCONTINENT 0 5;16 23\tNorth America\n#1\tIAMSYSTEM T1\tNorth America (NA)");
	}

	@Test
	void testBratWriter()
	{
		class WriterTest implements IBratWriterF
		{
			public ArrayList<String> annLines = new ArrayList<String>();

			@Override
			public void write(String annLine)
			{
				annLines.add(annLine);
			}

		}
		WriterTest writer = new WriterTest();
		Entity ent1 = new Entity("North America", "NA");
		Matcher matcher = new MatcherBuilder().keywords(ent1).w(3).build();
		List<IAnnotation> annots = matcher.annot("North and South America");
		assertEquals(1, annots.size());
		BratDocument<Entity> document = new BratDocument<Entity>();
		document.addAnnots(annots, "CONTINENT");
		BratWriter.saveDocuentNotes(document, writer);
		String filecontent = writer.annLines.stream().collect(Collectors.joining("\n"));
		assertEquals(filecontent, "T1\tCONTINENT 0 5;16 23\tNorth America\n#1\tIAMSYSTEM T1\tNorth America (NA)");
	}
}
