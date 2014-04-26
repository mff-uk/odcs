package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitAccessException;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnitException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link FileHandler}.
 *
 * @author Petyr
 */
public class FileHandlerImpl implements ManageableHandler, FileHandler {

	private static final Logger LOG = LoggerFactory.getLogger(FileHandlerImpl.class);

	/**
	 * Name of the file.
	 */
	private String name;

	/**
	 * Path to the represented file.
	 */
	private File file;

	/**
	 * Directory in which we are located in sense of file data unit.
	 */
	private DirectoryHandler parent;
	
	/**
	 * User data.
	 */
	private String userData;

	/**
	 * True if the represent file is in link mode. Ie. it is not located in
	 * DataUnit directory.
	 */
	private boolean isLink;

	/**
	 * Create non-readonly handler for given file.
	 *
	 * @param file Path to the existing file.
	 * @param parent
	 * @param name File name must not be null!
	 * @param asLink
	 */
	FileHandlerImpl(File file, DirectoryHandler parent, String name, boolean asLink) {
		this.name = name;
		this.file = file;
		this.parent = parent;
		this.userData = null;
		this.isLink = asLink;
		// if not exist and is not link
		if (!file.exists() && !asLink) {
			// create it
			try {
				FileUtils.touch(file);
			} catch (IOException ex) {
				LOG.error("Failed to touch new file.", ex);
				// user fill soon find this out
			}
		}
	}

	@Override
	public String getContent() {
		try {
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			LOG.error("Failed to read file content as string.", ex);
			return null;
		}
	}

	@Override
	public void setContent(String newContent) throws FileDataUnitException {
		if (isLink) {
			throw new DataUnitAccessException("Can't modify 'linked' file!");
		}
		try {
			// create file if it does not exits
			FileUtils.writeStringToFile(file, newContent);
		} catch (IOException ex) {
			LOG.error("Failed to save content into file.", ex);
			throw new ContentWriteFailed(ex);
		}
	}

	@Override
	public File asFile() {
		return file;
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
	public boolean equals(Object obj) {
		if (obj instanceof FileHandlerImpl) {
			FileHandlerImpl right = (FileHandlerImpl) obj;
			return name.equals(right.name);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 13 * hash + Objects.hashCode(this.name);
		return hash;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getRootedPath() {
		final String parentPath = parent.getRootedPath();
		return parentPath + "/" + getName();
	}	

	@Override
	public boolean isLink() {
		return this.isLink;
	}
	
}
