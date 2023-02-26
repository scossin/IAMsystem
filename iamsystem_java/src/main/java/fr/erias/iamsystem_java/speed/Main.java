package fr.erias.iamsystem_java.speed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.EncoderException;

import fr.erias.iamsystem_java.keywords.Entity;
import fr.erias.iamsystem_java.keywords.IEntity;
import fr.erias.iamsystem_java.keywords.IKeyword;
import fr.erias.iamsystem_java.keywords.Terminology;
import fr.erias.iamsystem_java.matcher.IAnnotation;
import fr.erias.iamsystem_java.matcher.Matcher;
import fr.erias.iamsystem_java.stopwords.NoStopwords;
import fr.erias.iamsystem_java.tokenize.ETokenizer;
import fr.erias.iamsystem_java.tokenize.IToken;
import fr.erias.iamsystem_java.tokenize.ITokenizer;
import fr.erias.iamsystem_java.tokenize.TokenizerFactory;

public class Main
{
	private final static String FOLDER = "/media/cossin/5980c25d-cf59-4fca-b649-c8c2f241fb1c/home/cossin15072019/Documents/DetectTerms/Detector/IAMsystemTerminos/src/main/resources/UMLS/";
	private final static String filename = FOLDER + "full_umls.tsv";

	public static Iterable<IKeyword> getTerminology(String filename) throws IOException
	{
		InputStream in = new FileInputStream(new File(filename)); // small terminology containing about 9400 terms
		String sep = "\t";
		int colLabel = 1;
		int colCode = 0;
		Iterable<IKeyword> terminology = Main.getUMLS(in, sep, colLabel, colCode, true);
		return (terminology);
	}

	public static Iterable<IKeyword> getUMLS(InputStream in, String sep, int colLabel, int colCode, boolean header)
			throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		if (header)
			br.readLine();
		Terminology termino = new Terminology();
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] columns = line.split(sep);
			String label = removeQuotes(columns[colLabel]);
			String kbid = removeQuotes(columns[colCode]);
			IEntity entity = new Entity(label, kbid);
			termino.addKeyword(entity);
		}
		br.close();
		return termino;
	}

	public static void main(String[] args) throws IOException, EncoderException
	{
		Main main = new Main();
		main.speedTest();
	}

	private static String removeQuotes(String label)
	{
		label = label.replaceAll("^\"|\"$", "");
		return (label);
	}

	private Matcher matcher;

	private ITokenizer<IToken> tokenizer;

	public Main() throws IOException, EncoderException
	{
		this.tokenizer = TokenizerFactory.getTokenizer(ETokenizer.FRENCH);
		matcher = new Matcher<IToken>(tokenizer, new NoStopwords());
		matcher.addKeyword(getTerminology(Main.filename));
		matcher.setRemoveNestedAnnot(false);
		matcher.setW(100);
	}

	// syndrome Triple X
	public void speedTest() throws IOException
	{
		File inputFolder = new File(
				"/media/cossin/5980c25d-cf59-4fca-b649-c8c2f241fb1c/workspace/DBpedia/wikipedia_articles");
		TxtFiles txtFiles = new TxtFiles(inputFolder);
		Iterator<File> iter = txtFiles.getFileIterator();
		iter.next();
		int count = 0;
		// Writer w = new PrintWriter(new File("output.txt"));
		long startTimeAll = System.nanoTime();
		System.out.println(startTimeAll);
		while (iter.hasNext())
		{
			File file = iter.next();
//			if (!file.getName().equals("Placenta_praevia.txt"))
//			{
//				continue;
//			}
			//System.out.println(file.getName());
			String content = Files.readString(file.toPath(), Charset.defaultCharset());
			long startTime = System.nanoTime();
			List<IAnnotation<IToken>> anns = matcher.annot(content);
			// for (CTcode ct : output.getCTcodes()) {
			// System.out.println(ct.toString());
			// w.write(ct.toString());
			// }
			long endTime = System.nanoTime();
			long totalTime = endTime - startTime;
			// double mem = (double) (Runtime.getRuntime().totalMemory() -
			// Runtime.getRuntime().freeMemory() / (1024*1024));
			System.out.println(file.getName() + "\t" + anns.size());
//			if (count == 1000)
//				break;
//			count++;
		}
		long endTimeAll = System.nanoTime();
		long totalTime = endTimeAll - startTimeAll;
		System.out.println(endTimeAll);
		System.out.println("KB: "
				+ (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));
		System.out.println(totalTime / 1000000);
	}
}
