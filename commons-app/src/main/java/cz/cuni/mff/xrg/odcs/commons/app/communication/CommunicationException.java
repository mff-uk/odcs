package cz.cuni.mff.xrg.odcs.commons.app.communication;

/**
 * Base class for communication exception. 
 * 
 * @author Petyr
 *
 */
public class CommunicationException extends Exception {

	/**
	 * 
	 * @param cause Cause of the {@link CommunicationException}.
	 */		
    public CommunicationException(Throwable cause) {
        super(cause);
    }
	
	/**
	 * 
	 * @param cause Cause of the {@link CommunicationException}.
	 */	
    public CommunicationException(String cause) {
        super(cause);
    }	
	
	/**
	 * 
	 * @param message Description of action that throws.
	 * @param cause Cause of the {@link CommunicationException}.
	 */	
    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
