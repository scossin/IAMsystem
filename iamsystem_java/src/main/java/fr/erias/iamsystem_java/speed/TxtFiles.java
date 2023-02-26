package fr.erias.iamsystem_java.speed;

import java.io.File;
import java.util.Iterator;

class RCPiterator implements Iterator<File>
{

	private File[] rcpFiles;
	private int i = 0;

	public RCPiterator(File[] rcpFiles)
	{
		this.rcpFiles = rcpFiles;
	}

	@Override
	public boolean hasNext()
	{
		// TODO Auto-generated method stub
		return i < rcpFiles.length;
	}

	@Override
	public File next()
	{
		File rcpFile = rcpFiles[i];
		i += 1;
		return rcpFile;
	}
}

/**
 * Iterates over HTML files
 *
 * @author Sebastien Cossin
 *
 */
public class TxtFiles
{

	public static void checkFolderExists(File folder)
	{
		if (!folder.isDirectory())
		{
			throw new NullPointerException("folder" + folder + "doesn't exist");
		}
		return;
	}

	private File[] rcpFiles;

	public TxtFiles(File inputFolder)
	{
		checkFolderExists(inputFolder);
		this.rcpFiles = inputFolder.listFiles();
	}

	public Iterator<File> getFileIterator()
	{
		// TODO Auto-generated method stub
		return new RCPiterator(this.rcpFiles);
	}
}