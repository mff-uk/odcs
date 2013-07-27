package cz.cuni.xrg.intlib.commons.dpu;

/**
 * Base abstract class for exception that are connected to the 
 * general dpu's problems that are not covered by other exceptions.
 * 
 * As the exception extends {@link RuntimeException} it does not have to 
 * be declared in methods or catch. 
 * 
 * @author Petyr
 *
 */
public abstract class DPUException extends RuntimeException {

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
