package cz.cuni.xrg.intlib.rdf.exceptions;

/**
 *
 * @author Jiri Tomes
 */
public class InvalidQueryException extends Exception {

    private final String message = "This SPARQL query is not valid !!!";

    public InvalidQueryException() {
    }

    
    public InvalidQueryException(String message) {
        super(message);
    }

    public InvalidQueryException(Throwable cause) {
        super(cause);
    }

    public InvalidQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return message;

    }
}
