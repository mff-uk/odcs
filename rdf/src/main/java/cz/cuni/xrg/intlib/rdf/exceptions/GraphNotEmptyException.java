package cz.cuni.xrg.intlib.rdf.exceptions;

/**
 *
 * @author Jiri Tomes
 */
public class GraphNotEmptyException extends RuntimeException {

    private String message = "Target graph is not empty. Load to SPARQL endpoint fail.";

    public GraphNotEmptyException() {
    }

    public GraphNotEmptyException(String message) {
        super(message);
    }

    public GraphNotEmptyException(Throwable cause) {
        super(cause);
    }

    public GraphNotEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
