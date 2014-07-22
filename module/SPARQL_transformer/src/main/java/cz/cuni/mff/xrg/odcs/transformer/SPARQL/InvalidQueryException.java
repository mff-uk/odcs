package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Exception is thrown when given SPARQL query is not valid.
 * 
 * @author Jiri Tomes
 */
public class InvalidQueryException extends DataUnitException {

    private static final String message = "This SPARQL query is not valid !!!";

    /**
     * Create a new instance of {@link InvalidQueryException} with the default {@link #message}.
     */
    public InvalidQueryException() {
        super(message);
    }

    /**
     * Create new instance of {@link InvalidQueryException} with the specific
     * message.
     * 
     * @param message
     *            String value of described message
     */
    public InvalidQueryException(String message) {
        super(message);
    }

    /**
     * Create new instance of {@link InvalidQueryException} with cause of
     * throwing this exception.
     * 
     * @param cause
     *            The cause of throwing exception
     */
    public InvalidQueryException(Throwable cause) {
        super(cause);
    }

    /**
     * Create new instance of {@link InvalidQueryException} with the specific
     * message and the cause of throwing this exception.
     * 
     * @param message
     *            String value of described message
     * @param cause
     *            The cause of throwing exception
     */
    public InvalidQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
