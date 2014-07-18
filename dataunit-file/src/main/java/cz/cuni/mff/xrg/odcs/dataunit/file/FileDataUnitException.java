package cz.cuni.mff.xrg.odcs.dataunit.file;

/**
 * Base class for exceptions used by {@link FileDataUnit}.
 * 
 * @author Petyr
 */
public abstract class FileDataUnitException extends Exception {

    /**
     * @param cause
     *            Cause of the {@link FileDataUnitException}.
     */
    public FileDataUnitException(Throwable cause) {
        super(cause);
    }

    public FileDataUnitException(String string) {
        super(string);
    }

}
