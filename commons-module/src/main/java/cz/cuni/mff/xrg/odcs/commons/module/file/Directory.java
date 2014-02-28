package cz.cuni.mff.xrg.odcs.commons.module.file;

import java.io.File;

/**
 * Class for easier management of DPU's files. FileStorage represent a 
 * directory, where DPU can store it's files. 
 * 
 * @author Petyr
 */
public final class Directory {

	/**
	 * Root path.
	 */
	private final File root;
	
	/**
	 * Create a new directory wrap for given directory.
	 * 
	 * @param root Directory to wrap.
	 */
	public Directory(File root) {
		this.root = root;
	}
	
	/**
	 * Return path to the file of given name in this directory.
	 * 
	 * @param fileName File name.
	 * @return Absolute path the file in the current directory.
	 */
	public File file(String fileName) {
		return new File(root, fileName);
	}

	/**
	 * Create sub-directory in current directory.
	 * 
	 * @param dirName Name of directory.
	 * @return Representation of created sub-directory.
	 */
	public Directory directory(String dirName) {
		File result = new File(root, dirName);
		result.mkdirs();
		return new Directory(result);
	}
}
