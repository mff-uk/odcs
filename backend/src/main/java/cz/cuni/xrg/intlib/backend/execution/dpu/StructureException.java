package cz.cuni.xrg.intlib.backend.execution.dpu;

/**
 * Exception inform about problem in {@link Pipeline} structure. 
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
