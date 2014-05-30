package cz.cuni.mff.xrg.odcs.commons.dpu;

/**
 * Base class for exception that are connected to the DPU's problems. Can be
 * also thrown by {@link DPU#execute(DPUContext)} to indicate that the execution
 * failed.
 * 
 * @author Petyr
 */
public class DPUException extends Exception {

    /**
     * @param cause
     *            Cause of the {@link DPUException}.
     */
    public DPUException(Throwable cause) {
        super(cause);
    }

    /**
     * @param cause
     *            Cause of the {@link DPUException}.
     */
    public DPUException(String cause) {
        super(cause);
    }

    /**
     * @param message
     *            Description of action that throws.
     * @param cause
     *            Cause of the {@link DPUException}.
     */
    public DPUException(String message, Throwable cause) {
        super(message, cause);
    }

}
