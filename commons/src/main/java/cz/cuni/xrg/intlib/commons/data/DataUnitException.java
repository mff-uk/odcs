package cz.cuni.xrg.intlib.commons.data;

/**
 * Base class for exceptions related to DataUnits.
 * 
 * @author Petyr
 *
 */
public abstract class DataUnitException extends Exception {

    public DataUnitException(Throwable cause) {
        super(cause);
    }

    public DataUnitException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataUnitException(String message) {
        super(message);
    }		
	
}