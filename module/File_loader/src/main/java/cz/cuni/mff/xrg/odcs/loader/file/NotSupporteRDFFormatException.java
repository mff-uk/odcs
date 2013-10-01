package cz.cuni.mff.xrg.odcs.loader.file;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;

/**
 * This exception is thrown, when selected data format is not supported by basic
 * RDF format types.
 *
 * @author Jiri Tomes
 */
public class NotSupporteRDFFormatException extends ConfigException {

    private final String message = "This RDF format is not supported.";

    public NotSupporteRDFFormatException() {
    }

    public NotSupporteRDFFormatException(String message) {
        super(message);
    }

    public NotSupporteRDFFormatException(Throwable cause) {
        super(cause);
    }

    public NotSupporteRDFFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
