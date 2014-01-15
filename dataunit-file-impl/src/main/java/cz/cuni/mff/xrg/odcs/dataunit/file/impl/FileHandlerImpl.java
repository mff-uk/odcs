package cz.cuni.mff.xrg.odcs.dataunit.file.impl;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileHandler;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link FileHandler}.
 * @author Petyr
 */
class FileHandlerImpl implements FileHandler {

	private static final Logger LOG = LoggerFactory.getLogger(FileHandlerImpl.class);
	
	/**
	 * File represented by this class.
	 */
	private final File file;
	
	/**
	 * Name of the represented file.
	 */
	private final String name;
	
	FileHandlerImpl(File file, String name) {
		this.file = file;
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FileHandlerImpl) {
			FileHandlerImpl right = (FileHandlerImpl)obj;
			// just compare the file
			return file.equals(right.file);
		} else {
			// use super implementation
			return super.equals(obj);
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.file);
		return hash;
	}

	@Override
	public File asFile() {
		return file;
	}

	@Override
	public String asString() {
		try {
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			LOG.error("Failed to read file content as string.", ex);
			return null;
		}
	}

	@Override
	public void setContent(String content) throws DataUnitException {
		try {
			FileUtils.writeStringToFile(file, content);
		} catch (IOException ex) {
			LOG.error("Failed to save content into file.", ex);
			throw new FileCreationException("Failed to save content into file.", ex);
		}
	}

	@Override
	public String getName() {
		return name;
	}
	
}
