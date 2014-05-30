package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnitException;

/**
 * Represent situation when the writing of content into file failed.
 * 
 * @author Petyr
 */
public class ContentWriteFailed extends FileDataUnitException {

    /**
     * @param cause
     *            Cause of the {@link ContentWriteFailed}.
     */
    public ContentWriteFailed(Throwable cause) {
        super(cause);
    }

}
