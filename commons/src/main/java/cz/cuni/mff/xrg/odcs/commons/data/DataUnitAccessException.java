package cz.cuni.mff.xrg.odcs.commons.data;

/**
 * Should be thrown when user try to modify read only {@link DataUnit}.
 * 
 * @author Petyr
 * 
 */
public class DataUnitAccessException extends RuntimeException {

	/**
	 * Create exception with default message.
	 */
    public DataUnitAccessException() {
        super("Can't modify given object!");
    }	
	
	/**
	 * 
	 * @param cause Cause of the {@link DataUnitAccessException}.
	 */	
    public DataUnitAccessException(Throwable cause) {
        super(cause);
    }
	
	/**
	 * 
	 * @param cause Cause of the {@link DataUnitAccessException}.
	 */
    public DataUnitAccessException(String cause) {
        super(cause);
    }
	
	/**
	 * 
	 * @param message Description of action that throws.
	 * @param cause Cause of the {@link DataUnitAccessException}.
	 */	
    public DataUnitAccessException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
