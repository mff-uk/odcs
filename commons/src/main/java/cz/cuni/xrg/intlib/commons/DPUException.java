package cz.cuni.xrg.intlib.commons;

/**
 * Base class for DPU's exception.
 * 
 * @author Petyr
 *
 */
public class DPUException extends Exception {

    public DPUException(Throwable cause) {
        super(cause);
    }

    public DPUException(String message, Throwable cause) {
        super(message, cause);
    }

    public DPUException(String message) {
        super(message);
    }	
	
}
