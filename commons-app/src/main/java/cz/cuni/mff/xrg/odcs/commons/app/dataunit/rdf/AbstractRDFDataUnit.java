package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf;

import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.repositories.FileRDFMetadataExtractor;
import cz.cuni.mff.xrg.odcs.rdf.repositories.OrderTupleQueryResultImpl;

/**
 * Abstract class provides common parent methods for RDFDataUnit implementation.
 *
 * @author Jiri Tomes
 */
public abstract class AbstractRDFDataUnit implements ManagableRdfDataUnit {

    public static final String DATA_UNIT_STORE_GRAPH = "http://linked.opendata.cz/ontology/odcs/dataunit";
    public static final String DATA_UNIT_RDF_CONTAINSGRAPH_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dataunit/rdf/containsGraph";
    public static final String DATA_UNIT_RDF_WRITEGRAPH_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dataunit/rdf/writeGraph";

    private FileRDFMetadataExtractor fileRDFMetadataExtractor;

    /**
     * Logging information about execution of method using openRDF.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRDFDataUnit.class);

    private String dataUnitName;

    protected URI writeContext;

    protected Set<URI> readContexts;

    private List<RepositoryConnection> requestedConnections;

    private Thread ownerThread;

    public abstract RepositoryConnection getConnectionInternal() throws RepositoryException;

    public AbstractRDFDataUnit(String dataUnitName, String writeContextString) {
        this.dataUnitName = dataUnitName;
        if (!writeContextString.toLowerCase().startsWith("http://")) {
            writeContextString = "http://" + writeContextString;
        }
        this.writeContext = new URIImpl(writeContextString);
        this.readContexts = new HashSet<>();
        this.readContexts.add(this.writeContext);

        this.requestedConnections = new ArrayList<>();
        this.ownerThread = Thread.currentThread();

        this.fileRDFMetadataExtractor = new FileRDFMetadataExtractor(this);
    }

    //DataUnit interface
    @Override
    public ManagableDataUnit.Type getType() {
        return ManagableDataUnit.Type.RDF;
    }

    //DataUnit interface
    @Override
    public boolean isType(ManagableDataUnit.Type dataUnitType) {
        return this.getType().equals(dataUnitType);
    }

    //DataUnit interface
    @Override
    public String getName() {
        return dataUnitName;
    }

    //MetadataDataUnit interface
    @Override
    public RepositoryConnection getConnection() throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            LOG.warn("Constraint violation, only one thread can access this data unit");
        }

        try {
            RepositoryConnection connection = getConnectionInternal();
            requestedConnections.add(connection);
            return connection;
        } catch (RepositoryException ex) {
            throw new DataUnitException(ex);
        }
    }

    //MetadataDataUnit interface
    @Override
    public Set<URI> getMetadataGraphnames() throws DataUnitException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //RDFDataUnit interface
    @Override
    public Set<URI> getDataGraphnames() {
        return readContexts;
    }

    //RDFDataUnit interface old
    public Map<String, List<String>> getRDFMetadataForSubjectURI(
            String subjectURI, List<String> predicates) {
        return this.fileRDFMetadataExtractor.getMetadataForSubject(subjectURI,
                predicates);
    }

    //RDFDataUnit interface old
    public Map<String, List<String>> getRDFMetadataForFile(String filePath,
            List<String> predicates) {
        return this.fileRDFMetadataExtractor.getMetadataForFilePath(filePath,
                predicates);
    }

    //RDFDataUnit interface old
    public OrderTupleQueryResultImpl executeOrderSelectQueryAsTuples(
            String orderSelectQuery) throws InvalidQueryException {

        OrderTupleQueryResultImpl result = new OrderTupleQueryResultImpl(
                orderSelectQuery, this);
        return result;
    }

    //WritableRDFDataUnit interface
    @Override
    public URI getWriteDataGraph() {
        return writeContext;
    }

    //WritableDataUnit interface
    @Override
    public void addAll(RDFDataUnit otherRDFDataUnit) {
        if (!this.getClass().equals(otherRDFDataUnit.getClass())) {
            throw new IllegalArgumentException("Incompatible DataUnit class. This DataUnit is of class "
                    + this.getClass().getCanonicalName() + " and it cannot merge other DataUnit of class " + otherRDFDataUnit.getClass().getCanonicalName() + ".");
        }

        RepositoryConnection connection = null;
        try {
            connection = getConnection();

            String targetGraphName = getWriteDataGraph().stringValue();
            for (URI sourceGraph : otherRDFDataUnit.getDataGraphnames()) {
                String sourceGraphName = sourceGraph.stringValue();

                LOG.info("Trying to merge {} triples from <{}> to <{}>.",
                        connection.size(sourceGraph), sourceGraphName,
                        targetGraphName);

                String mergeQuery = String.format("ADD <%s> TO <%s>", sourceGraphName,
                        targetGraphName);

                Update update = connection.prepareUpdate(
                        QueryLanguage.SPARQL, mergeQuery);

                update.execute();

                LOG.info("Merged {} triples from <{}> to <{}>.",
                        connection.size(sourceGraph), sourceGraphName,
                        targetGraphName);
            }
        } catch (MalformedQueryException ex) {
            LOG.error("NOT VALID QUERY: {}", ex);
        } catch (RepositoryException | DataUnitException | UpdateExecutionException ex) {
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

    //ManagableDataUnit interface
    @Override
    public void isReleaseReady() {
        int count = 0;
        for (RepositoryConnection connection : requestedConnections) {
            try {
                if (connection.isOpen()) {
                    count++;
                }
            } catch (RepositoryException ex) {
                try {
                    connection.close();
                } catch (RepositoryException ex1) {
                    LOG.warn("Error when closing connection", ex1);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }

        if (count > 0) {
            LOG.error("{} connections remained opened after DPU execution, dataUnitName '{}'.", count, this.getName());
        }
    }

    @Override
    //ManagableDataUnit interface
    public void clear() {
        /**
         * Beware! Clean is called from different thread then all other
         * operations (pipeline executor thread). That is the reason why we
         * cannot obtain connection using this.getConnection(), it would throw
         * an Exception. This connection has to be obtained directly from
         * repository and we take care to close it properly.
         */
        RepositoryConnection connection = null;
        try {
            connection = getConnectionInternal();
            connection.clear(writeContext);
        } catch (RepositoryException ex) {
            throw new RuntimeException("Could not delete repository", ex);
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

    //ManagableDataUnit interface
    @Override
    public void release() {
        List<RepositoryConnection> openedConnections = new ArrayList<>();
        for (RepositoryConnection connection : requestedConnections) {
            try {
                if (connection.isOpen()) {
                    openedConnections.add(connection);
                }
            } catch (RepositoryException ex) {
                try {
                    connection.close();
                } catch (RepositoryException ex1) {
                    LOG.warn("Error when closing connection", ex1);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }

        if (!openedConnections.isEmpty()) {
            LOG.error(String.valueOf(openedConnections.size()) + " connections remained opened after DPU execution.");
            for (RepositoryConnection connection : openedConnections) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }
    }

    //ManagableDataUnit interface
    @Override
    public void merge(DataUnit otherDataUnit) throws IllegalArgumentException, DataUnitException {
        if (!this.getClass().equals(otherDataUnit.getClass())) {
            throw new IllegalArgumentException("Incompatible DataUnit class. This DataUnit is of class "
                    + this.getClass().getCanonicalName() + " and it cannot merge other DataUnit of class " + otherDataUnit.getClass().getCanonicalName() + ".");
        }

        final RDFDataUnit otherRDFDataUnit = (RDFDataUnit) otherDataUnit;
        this.readContexts.addAll(otherRDFDataUnit.getDataGraphnames());
    }

    @Override
    public void store() {
        RepositoryConnection connection = null;
        try {
            connection = getConnectionInternal();
            ValueFactory valueFactory = connection.getValueFactory();
            for (URI context : this.getDataGraphnames()) {
                connection.add(valueFactory.createStatement(
                        this.getWriteDataGraph(),
                        valueFactory.createURI(DATA_UNIT_RDF_CONTAINSGRAPH_PREDICATE),
                        context),
                        valueFactory.createURI(DATA_UNIT_STORE_GRAPH));
            }
            connection.add(valueFactory.createStatement(
                    this.getWriteDataGraph(),
                    valueFactory.createURI(DATA_UNIT_RDF_WRITEGRAPH_PREDICATE),
                    this.writeContext),
                    valueFactory.createURI(DATA_UNIT_STORE_GRAPH));
        } catch (RepositoryException ex) {
            throw new RuntimeException(ex);
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
    public void load() {
        RepositoryConnection connection = null;
        try {
            connection = getConnectionInternal();

            ValueFactory valueFactory = connection.getValueFactory();
            RepositoryResult<Statement> result = connection.getStatements(this.getWriteDataGraph(),
                    valueFactory.createURI(DATA_UNIT_RDF_CONTAINSGRAPH_PREDICATE),
                    null,
                    false,
                    valueFactory.createURI(DATA_UNIT_STORE_GRAPH));
            while (result.hasNext()) {
                Statement contextStatement = result.next();
                this.readContexts.add(valueFactory.createURI(contextStatement.getObject().stringValue()));
            }
            RepositoryResult<Statement> writeContextResult = connection.getStatements(this.getWriteDataGraph(),
                    valueFactory.createURI(DATA_UNIT_RDF_WRITEGRAPH_PREDICATE),
                    null,
                    false,
                    valueFactory.createURI(DATA_UNIT_STORE_GRAPH));

            int i = 0;
            while (writeContextResult.hasNext()) {
                Statement writeContextStatement = writeContextResult.next();
                this.writeContext = valueFactory.createURI(writeContextStatement.getObject().stringValue());
                i++;
            }
            if ((i != 1) || (!readContexts.contains(writeContext))) {
                throw new RuntimeException("impossible");
            }
        } catch (RepositoryException ex) {
            throw new RuntimeException(ex);
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
