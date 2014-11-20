package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.virtuoso;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import virtuoso.sesame2.driver.VirtuosoRepository;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.AbstractRDFDataUnit;
import eu.unifiedviews.dataunit.DataUnitException;


/**
 * Implementation of Virtuoso repository - RDF data and intermediate results are
 * saved in Virtuoso storage.
 * 
 * @author Jiri Tomes
 */
public final class VirtuosoRDFDataUnit extends AbstractRDFDataUnit {

    private static final Logger LOG = LoggerFactory.getLogger(VirtuosoRDFDataUnit.class);

    private final Repository repository;

    /**
     * Construct a VirtuosoRepository with a specified parameters.
     * 
     * @param url
     *            the Virtuoso JDBC URL connection string or hostlist
     *            for pooled connection.
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
    public VirtuosoRDFDataUnit(String url, String user, String password, String dataUnitName, String dataGraph) {
        super(dataUnitName, dataGraph);

        this.repository = new VirtuosoRepository(url, user, password);
        try {
            repository.initialize();
        } catch (RepositoryException ex) {
            throw new RuntimeException("Could not initialize repository", ex);
        }
        RepositoryConnection connection = null;
        try {
            connection = getConnection();
            LOG.info("Initialized Virtuoso RDF DataUnit named '{}' with data graph <{}>.", dataUnitName, dataGraph);
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

    @Override
    public void release() {
        super.release();
        try {
            repository.shutDown();
        } catch (RepositoryException ex) {
            LOG.warn("Error in shutdown", ex);
        }
    }
}
