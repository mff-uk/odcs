package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.remoterdf;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.repository.manager.RepositoryProvider;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.nativerdf.config.NativeStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.AbstractRDFDataUnit;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Implementation of Sesame http repository - RDF data and intermediate results are
 * saved in Sesame server
 */
public final class RemoteRDFDataUnit extends AbstractRDFDataUnit {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteRDFDataUnit.class);

    private Repository repository;

    /**
     * Construct a Remote repository with a specified parameters.
     * 
     * @param url
     *            the URL connection string
     * @param user
     *            the database user on whose behalf the connection is
     *            being made.
     * @param password
     *            the user's password.
     * @param dataGraph
     *            a default Graph name, used for Sesame calls, when
     *            contexts list is empty, exclude exportStatements,
     *            hasStatement, getStatements methods.
     * @param dataUnitName
     *            DataUnit's name. If not used in Pipeline can be
     *            empty String.
     */
    public RemoteRDFDataUnit(String url, String user, String password, String pipelineId, String dataUnitName, String dataGraph) {
        super(dataUnitName, dataGraph);

        try {
            RepositoryManager repositoryManager = RepositoryProvider.getRepositoryManager(url);
            if (repositoryManager instanceof RemoteRepositoryManager) {
                if (user != null && !user.isEmpty()) {
                    ((RemoteRepositoryManager) repositoryManager).setUsernameAndPassword(user, password);
                }
            }
            repository = repositoryManager.getRepository(pipelineId);
            if (repository == null) {
                repositoryManager.addRepositoryConfig(new RepositoryConfig(pipelineId, new SailRepositoryConfig(new NativeStoreConfig())));
                repository = repositoryManager.getRepository(pipelineId);
            }
            if (repository == null) {
                throw new RuntimeException("Could not initialize repository");
            }
        } catch (RepositoryConfigException | RepositoryException ex) {
            throw new RuntimeException("Could not initialize repository", ex);
        }

        try {
            repository.initialize();
        } catch (RepositoryException ex) {
            throw new RuntimeException("Could not initialize repository", ex);
        }
        RepositoryConnection connection = null;
        try {
            connection = getConnection();
            LOG.info("Initialized Sesame RDF DataUnit named '{}' with data graph <{}>", dataUnitName, dataGraph);
        } catch (DataUnitException ex) {
            throw new RuntimeException("Could not test initial connect to repository", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                }
            }
        }
    }

    @Override
    public RepositoryConnection getConnectionInternal() throws RepositoryException {
        return repository.getConnection();
    }
}
