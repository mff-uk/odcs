package cz.cuni.mff.xrg.odcs.loader.file;

import eu.unifiedviews.dpu.config.DPUConfigException;

/**
 * This exception is thrown, when selected data format is not supported by basic
 * RDF format types.
 * 
 * @author Jiri Tomes
 */
public class NotSupporteRDFFormatException extends DPUConfigException {

    private final String message = "This RDF format is not supported.";

    /**
     * Create a new instance of {@link NotSupporteRDFFormatException} without
     * detail message.
     */
    public NotSupporteRDFFormatException() {
    }

    /**
     * Create new instance of {@link NotSupporteRDFFormatException} with
     * specific message.
     * 
     * @param message
     *            String value of described message
     */
    public NotSupporteRDFFormatException(String message) {
        super(message);
    }

    /**
     * Create new instance of {@link NotSupporteRDFFormatException} with cause
     * of throwing this exception.
     * 
     * @param cause
     *            Cause of throwing exception
     */
    public NotSupporteRDFFormatException(Throwable cause) {
        super(cause);
    }

    /**
     * Create new instance of {@link NotSupporteRDFFormatException} with cause
     * of throwing this exception.
     * 
     * @param message
     * @param cause
     *            Cause of throwing exception
     */
    public NotSupporteRDFFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns the error message as string.
     * 
     * @return the error message as string.
     */
    @Override
    public String getMessage() {
        return message;
    }
}
