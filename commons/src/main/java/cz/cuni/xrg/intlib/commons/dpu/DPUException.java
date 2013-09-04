package cz.cuni.xrg.intlib.commons.dpu;

/**
 * Base class for exception that are connected to the dpu's problems. Can be 
 * also thrown by {@link DPU#execute(DPUContext)} to indicate that the execution 
 * failed.
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
