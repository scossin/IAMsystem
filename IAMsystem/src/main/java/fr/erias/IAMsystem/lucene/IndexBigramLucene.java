package fr.erias.IAMsystem.lucene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.load.Loader;

/**
 * Create a Lucene Index to detect typos
 * 
 * DON T FORGET TO PUT THE INDEX IN THE RESSOURCE FOLDER !!
 * 
 * @author Cossin Sebastien (cossin.sebastien@gmail.com)
 *
 */
public class IndexBigramLucene {

	final static Logger logger = LoggerFactory.getLogger(IndexBigramLucene.class);

	/**
	 * Get unique TokenBigram to index in Lucene in order to detect typo
	 * 
	 * @param uniqueTokensBigram A map between the collapse form and the uncollapse form (ex "meningoencephalite, meningo encephalite"). Use {@link Loader}
	 * @param indexFolder Path to create Lucene Index
	 * @param concatenatedField The name of the Lucene that contains the concatenated form (ex : "meningoencephalite"
	 * @param bigramField The name of the Lucene field that contains the bigram form (ex : "meningo encephalite")
	 * @throws IOException If the index can't be created
	 */
	public static void IndexLuceneUniqueTokensBigram(HashMap<String,String> uniqueTokensBigram, File indexFolder, String concatenatedField, String bigramField) throws IOException {
		// Indexing in Lucene
		logger.info("Indexing Terminology...");
		Directory directory = FSDirectory.open(indexFolder.toPath());
		IndexWriterConfig config = new IndexWriterConfig();
		config.setOpenMode(OpenMode.CREATE);    //config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		IndexWriter iwriter = new IndexWriter(directory, config);
		int counter = 0;
		Iterator<Entry<String, String>> iter = uniqueTokensBigram.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			String collapse = entry.getKey();
			String bigram = entry.getValue();
			Document doc = new Document();
			IndexableField indexableField = new StringField (concatenatedField, collapse, Field.Store.YES);
			doc.add(indexableField);
			indexableField = new StringField (bigramField, bigram, Field.Store.YES);
			doc.add(indexableField);
			iwriter.addDocument(doc); // write the document to the index
			counter ++ ;
		}
		logger.info("number of lines indexed : " + counter);
		iwriter.close();
		directory.close();
	}
}
