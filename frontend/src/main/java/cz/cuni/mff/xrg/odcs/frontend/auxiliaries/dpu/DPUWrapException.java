package cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu;

/**
 * Exception used to wrap other exception that can occurs
 * during working with {@link DPURecordWrap} and it's descendants.
 * 
 * @author Petyr
 *
 */
public class DPUWrapException extends Exception {
	
	public DPUWrapException(Throwable cause) {
        super(cause);
    }

    public DPUWrapException(String message, Throwable cause) {
        super(message, cause);
    }

    public DPUWrapException(String message) {
        super(message);
    }
    
}
