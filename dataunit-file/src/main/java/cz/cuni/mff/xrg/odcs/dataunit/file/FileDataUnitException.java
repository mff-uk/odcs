package cz.cuni.mff.xrg.odcs.dataunit.file;

/**
 * Base class for exceptions used by {@link FileDataUnit}.
 * 
 * @author Petyr
 */
public abstract class FileDataUnitException extends Exception {
	
	public FileDataUnitException(Throwable cause) {
		super(cause);
	}

}
