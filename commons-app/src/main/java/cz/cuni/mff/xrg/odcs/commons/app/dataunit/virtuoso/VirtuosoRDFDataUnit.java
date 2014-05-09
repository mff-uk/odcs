package cz.cuni.mff.xrg.odcs.commons.app.dataunit.virtuoso;

import java.util.ArrayList;
import java.util.List;

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
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.AbstractRDFDataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

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
     * @throws RepositoryException
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

    /**
     * Make RDF data merge over repository - data in repository merge with data
     * in second defined repository.
     * 
     * @param otherDataUnit
     *            Type of repository contains RDF data as implementation of
     *            RDFDataUnit interface.
     * @throws IllegalArgumentException
     *             if otherDataUnit repository is not of compatible type (#RDFDataUnit).
     */
    @Override
    public void merge(DataUnit otherDataUnit) throws IllegalArgumentException {
        if (!(otherDataUnit instanceof VirtuosoRDFDataUnit)) {
            throw new IllegalArgumentException("Incompatible repository type");
        }

        final RDFDataUnit otherRDFDataUnit = (RDFDataUnit) otherDataUnit;
        RepositoryConnection connection = null;
        try {
            connection = getConnection();

            String sourceGraphName = otherRDFDataUnit.getDataGraph().stringValue();
            String targetGraphName = getDataGraph().stringValue();

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
                    connection.size(getDataGraph()), sourceGraphName,
                    targetGraphName);

        } catch (MalformedQueryException ex) {
            LOG.error("NOT VALID QUERY: {}", ex);
        } catch (QueryEvaluationException ex) {
            LOG.error("MERGING STOPPED: {}", ex);
        } catch (RepositoryException ex) {
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
}
