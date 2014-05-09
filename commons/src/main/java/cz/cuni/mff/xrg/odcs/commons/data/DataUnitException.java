package cz.cuni.mff.xrg.odcs.commons.data;

/**
 * Base class for exceptions related to DataUnits.
 * 
 * @author Petyr
 */
public abstract class DataUnitException extends Exception {

    /**
     * @param cause
     *            Cause of the {@link DataUnitException}.
     */
    public DataUnitException(Throwable cause) {
        super(cause);
    }

    /**
     * @param cause
     *            Cause of the {@link DataUnitException}.
     */
    public DataUnitException(String cause) {
        super(cause);
    }

    /**
     * @param message
     *            Description of action that throws.
     * @param cause
     *            Cause of the {@link DataUnitException}.
     */
    public DataUnitException(String message, Throwable cause) {
        super(message, cause);
    }

}
