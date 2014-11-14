package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.localrdf;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.AbstractRDFDataUnit;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Implementation of local RDF repository - RDF data are saved in files on hard
 * disk in computer, intermediate results are keeping in computer memory.
 *
 */
public class LocalRDFDataUnit extends AbstractRDFDataUnit {

    private static final Logger LOG = LoggerFactory.getLogger(LocalRDFDataUnit.class);

    private final Repository repository;

    /**
     * Public constructor - create new instance of repository in defined
     * repository Path.
     *
     * @param repository
     * @param dataGraph
     *            String value of URI graph that will be set to repository.
     * @param dataUnitName
     *            DataUnit's name. If not used in Pipeline can be empty String.
     */
    public LocalRDFDataUnit(Repository repository, String dataUnitName, String dataGraph) {
        super(dataUnitName, dataGraph);
        this.repository = repository;

        RepositoryConnection connection = null;
        try {
            connection = getConnection();
            LOG.info("Initialized Local RDF DataUnit named '{}' with data graph <{}>.", dataUnitName, dataGraph);
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
