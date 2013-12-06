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
	
	public Directory(File root) {
		this.root = root;
	}
	
	/**
	 * Return path the file in the current directory.
	 * @param fileName File name.
	 * @return
	 */
	public File file(String fileName) {
		return new File(root, fileName);
	}

	/**
	 * Create sub-directory in current directory.
	 * @param dirName
	 * @return
	 */
	public Directory directory(String dirName) {
		File result = new File(root, dirName);
		result.mkdirs();
		return new Directory(result);
	}
}
