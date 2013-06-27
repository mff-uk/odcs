package cz.cuni.xrg.intlib.commons.data;

/**
 * Exception related to creating DataUnits.
 * 
 * @author Petyr
 *
 */
public class DataUnitCreateException extends Exception {

    public DataUnitCreateException(Throwable cause) {
        super(cause);
    }

    public DataUnitCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataUnitCreateException(String message) {
        super(message);
    }		
	
}
