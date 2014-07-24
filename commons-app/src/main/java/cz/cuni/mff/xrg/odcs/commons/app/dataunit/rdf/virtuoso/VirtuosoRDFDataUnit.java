package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.virtuoso;

import org.openrdf.model.URI;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import virtuoso.sesame2.driver.VirtuosoRepository;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.AbstractRDFDataUnit;


/**
 * Implementation of Virtuoso repository - RDF data and intermediate results are
 * saved in Virtuoso storage.
 * 
 * @author Jiri Tomes
 */
public final class VirtuosoRDFDataUnit extends AbstractRDFDataUnit {

    private static final Logger LOG = LoggerFactory.getLogger(VirtuosoRDFDataUnit.class);

    private Repository repository;

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
    public VirtuosoRDFDataUnit(String url, String user, String password,
            String dataUnitName, String dataGraph) {
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
            LOG.info("Initialized Virtuoso RDF DataUnit named '{}' with data graph <{}> containing {} triples.",
                    dataUnitName, dataGraph, connection.size(this.getBaseDataGraphURI()));
        } catch (RepositoryException | DataUnitException ex) {
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

    //WritableDataUnit interface
    @Override
    public void addAll(RDFDataUnit otherDataUnit) {
        if (!this.getClass().equals(otherDataUnit.getClass())) {
            throw new IllegalArgumentException("Incompatible DataUnit class. This DataUnit is of class "
                    + this.getClass().getCanonicalName() + " and it cannot merge other DataUnit of class " + otherDataUnit.getClass().getCanonicalName() + ".");
        }

        final RDFDataUnit otherRDFDataUnit = (RDFDataUnit) otherDataUnit;
        RepositoryConnection connection = null;
        try {
            connection = getConnection();

            String targetGraphName = getBaseDataGraphURI().stringValue();
            for (URI sourceGraph : otherRDFDataUnit.getDataGraphnames()) {
                String sourceGraphName = sourceGraph.stringValue();

                LOG.info("Trying to merge {} triples from <{}> to <{}>.",
                        connection.size(sourceGraph), sourceGraphName,
                        targetGraphName);

                // mergeQuery = String.format("DEFINE sql:log-enable %d \n"
                // + "ADD <%s> TO <%s>",
                // LOG_LEVEL,
                // sourceGraphName,
                // targetGraphName);
                String mergeQuery = String.format("ADD <%s> TO <%s>", sourceGraphName,
                        targetGraphName);

                GraphQuery result = connection.prepareGraphQuery(
                        QueryLanguage.SPARQL, mergeQuery);

                result.evaluate();

                LOG.info("Merged {} triples from <{}> to <{}>.",
                        connection.size(sourceGraph), sourceGraphName,
                        targetGraphName);
            }
        } catch (MalformedQueryException ex) {
            LOG.error("NOT VALID QUERY: {}", ex);
        } catch (QueryEvaluationException ex) {
            LOG.error("MERGING STOPPED: {}", ex);
        } catch (RepositoryException | DataUnitException ex) {
            LOG.error(ex.getMessage(), ex);
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
    public void release() {
        super.release();
        try {
            repository.shutDown();
        } catch (RepositoryException ex) {
            LOG.warn("Error in shutdown", ex);
            // eat close exception, we cannot do anything clever here;
        }
    }
}
