package eu.unifiedviews.commons.rdf.repository;

import java.io.File;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 *
 * @author Škoda Petr
 */
public class RepositoryFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryFactory.class);

    private String rootDirectory;

    private String uri;
    
    private String user;
    
    private String password;

    /**
     *
     * @param uri URI of remote server.
     * @param user User name.
     * @param password User password.
     */
    public void setRemoteParameters(String uri, String user, String password) {
        this.uri = uri;
        this.user = user;
        this.password = password;
    }

    /**
     *
     * @param rootDirectory Root directory where repositories data can be stored.
     */
    public void setLocalParameters(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public ManagableRepository create(String pipelineId, ManagableRepository.Type type) throws RDFException {
        // Prepare parameters.
        final String directory = rootDirectory + File.separator + pipelineId;
        // Create repository.
        final ManagableRepository repository;
        switch (type) {
            case INMEMORY_RDF:
                repository = new InMemoryRDF(directory);
                break;
            case LOCAL_RDF:
                repository = new LocalRDF(directory);
                break;
            case REMOTE_RDF:
                repository = new RemoteRDF(uri, user, password, pipelineId);
                break;
            case VIRTUOSO:
                repository = new Virtuoso(uri, user, password);
                break;
            default:
                throw new RDFException("Unknown repository type: " + type.toString());
        }
        // Test connection.
        RepositoryConnection connection = null;
        try {
            connection = repository.getConnectionSource().getConnection();
        } catch (RepositoryException ex) {
            throw new RDFException("Could not test initial connect to repository", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                }
            }
        }
        return repository;
    }

}
