package cz.cuni.xrg.intlib.repository;

import java.io.IOException;

/**
 *
 * @author Jiri Tomes
 */
public class FileCannotOverwriteException extends IOException {

    private final String message = "File can not be overwrite";

    public FileCannotOverwriteException() {
    }

    public FileCannotOverwriteException(String message) {
        super(message);
    }

    public FileCannotOverwriteException(Throwable cause) {
        super(cause);
    }

    public FileCannotOverwriteException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
