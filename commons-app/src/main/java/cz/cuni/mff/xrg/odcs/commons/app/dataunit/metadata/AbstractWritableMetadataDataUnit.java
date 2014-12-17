 package cz.cuni.mff.xrg.odcs.commons.app.dataunit.metadata;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.constants.Ontology;
import eu.unifiedviews.commons.dataunit.ManagableDataUnit;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;

public abstract class AbstractWritableMetadataDataUnit implements WritableMetadataDataUnit, ManagableDataUnit {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractWritableMetadataDataUnit.class);

    protected static final String SYMBOLIC_NAME_BINDING = "symbolicName";

    protected static final String PREDICATE_BINDING = "predicate";

    protected static final String OBJECT_BINDING = "object";

    /**
     * First %s stands for FROM clause place.
     */
    protected static final String DUPLICATE_ENTRIES_QUERY = "SELECT DISTINCT ?" + SYMBOLIC_NAME_BINDING + " %s WHERE { "
            + "?subject1 <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
            + "?subject2 <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
            + "FILTER ( ?subject1 != ?subject2 ) }";

    /**
     * First %s stands for write graph URI.
     */
    protected static final String ADD_METADATA_QUERY = "INSERT INTO <%s> { ?s ?" + PREDICATE_BINDING + " ?" + OBJECT_BINDING + " } "
                + "WHERE { "
                + "?s <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
                + " } ";

    /**
     * Name of this data unit.
     */
    protected String dataUnitName;

    /**
     * Name of assigned main graph. This graph is used to store information about entries.
     */
    protected URI writeContext;

    /**
     * Names of read graphs.
     */
    protected Set<URI> readContexts;

    /**
     * List of all requested connection.
     */
    protected Set<RepositoryConnection> requestedConnections;

    /**
     * Thread that creates this data unit.
     */
    protected Thread ownerThread;

    /**
     * Used to generate new entry URIs.
     */
    private final AtomicInteger entryCounter = new AtomicInteger(0);

    protected abstract RepositoryConnection getConnectionInternal() throws RepositoryException;

    public AbstractWritableMetadataDataUnit(String dataUnitName, String writeContextString) {
        this.dataUnitName = dataUnitName;
        this.writeContext = new URIImpl(writeContextString);
        this.readContexts = new HashSet<>();
        this.readContexts.add(this.writeContext);
        this.requestedConnections = new HashSet<>();
        this.ownerThread = Thread.currentThread();
    }

    // MetadataDataUnit interface
    @Override
    public RepositoryConnection getConnection() throws DataUnitException {
        checkForMultithreadAccess();

        try {
            // Get connection.
            final RepositoryConnection connection = getConnectionInternal();
            // And prepare the stack-trace.
            final StringWriter stringWriter = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(stringWriter);
            new Exception("Stack trace").printStackTrace(printWriter);
            // Same connection has been returned twice. This can be alright if some pooling
            // will be used. But such functionality is not used right now.
            if (requestedConnections.contains(connection)) {
                LOG.warn("Same connection returned twice!!!");
            }
            // TODO: In debug mode we should add this here to wath if the connection has been properly closed.
            //requestedConnections.add(connection);
            return connection;
        } catch (RepositoryException ex) {
            throw new DataUnitException(ex);
        }
    }

    // MetadataDataUnit interface
    @Override
    public Set<URI> getMetadataGraphnames() throws DataUnitException {
        return readContexts;
    }

    //WritableMetadataDataUnit
    @Override
    public URI getMetadataWriteGraphname() throws DataUnitException {
        return writeContext;
    }

    //WritableMetadataDataUnit
    @Override
    public void addEntry(String symbolicName) throws DataUnitException {
        checkForMultithreadAccess();

        RepositoryConnection connection = null;
        try {
            // TODO michal.klempa think of not connecting everytime
            connection = getConnectionInternal();
            connection.begin();
            addEntry(symbolicName, connection);
            connection.commit();
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error when adding entry.", ex);
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

    //ManagableDataUnit interface
    @Override
    public String getName() {
        return dataUnitName;
    }

    //ManagableDataUnit interface
    @Override
    public void clear() throws DataUnitException {
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
            // Just delete the entry graph as that is all what we got.
            connection.clear(writeContext);
        } catch (RepositoryException ex) {
            throw new DataUnitException("Could not clear metadata.", ex);
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

    //ManagableDataUnit interface
    @Override
    public void checkConsistency() throws DataUnitException{
        try {
            checkForDuplicitEntries(readContexts);
        } catch (DataUnitException ex) {
            throw new DataUnitException("Inconsistent state of data unit:" + dataUnitName, ex);
        }
        // Check for open connections.
        closeOpenedConnection();
    }

    //ManagableDataUnit interface
    @Override
    public void release() {
        // Just close the connections.
        closeOpenedConnection();
    }

    //ManagableDataUnit interface
    @Override
    public void merge(DataUnit otherDataUnit) throws IllegalArgumentException, DataUnitException {
        if (!this.getClass().equals(otherDataUnit.getClass())) {
            throw new IllegalArgumentException("Incompatible DataUnit class. This DataUnit is of class " + this.getClass().getCanonicalName() + " and it cannot merge other DataUnit of class " + otherDataUnit.getClass().getCanonicalName() + ".");
        }
        final AbstractWritableMetadataDataUnit otherMetadata = (AbstractWritableMetadataDataUnit) otherDataUnit;
        // What we need to do is just co replicate all symbolic names.
        final Set<URI> newReadSet = new HashSet<>(this.readContexts.size() + otherMetadata.readContexts.size());
        newReadSet.addAll(this.readContexts);
        newReadSet.addAll(otherMetadata.readContexts);
        checkForDuplicitEntries(newReadSet);
        // Merge read contexts.
        this.readContexts.addAll(otherMetadata.getMetadataGraphnames());
    }

    //ManagableDataUnit interface
    @Override
    public void store() {
        // Write context - read and write.
        RepositoryConnection connection = null;
        try {
            connection = getConnectionInternal();
            final ValueFactory valueFactory = connection.getValueFactory();
            // Sore all read contexts.
            for (URI context : readContexts) {
                connection.add(
                        writeContext,
                        valueFactory.createURI(Ontology.PREDICATE_METADATA_CONTEXT_READ),
                        context,
                        valueFactory.createURI(Ontology.GRAPH_METADATA));
            }
            // Just one write context.
            connection.add(
                    writeContext,
                    valueFactory.createURI(Ontology.PREDICATE_METADATA_CONTEXT_WRITE),
                    writeContext,
                    valueFactory.createURI(Ontology.GRAPH_METADATA));
            // And entry counter.
            connection.add(
                    writeContext,
                    valueFactory.createURI(Ontology.PREDICATE_METADATA_ENTRY_COUNTER),
                    valueFactory.createLiteral(entryCounter.intValue()),
                    valueFactory.createURI(Ontology.GRAPH_METADATA));
        } catch (RepositoryException ex) {
            throw new RuntimeException(ex);
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

    //ManagableDataUnit interface
    @Override
    public void load() throws DataUnitException {
        // Read context - read and write.
        RepositoryConnection connection = null;
        try {
            connection = getConnectionInternal();
            final ValueFactory valueFactory = connection.getValueFactory();
            final RepositoryResult<Statement> result = connection.getStatements(
                    this.getMetadataWriteGraphname(),
                    valueFactory.createURI(Ontology.PREDICATE_METADATA_CONTEXT_READ),
                    null,
                    false,
                    valueFactory.createURI(Ontology.GRAPH_METADATA));
            while (result.hasNext()) {
                Statement contextStatement = result.next();
                this.readContexts.add(valueFactory.createURI(contextStatement.getObject().stringValue()));
            }
            // Get read context - this a little bit strange as we already use this context
            // to read this data, but nothing bad should happen.
            final Value writeContextValue = getSingleObject(connection,
                    this.getMetadataWriteGraphname(),
                    valueFactory.createURI(Ontology.PREDICATE_METADATA_CONTEXT_WRITE),
                    valueFactory.createURI(Ontology.GRAPH_METADATA));
            if (writeContextValue instanceof URI) {
                this.writeContext = (URI)writeContextValue;
            } else {
                throw new DataUnitException("Write context must be a URI!");
            }
            // Get entry counter.
            final Value entryValue = getSingleObject(connection,
                    this.getMetadataWriteGraphname(),
                    valueFactory.createURI(Ontology.PREDICATE_METADATA_ENTRY_COUNTER),
                    valueFactory.createURI(Ontology.GRAPH_METADATA));
            if (entryValue instanceof Literal) {
                final Literal entryLiteral = (Literal)entryValue;
                try {
                    this.entryCounter.set(entryLiteral.intValue());
                } catch (NumberFormatException ex) {
                    throw new DataUnitException("Entry counter must be an integer!");
                }
            } else {
                throw new DataUnitException("Entry counter must be an integer!");
            }
        } catch (DataUnitException | RepositoryException ex) {
            throw new DataUnitException(ex);
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

    /**
     * Create a new entry.
     *
     * @param symbolicName
     * @param connection Connection used to add data. Will not be closed.
     * @return Subject of the new entry.
     * @throws DataUnitException
     */
    protected URI addEntry(String symbolicName, RepositoryConnection connection) throws DataUnitException {
        try {
            final ValueFactory valueFactory = connection.getValueFactory();
            // Prepare new URI.
            final URI entrySubject = valueFactory.createURI(
                    writeContext
                    + "/entry/"
                    + Integer.toString(entryCounter.incrementAndGet()));
            // Insert statement.
            connection.add(entrySubject,
                    valueFactory.createURI(MetadataDataUnit.PREDICATE_SYMBOLIC_NAME),
                    valueFactory.createLiteral(symbolicName),
                    getMetadataWriteGraphname());
            return entrySubject;
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error when adding entry.", ex);
        }
    }

    /**
     * Check if current thread is different from {@link #ownerThread} and if yes then log info message.
     */
    protected void checkForMultithreadAccess() {
        if (!ownerThread.equals(Thread.currentThread())) {
            LOG.info("More than more thread is accessing this data unit, owner: {} current: {}", ownerThread.getName(), Thread.currentThread().getName());
        }
    }

    /**
     * Close connection that were not closed by the data unit user.
     */
    private void closeOpenedConnection() {
        int count = 0;
        for (RepositoryConnection connection : requestedConnections) {
            try {
                if (connection.isOpen()) {
                    count++;
                    //LOG.error("Connection: is not closed connection opened on:\n{}", requestedConnections.get(connection));
                }
            } catch (RepositoryException ex) {
                try {
                    connection.close();
                } catch (RepositoryException ex1) {
                    LOG.warn("Error when closing connection", ex1);
                }
            }
        }

        if (count > 0) {
            LOG.error("{} connections remained opened after DPU execution, dataUnitName '{}'.", count, this.getName());
        }
    }

    /**
     * Check for duplicity for entry in given graphs.
     *
     * @param graphs
     */
    private void checkForDuplicitEntries(Set<URI> graphs) throws DataUnitException {
        // Prepare query.
        final StringBuilder fromClause = new StringBuilder(graphs.size() * 15);
        for (URI graph : graphs) {
            fromClause.append("FROM <");
            fromClause.append(graph.stringValue());
            fromClause.append(">");
        }
        final String queryWithGraphs = String.format(DUPLICATE_ENTRIES_QUERY, fromClause.toString());
        // Ask query and log result.
        boolean containDuplicity = false;
        RepositoryConnection connection = null;
        try {
            connection = getConnectionInternal();
            connection.begin();
            final TupleQuery duplicateEntries = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryWithGraphs);
            TupleQueryResult result = null;
            try {
                result = duplicateEntries.evaluate();
                while (result.hasNext()) {
                    final BindingSet bindingSet = result.next();
                    final String symbolicName = bindingSet.getValue(SYMBOLIC_NAME_BINDING).stringValue();
                    containDuplicity = true;
                    LOG.error("Duplicity entry found for symbollic name: %s", symbolicName);
                }
            } finally {
                if (result != null) {
                    try {
                        result.close();
                    } catch (QueryEvaluationException ex) {
                        LOG.warn("Error in close", ex);
                    }
                }
            }
            connection.commit();
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            LOG.error("Error when checking duplicates.", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                }
            }
        }
        if (containDuplicity) {
            throw new DataUnitException("Duplicate symbolic names found.");
        }
    }

    /**
     * Read and return value of a single object.
     * 
     * @param connection
     * @param subject
     * @param predicate
     * @param graph
     * @return
     * @throws RepositoryException
     * @throws DataUnitException In case that there is more than one object.
     */
    private Value getSingleObject(RepositoryConnection connection, URI subject, URI predicate, URI graph) throws RepositoryException, DataUnitException {
        final RepositoryResult<Statement> result = connection.getStatements(
                subject,
                predicate,
                null,
                false,
                graph);
        // Get the first.
        Value object = null;
        if (result.hasNext()) {
            object = result.next().getObject();
            if (result.hasNext()) {
                // More then one record.
                throw new DataUnitException("Multiple values for predicate: " + predicate.stringValue());
            }
            return object;
        }
        throw new DataUnitException("No value for required predicate: " + predicate.stringValue());
    }

}
