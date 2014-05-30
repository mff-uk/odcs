package cz.cuni.mff.xrg.odcs.commons.data;

/**
 * Base class for exceptions related to DataUnits.
 * 
 * @author Petyr
 */
public class DataUnitException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8479349779218724204L;

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
