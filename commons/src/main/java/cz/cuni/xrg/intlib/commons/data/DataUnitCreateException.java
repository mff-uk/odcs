package cz.cuni.xrg.intlib.commons.data;

/**
 * Exception related to DataUnits creation.
 * 
 * @author Petyr
 *
 */
public class DataUnitCreateException extends DataUnitException {

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
