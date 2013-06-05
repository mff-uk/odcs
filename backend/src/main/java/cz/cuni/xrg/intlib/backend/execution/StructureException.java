package cz.cuni.xrg.intlib.backend.execution;

/**
 * Represent error in pipeline structure. Used by PipelineWorker.
 * 
 * @author Petyr
 *
 */
public class StructureException extends Exception {

    public StructureException(Throwable cause) {
        super(cause);
    }

    public StructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public StructureException(String message) {
        super(message);
    }		
	
}
