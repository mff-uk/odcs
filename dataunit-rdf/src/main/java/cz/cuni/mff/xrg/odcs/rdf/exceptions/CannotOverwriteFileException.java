package cz.cuni.mff.xrg.odcs.rdf.exceptions;

import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Exception is thrown when the file where RDF data are loaded is protected for
 * overwritting.
 * 
 * @author Jiri Tomes
 */
public class CannotOverwriteFileException extends DataUnitException {

    private static final String MESSAGE = "This file cannot be overwritten";

    /**
     * Create a new instance of {@link CannotOverwriteFileException} with the
     * default {@link #MESSAGE}.
     */
    public CannotOverwriteFileException() {
        super(MESSAGE);
    }

    /**
     * Create new instance of {@link CannotOverwriteFileException} with the
     * specific message.
     * 
     * @param message
     *            String value of described message
     */
    public CannotOverwriteFileException(String message) {
        super(message);
    }

    /**
     * Create new instance of {@link CannotOverwriteFileException} with cause of
     * throwing this exception.
     * 
     * @param cause
     *            The cause of throwing the exception
     */
    public CannotOverwriteFileException(Throwable cause) {
        super(cause);
    }

    /**
     * Create new instance of {@link CannotOverwriteFileException} with the
     * specific message and the cause of throwing this exception.
     * 
     * @param message
     *            String value of described message
     * @param cause
     *            The cause of throwing exception
     */
    public CannotOverwriteFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
