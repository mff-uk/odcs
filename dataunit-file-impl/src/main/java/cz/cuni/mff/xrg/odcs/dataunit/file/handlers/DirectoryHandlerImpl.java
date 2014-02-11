package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitAccessException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.dataunit.file.options.OptionsAdd;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ManageableDirectoryHandler}.
 *
 * @author Petyr
 */
public class DirectoryHandlerImpl implements ManageableDirectoryHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DirectoryHandlerImpl.class);

	/**
	 * Directory name.
	 */
	private String name;

	/**
	 * Path to the represented directory.
	 */
	private File directory;

	/**
	 * Root of the respective {@link FileDataUnit}.
	 */
	private File rootDir;
	
	/**
	 * User data.
	 */
	private String userData;

	/**
	 * True if in read only mode.
	 */
	private boolean isReadOnly;

	/**
	 * True if the represent file is in link mode. Ie. it is not located in
	 * DataUnit directory.
	 */
	private boolean isLink;

	/**
	 * List of stored handlers.
	 */
	private LinkedList<ManageableHandler> handlers;

	/**
	 * Create root handler for given directory. The given directory should be 
	 * empty.
	 *
	 * @param directory
	 */
	public DirectoryHandlerImpl(File directory) {
		this.name = directory.getName();
		this.directory = directory;
		this.rootDir = directory;
		this.userData = null;
		this.isReadOnly = false;
		this.isLink = false;
		this.handlers = new LinkedList<>();
		// try to create a directory
		this.directory.mkdirs();
	}

	/**
	 * Create new handler for existing directory. Also scan and add it's content
	 * recursively.
	 *
	 * @param directory
	 * @param root of the respective {@link FileDataUnit}
	 * @param name
	 * @param isLink
	 */
	private DirectoryHandlerImpl(File directory, File root, String name, boolean isLink) {
		// set fields
		this.name = name;
		this.directory = directory;
		this.rootDir = root;
		this.userData = null;
		this.isReadOnly = false;
		this.isLink = isLink;
		this.handlers = new LinkedList<>();
		// create a directory
		directory.mkdirs();
		// scan the directory
		scanDirectory();
	}

	@Override
	public String getRootedPath() {
        Path pathAbsolute = Paths.get(directory.toURI());
        Path pathBase = Paths.get(rootDir.toURI());
        Path pathRelative = pathBase.relativize(pathAbsolute);
		return pathRelative.toString();		
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isReadOnly() {
		return this.isReadOnly;
	}

	@Override
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
		for (ManageableHandler handler : handlers) {
			handler.setReadOnly(isReadOnly);
		}
	}

	@Override
	public boolean isLink() {
		return this.isLink;
	}

	@Override
	public File asFile() {
		return this.directory;
	}

	@Override
	public void setUserData(String newUserData) {
		this.userData = newUserData;
	}

	@Override
	public String getUserData() {
		return this.userData;
	}

	@Override
	public FileHandler addNewFile(String name) throws DataUnitException {
		accessCheck();

		// check existance
		ManageableHandler existing = getManageableByName(name);
		if (existing == null) {
			// ok, prepare path to file
			final File newFilePath = new File(this.directory, name);
			// create file handler
			final FileHandlerImpl newFile
					= new FileHandlerImpl(newFilePath, rootDir, name, false);
			this.handlers.add(newFile);
			return newFile;
		} else {
			// already exists
			if (existing instanceof FileHandler) {
				return (FileHandler) existing;
			} else {
				// it's directory
				return null;
			}
		}
	}

	@Override
	public FileHandler addExistingFile(File file, OptionsAdd options)
			throws DataUnitException {
		accessCheck();

		final String newName = file.getName();
		ManageableHandler existing = getManageableByName(newName);
		if (existing != null) {
			if (existing instanceof FileHandler) {
				// ok we can work with this
			} else {
				// it's not a file
				return null;
			}

			if (!options.overwrite()) {
				// already exists and we should not overwrite
				return (FileHandler) existing;
			} else {
				// ok we can overwrite file -> so remove the old one
				remove(existing);
			}
		}
		// if we are here the file does not exist, and we may add the new one
		if (options.isLink()) {
			// will be added as link
		} else {
			// we need to copy this first
			final File newFile = new File(this.directory, newName);
			try {
				FileUtils.copyFile(file, newFile);
			} catch (IOException ex) {
				throw new CopyFailed(ex);
			}
			file = newFile;
		}
		// now in file is the link to file for which we want to create handler
		FileHandlerImpl newHandler
				= new FileHandlerImpl(file, rootDir, newName, options.isLink());
		this.handlers.add(newHandler);
		return newHandler;
	}

	@Override
	public DirectoryHandler addNewDirectory(String name)
			throws DataUnitException {
		accessCheck();

		// check existance
		ManageableHandler existing = getManageableByName(name);
		if (existing == null) {
			// ok, prepare path to file
			final File newFilePath = new File(this.directory, name);
			// create dir handler
			final DirectoryHandlerImpl newDir
					= new DirectoryHandlerImpl(newFilePath, rootDir, name, false);
			this.handlers.add(newDir);
			return newDir;
		} else {
			// already exists
			if (existing instanceof DirectoryHandler) {
				return (DirectoryHandler) existing;
			} else {
				// it's directory
				return null;
			}
		}
	}

	@Override
	public DirectoryHandler addExistingDirectory(File directory, OptionsAdd options)
			throws DataUnitException {
		accessCheck();

		final String newName = directory.getName();
		ManageableHandler existing = getManageableByName(newName);
		if (existing != null) {
			if (existing instanceof DirectoryHandler) {
				// ok we can work with this
			} else {
				// it's not a file
				return null;
			}

			if (!options.overwrite()) {
				// already exists and we should not overwrite
				return (DirectoryHandler) existing;
			} else {
				// ok we can overwrite file -> so remove the old one
				remove(existing);
			}
		}
		// if we are here the file does not exist, and we may add the new one
		if (options.isLink()) {
			// will be added as link
		} else {
			// we need to copy this first
			final File newDirectory = new File(this.directory, newName);
			try {
				FileUtils.copyDirectory(directory, newDirectory);
			} catch (IOException ex) {
				throw new CopyFailed(ex);
			}
			directory = newDirectory;
		}
		// now in file is the link to file for which we want to create handler
		DirectoryHandlerImpl newHandler
				= new DirectoryHandlerImpl(directory, rootDir, newName, options.isLink());
		this.handlers.add(newHandler);
		return newHandler;
	}

	@Override
	public boolean add(Handler e) {
		return add(e, new OptionsAdd());
	}

	@Override
	public boolean add(Handler e, OptionsAdd options) {
		Handler newHandler;
		try {
			if (e instanceof ManageableFileHandler) {
				final ManageableFileHandler fileHandler = (ManageableFileHandler) e;
				newHandler = addExistingFile(fileHandler.asFile(), options);
			} else if (e instanceof ManageableDirectoryHandler) {
				final ManageableDirectoryHandler dirHandler
						= (ManageableDirectoryHandler) e;
				newHandler = addExistingDirectory(dirHandler.asFile(), options);
			} else {
				// unknown ..
				return false;
			}
		} catch (DataUnitException ex) {
			LOG.error("Failed to add existing handler.", ex);
			return false;
		}
		newHandler.setUserData(e.getUserData());
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Handler> c) {
		return addAll(c, new OptionsAdd());
	}

	@Override
	public boolean addAll(Collection<? extends Handler> c, OptionsAdd options) {
		boolean result = false;
		for (Handler handler : c) {
			result |= add(handler, options);
		}
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public Handler getByName(String name) {
		return getManageableByName(name);
	}

	@Override
	public int size() {
		return this.handlers.size();
	}

	@Override
	public boolean isEmpty() {
		return this.handlers.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return this.handlers.contains(o);
	}

	@Override
	public Iterator<Handler> iterator() {

		return new Iterator<Handler>() {

			/**
			 * Iterator over underlying collection.
			 */
			private final Iterator<ManageableHandler> iterator = handlers.iterator();
			
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Handler next() {
				return this.iterator.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public Object[] toArray() {
		return this.handlers.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.handlers.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		accessCheck();

		if (o instanceof ManageableHandler) {
			ManageableHandler manageable = (ManageableHandler) o;
			// is it a link ?
			if (manageable.isLink()) {
				// ok no deletion needed, just try to remove it
				// we do not delete linked resources
				return this.handlers.remove(manageable);
			}
			// try to remove it from our collection
			final boolean hasBeenRemoved = this.handlers.remove(manageable);
			if (!hasBeenRemoved) {
				// we dont have it
				return false;
			}
			// delete the data
			try {
				FileUtils.forceDelete(manageable.asFile());
			} catch (IOException ex) {
				LOG.error("Failed to delete file.", ex);
			}
			// we can simply delete the directory this way, 
			// as it has all it's data in it's directory .. no data are outside
			return true;
		} else {
			// unknown
			LOG.warn("Method remove(Object) has been called on unexpected object type: %s",
					o.getClass().getName());
			return false;
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (o instanceof Handler) {
				final Handler handler = (Handler) o;
				if (this.contains(handler)) {
					// ok, continue
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = false;
		// just recall the remove on every object
		for (Object o : c) {
			if (o instanceof Handler) {
				final Handler handler = (Handler) o;
				result |= remove(handler);
			}
		}
		return result;
	}

	@Override
	public void clear() {
		accessCheck();
		// just clear the list
		this.handlers.clear();
		// and clear out directory
		if (directory.exists()) {
			try {
				FileUtils.cleanDirectory(directory);
			} catch (IOException ex) {
				LOG.error("Failed to clean directory.", ex);
			}
		}
	}

	/**
	 * @param name
	 * @return handler of given name or null
	 */
	private ManageableHandler getManageableByName(String name) {
		for (ManageableHandler handler : this.handlers) {
			if (handler.getName().compareTo(name) == 0) {
				return handler;
			}
		}
		return null;
	}

	/**
	 * Check if the modification are permitted on this directory. If not then
	 * throw {@link DataUnitAccessException}.
	 */
	private void accessCheck() {
		if (isReadOnly) {
			throw new DataUnitAccessException("Can't modify read only FileDataUnit.");
		} else if (isLink) {
			throw new DataUnitAccessException("Can't modify linked directory.");
		}
	}

	/**
	 * Scan content of {@link #directory} and add it's content recursively. It
	 * assume that {@link #handlers} is empty!
	 */
	private void scanDirectory() {
		// we want first level files and directories
		File[] toAdd = this.directory.listFiles();

		for (File file : toAdd) {
			if (file == this.directory) {
				// the listFilesAndDirs also return
				// this directory .. 
				continue;
			}

			final String newName = file.getName();
			if (file.isFile()) {
				FileHandlerImpl fileHandler
						= new FileHandlerImpl(file, rootDir, newName, false);
				this.handlers.add(fileHandler);
			} else if (file.isDirectory()) {

				// create handler for subdir, this will 
				// also let it scan the subdir for subdirectories
				DirectoryHandlerImpl dirHandler
						= new DirectoryHandlerImpl(file, rootDir, newName, false);
				this.handlers.add(dirHandler);
			} else {
				LOG.warn("Unknown file '%s' type ignored during scan.", newName);
			}
		}
	}

}
