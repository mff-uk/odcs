package eu.unifiedviews.commons.rdf;

import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Used to get connection to repository. Does not manage or close the repository.
 *
 * @author Škoda Petr
 */
public class ConnectionSource {

    /**
     * Underlying repository.
     */
    private final Repository repository;

    /**
     * If true then connection is considered to be unreliable and in case of failure the operation
     * should be tried again.
     */
    private final boolean retryOnFailure;

    /**
     * Used repository.
     *
     * @param repository
     * @param retryOnFailure
     */
    public ConnectionSource(Repository repository, boolean retryOnFailure) {
        this.repository = repository;
        this.retryOnFailure = retryOnFailure;
    }

    public RepositoryConnection getConnection() throws RepositoryException {
        return repository.getConnection();
    }

    public boolean isRetryOnFailure() {
        return retryOnFailure;
    }

    public ValueFactory getValueFactory() {
        return repository.getValueFactory();
    }

}
