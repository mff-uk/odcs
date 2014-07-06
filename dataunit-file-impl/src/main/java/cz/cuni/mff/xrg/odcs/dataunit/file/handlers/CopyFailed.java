package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Thrown in case that some file/directory can not be copied.
 * 
 * @author Petyr
 */
public class CopyFailed extends DataUnitException {

    /**
     * @param cause
     *            Cause of the {@link ContentWriteFailed}.
     */
    public CopyFailed(Throwable cause) {
        super(cause);
    }

}
