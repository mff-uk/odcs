package cz.cuni.xrg.intlib.rdf.exceptions;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;

/**
 *
 * @author Jiri Tomes
 */
public class InvalidQueryException extends DataUnitException {

    private final String message = "This SPARQL query is not valid !!!";

    public InvalidQueryException() {
    	super("");
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
