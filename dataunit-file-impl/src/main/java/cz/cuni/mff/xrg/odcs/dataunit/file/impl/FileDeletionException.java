package cz.cuni.mff.xrg.odcs.dataunit.file.impl;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

/**
 * Exception that is used to report problem during on request file deletion.
 * @author Petyr
 */
public class FileDeletionException extends DataUnitException {
	
	public FileDeletionException(Throwable cause) {
		super(cause);
	}

	public FileDeletionException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileDeletionException(String message) {
		super(message);
	}
	
}
