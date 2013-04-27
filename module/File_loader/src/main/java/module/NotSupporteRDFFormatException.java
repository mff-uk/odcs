package module;

/**
 *
 * @author Jiri Tomes
 */
public class NotSupporteRDFFormatException extends Exception {

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
