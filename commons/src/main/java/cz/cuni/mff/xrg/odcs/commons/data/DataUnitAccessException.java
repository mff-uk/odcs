package cz.cuni.mff.xrg.odcs.commons.data;

/**
 * Should be thrown when user try to modify read only {@link DataUnit}.
 * 
 * @author Petyr
 */
public class DataUnitAccessException extends RuntimeException {

    public DataUnitAccessException() {
        super("Can't modify read only dataunit!");
    }	
	
    public DataUnitAccessException(Throwable cause) {
        super(cause);
    }

    public DataUnitAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataUnitAccessException(String message) {
        super(message);
    }	
	
}
