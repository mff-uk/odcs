package cz.cuni.mff.xrg.odcs.dataunit.file.impl;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link FileDataUnit}. Support lazy merge. So the merge does
 * not actually copy the files but just add the merger directory into the list
 * of directories. If the merged directory is deleter then the data from given
 * {@link FileDataUnit} are lost.
 *
 * @author Petyr
 */
class FileDataUnitImpl implements ManageableFileDataUnit {

	private static final Logger LOG = LoggerFactory.getLogger(FileDataUnitImpl.class);

	/**
	 * True if in read only mode.
	 */
	private boolean isReadOnly = false;

	/**
	 * Name of this {@link FileDataUnitImpl}.
	 */
	private final String name;

	/**
	 * Directory used to create this data unit. The new files are added into
	 * this directory.
	 */
	private final File mainDirectory;

	/**
	 * Directories that are used by this {@link FileDataUnitImpl}.
	 */
	private final LinkedList<File> directories = new LinkedList<>();

	/**
	 * List of files in {@link #directories}.
	 */
	private final HashSet<FileHandler> fileHandlers = new HashSet<>();

	FileDataUnitImpl(final String name, final File directory) {
		this.name = name;
		this.mainDirectory = directory;
		// add the directory
		addDirectory(directory);
	}

	/**
	 * Analyze given directory and return {@link FileHandler} for files in given
	 * directory.
	 *
	 * @param directory
	 */
	private LinkedList<FileHandler> analyzeDirectory(final File directory) {
		final LinkedList<FileHandler> result = new LinkedList<>();
		final Collection<File> files = FileUtils.listFiles(directory, null, true);
		for (File file : files) {
			// add to the list as handler
			result.add(new FileHandlerImpl(file, file.getName()));
		}
		return result;
	}

	/**
	 * Add files from given directory into {@link #fileHandlers} and the
	 * directory into the {@link #directories}.
	 *
	 * @param directory
	 */
	private void addDirectory(final File directory) {
		// if directory does not exist then create it
		if (!directory.exists()) {
			// the directory does not exist, nothing to add
			return;
		}
		if (this.directories.contains(directory)) {
			// already added
		} else {
			// add to the list of directories
			this.directories.add(directory);
		}
		// add files into the fileHandlers
		this.fileHandlers.addAll(analyzeDirectory(directory));
	}

	@Override
	public FileHandler create(final String name, boolean create) throws DataUnitException {
		if (isReadOnly) {
			// read only, no action
			return null;
		}

		// if directory does not exist then create it
		if (!mainDirectory.exists()) {
			mainDirectory.mkdirs();
		}

		File newFile = new File(mainDirectory, name);
		FileHandlerImpl handler = new FileHandlerImpl(newFile, name);
		if (create) {
			try {
				// create file
				FileUtils.touch(newFile);
			} catch (IOException ex) {
				LOG.error("Failed to create file.", ex);
				throw new FileCreationException(ex);
			}
		}
		fileHandlers.add(handler);
		return handler;
	}

	@Override
	public FileHandler add(File file, boolean asLink) throws DataUnitException {
		if (asLink) {
			// ok just add
		} else {
			// copy the file to the local repository and create handler to it
		}
		return null;
	}
	
	@Override
	public void delete(FileHandler handler) throws DataUnitException {
		if (isReadOnly) {
			// read only, no action
			return;
		}

		if (fileHandlers.contains(handler)) {
			// remove from the list
			fileHandlers.remove(handler);
		}

		if (!handler.asFile().exists()) {
			// the file does not exist
		} else {
			// delete the file
			try {
				FileUtils.forceDelete(handler.asFile());
			} catch (IOException ex) {
				throw new FileDeletionException(ex);
			}
		}
	}

	@Override
	public DataUnitType getType() {
		return DataUnitType.FILE;
	}

	@Override
	public String getDataUnitName() {
		return name;
	}

	@Override
	public Iterator<FileHandler> iterator() {
		// return iterator to stored list
		return this.fileHandlers.iterator();
	}

	@Override
	public void madeReadOnly() {
		isReadOnly = true;
	}

	@Override
	public void merge(DataUnit unit) throws IllegalArgumentException {
		if (unit instanceof FileDataUnitImpl) {
			FileDataUnitImpl fileUnit = (FileDataUnitImpl)unit;
			// add every data unit
			for (File directory : fileUnit.directories) {
				addDirectory(directory);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void delete() {
		// delete stored handlers
		directories.clear();
		fileHandlers.clear();
		// delete file on hdd
		try {
			FileUtils.deleteDirectory(mainDirectory);
		} catch (IOException ex) {
			LOG.error("Failed to delete the main directory.", ex);
		}
	}

	@Override
	public void release() {
		// we hold no locks, nothing to be done here
	}

	@Override
	public void clean() {
		// delete stored handlers
		directories.clear();
		fileHandlers.clear();
		// delete file on hdd
		try {
			FileUtils.cleanDirectory(mainDirectory);
		} catch (IOException ex) {
			LOG.error("Failed to clean the main directory.", ex);
		}
	}

	@Override
	public void save(File directory) throws RuntimeException {
		// all the data are alredy on the harddrive
	}

	@Override
	public void load(File directory) throws FileNotFoundException, RuntimeException {
		// just add the directory
		addDirectory(directory);
	}

	@Override
	public boolean isReadOnly() {
		return isReadOnly;
	}

}
