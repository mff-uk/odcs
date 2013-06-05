package cz.cuni.xrg.intlib.commons.app.communication;

/**
 * Base class for communication exception. 
 * 
 * @author Petyr
 *
 */
public class CommunicationException extends Exception {

    public CommunicationException(Throwable cause) {
        super(cause);
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunicationException(String message) {
        super(message);
    }	
	
}
