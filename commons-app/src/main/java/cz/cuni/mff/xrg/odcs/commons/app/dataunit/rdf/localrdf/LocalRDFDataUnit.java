package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.localrdf;

import java.io.File;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryProvider;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.nativerdf.config.NativeStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.AbstractRDFDataUnit;

/**
 * Implementation of local RDF repository - RDF data are saved in files on hard
 * disk in computer, intermediate results are keeping in computer memory.
 * 
 * @author Jiri Tomes
 */
public class LocalRDFDataUnit extends AbstractRDFDataUnit {
    private static final Logger LOG = LoggerFactory.getLogger(LocalRDFDataUnit.class);

    public static final String GLOBAL_REPOSITORY_ID = "sdavhniw2uv3ni32u3fkhj";

    private Repository repository;

    /**
     * Public constructor - create new instance of repository in defined
     * repository Path.
     * 
     * @param repositoryPath
     *            String value of path to directory where will be repository
     *            stored.
     * @param namedGraph
     *            String value of URI graph that will be set to repository.
     * @param dataUnitName
     *            DataUnit's name. If not used in Pipeline can be empty String.
     */
    public LocalRDFDataUnit(String repositoryPath, String dataUnitName,
            String dataGraph) {
        super(dataUnitName, dataGraph);

        try {
            File managerDir = new File(repositoryPath);
            if (!managerDir.isDirectory() && !managerDir.mkdirs()) {
                throw new RuntimeException("Could not create repository manager directory.");
            }
            LocalRepositoryManager localRepositoryManager = RepositoryProvider.getRepositoryManager(managerDir);
            repository = localRepositoryManager
                    .getRepository(GLOBAL_REPOSITORY_ID);
            if (repository == null) {
                localRepositoryManager.addRepositoryConfig(
                        new RepositoryConfig(GLOBAL_REPOSITORY_ID, new SailRepositoryConfig(new NativeStoreConfig()))
                        );
                repository = localRepositoryManager.getRepository(GLOBAL_REPOSITORY_ID);
            }
            if (repository == null) {
                throw new RuntimeException("Could not initialize repository");
            }
        } catch (RepositoryConfigException | RepositoryException ex) {
            throw new RuntimeException("Could not initialize repository", ex);
        }

        RepositoryConnection connection = null;
        try {
            connection = getConnection();
            LOG.info("Initialized Local RDF DataUnit named '{}' with data graph <{}> containing {} triples.",
                    dataUnitName, dataGraph, connection.size(this.getDataGraph()));
        } catch (RepositoryException ex) {
            throw new RuntimeException("Could not test initial connect to repository", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }
    }

    @Override
    public RepositoryConnection getConnectionInternal() throws RepositoryException {
        return repository.getConnection();
    }
}
