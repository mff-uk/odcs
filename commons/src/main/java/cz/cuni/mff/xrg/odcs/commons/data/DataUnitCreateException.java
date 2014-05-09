package cz.cuni.mff.xrg.odcs.commons.data;

/**
 * Exception related to DataUnits creation.
 * 
 * @author Petyr
 */
public class DataUnitCreateException extends DataUnitException {

    /**
     * @param cause
     *            Cause of the {@link DataUnitCreateException}.
     */
    public DataUnitCreateException(Throwable cause) {
        super(cause);
    }

    /**
     * @param cause
     *            Cause of the {@link DataUnitCreateException}.
     */
    public DataUnitCreateException(String cause) {
        super(cause);
    }

    /**
     * @param message
     *            Description of action that throws.
     * @param cause
     *            Cause of the {@link DataUnitCreateException}.
     */
    public DataUnitCreateException(String message, Throwable cause) {
        super(message, cause);
    }

}
