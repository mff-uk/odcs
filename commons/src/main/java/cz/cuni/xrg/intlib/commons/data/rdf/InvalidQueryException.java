package cz.cuni.xrg.intlib.commons.data.rdf;

/**
 *
 * @author Jiri Tomes
 */
public class NotValidQueryException extends Exception {

    private final String message = "This SPARQL query is not valid !!!";

    public NotValidQueryException() {
    }

    
    public NotValidQueryException(String message) {
        super(message);
    }

    public NotValidQueryException(Throwable cause) {
        super(cause);
    }

    public NotValidQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return message;

    }
}
