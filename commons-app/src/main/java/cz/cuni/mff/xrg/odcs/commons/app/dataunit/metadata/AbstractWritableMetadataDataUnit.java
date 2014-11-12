package cz.cuni.mff.xrg.odcs.commons.app.dataunit.metadata;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
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

import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;
import eu.unifiedviews.helpers.dataunit.dataset.DatasetBuilder;

public abstract class AbstractWritableMetadataDataUnit implements WritableMetadataDataUnit, ManagableDataUnit {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractWritableMetadataDataUnit.class);

    public static final String DATA_UNIT_STORE_GRAPH = "http://unifiedviews.eu/AbstractWritableMetadataDataUnit/storeGraph";

    public static final String DATA_UNIT_RDF_CONTAINSGRAPH_PREDICATE = "http://unifiedviews.eu/AbstractWritableMetadataDataUnit/containsGraph";

    public static final String DATA_UNIT_RDF_WRITEGRAPH_PREDICATE = "http://unifiedviews.eu/AbstractWritableMetadataDataUnit/writeGraph";

    protected static final String SYMBOLIC_NAME_BINDING = "symbolicName";

    // TODO Update this query for virtuoso.
    protected static final String DUPLICATE_ENTRIES_QUERY = "SELECT DISTINCT ?" + SYMBOLIC_NAME_BINDING + " WHERE { "
            + "?subject1 <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
            + "?subject2 <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
            + "FILTER ( ?subject1 != ?subject2 ) }";

    protected String dataUnitName;

    protected URI writeContext;

    protected Set<URI> readContexts;

    protected List<RepositoryConnection> requestedConnections;

    protected Map<RepositoryConnection, String> requestedConnectionsSource;

    protected Thread ownerThread;

    public abstract RepositoryConnection getConnectionInternal() throws RepositoryException;

    public AbstractWritableMetadataDataUnit(String dataUnitName, String writeContextString) {
        this.dataUnitName = dataUnitName;
        this.writeContext = new URIImpl(writeContextString);
        this.readContexts = new HashSet<>();
        this.readContexts.add(this.writeContext);

        this.requestedConnections = new ArrayList<>();
        this.requestedConnectionsSource = new HashMap<>();
        this.ownerThread = Thread.currentThread();
    }

    // MetadataDataUnit interface
    @Override
    public RepositoryConnection getConnection() throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            LOG.info("More than more thread is accessing this data unit."
                    + " owner: {} current: {}", ownerThread.getName(),
                    Thread.currentThread().getName());
        }

        try {
            RepositoryConnection connection = getConnectionInternal();
            requestedConnections.add(connection);

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            new Exception("Stack trace").printStackTrace(printWriter);

            if (requestedConnectionsSource.containsKey(connection)) {
                LOG.error("Duplicit connection !!!");
            }
            requestedConnectionsSource.put(
                    connection, stringWriter.getBuffer().toString());

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
        if (!ownerThread.equals(Thread.currentThread())) {
            LOG.info("More than more thread is accessing this data unit");
        }

        RepositoryConnection connection = null;
        try {
            // TODO michal.klempa think of not connecting everytime
            connection = getConnectionInternal();
            connection.begin();
            ValueFactory valueFactory = connection.getValueFactory();
            BNode blankNodeId = valueFactory.createBNode();
            Statement statement = valueFactory.createStatement(
                    blankNodeId,
                    valueFactory.createURI(MetadataDataUnit.PREDICATE_SYMBOLIC_NAME),
                    valueFactory.createLiteral(symbolicName)
                    );
            connection.add(statement, getMetadataWriteGraphname());
            connection.commit();
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error when adding entry.", ex);
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
    public String getName() {
        return dataUnitName;
    }

    //ManagableDataUnit interface
    @Override
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
    public void isReleaseReady() {
        List<String> duplicateSymbolicNames = checkDuplicateEntries(new DatasetBuilder().withDefaultGraphs(readContexts).withNamedGraphs(readContexts).build());
        for (String duplicateSymbolicName : duplicateSymbolicNames) {
            LOG.error("Symbolic name " + duplicateSymbolicName
                    + " appears to be multiple times in RDF dataunit " + this.getName());
        }

        int count = 0;
        for (RepositoryConnection connection1 : requestedConnections) {
            try {
                if (connection1.isOpen()) {
                    count++;
                    // is open
                    LOG.error("Connection: is not closed connection opened on:\n{}",
                            requestedConnectionsSource.get(connection1));
                }
            } catch (RepositoryException ex) {
                try {
                    connection1.close();
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

        final AbstractWritableMetadataDataUnit otherRDFDataUnit = (AbstractWritableMetadataDataUnit) otherDataUnit;
        List<String> duplicateSymbolicNames = checkDuplicateEntries(new DatasetBuilder()
        .withDefaultGraphs(readContexts)
        .withNamedGraphs(readContexts)
        .withDefaultGraphs(otherRDFDataUnit.getMetadataGraphnames())
        .withNamedGraphs(otherRDFDataUnit.getMetadataGraphnames())
        .build());

        for (String duplicateSymbolicName : duplicateSymbolicNames) {
            LOG.error("Symbolic name " + duplicateSymbolicName
                    + " appears to be in " + this.getType() + " dataunit " + this.getName()
                    + " and also in merging " + this.getType() + " dataunit " + otherRDFDataUnit.getName());
        }
        if (!duplicateSymbolicNames.isEmpty()) {
            throw new DataUnitException("Duplicate symbolic names found when automerging data units, execution stop to prevent data lost");
        }
        this.readContexts.addAll(otherRDFDataUnit.getMetadataGraphnames());
    }

    //ManagableDataUnit interface
    @Override
    public void store() {
        RepositoryConnection connection = null;
        try {
            connection = getConnectionInternal();
            ValueFactory valueFactory = connection.getValueFactory();
            for (URI context : readContexts) {
                connection.add(valueFactory.createStatement(
                        writeContext,
                        valueFactory.createURI(DATA_UNIT_RDF_CONTAINSGRAPH_PREDICATE),
                        context),
                        valueFactory.createURI(DATA_UNIT_STORE_GRAPH));
            }
            connection.add(valueFactory.createStatement(
                    writeContext,
                    valueFactory.createURI(DATA_UNIT_RDF_WRITEGRAPH_PREDICATE),
                    writeContext),
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

    //ManagableDataUnit interface
    @Override
    public void load() {
        RepositoryConnection connection = null;
        try {
            connection = getConnectionInternal();

            ValueFactory valueFactory = connection.getValueFactory();
            RepositoryResult<Statement> result = connection.getStatements(this.getMetadataWriteGraphname(),
                    valueFactory.createURI(DATA_UNIT_RDF_CONTAINSGRAPH_PREDICATE),
                    null,
                    false,
                    valueFactory.createURI(DATA_UNIT_STORE_GRAPH));
            while (result.hasNext()) {
                Statement contextStatement = result.next();
                this.readContexts.add(valueFactory.createURI(contextStatement.getObject().stringValue()));
            }
            RepositoryResult<Statement> writeContextResult = connection.getStatements(this.getMetadataWriteGraphname(),
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
            if (i != 1 || !readContexts.contains(writeContext)) {
                throw new RuntimeException("impossible");
            }
        } catch (DataUnitException | RepositoryException ex) {
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

    private List<String> checkDuplicateEntries(Dataset dataset) {
        RepositoryConnection connection = null;
        List<String> resultList = new ArrayList<>();
        try {
            connection = getConnectionInternal();
            connection.begin();
            TupleQuery duplicateEntries = connection.prepareTupleQuery(QueryLanguage.SPARQL, DUPLICATE_ENTRIES_QUERY);
            duplicateEntries.setDataset(dataset);

            TupleQueryResult result = null;
            try {
                result = duplicateEntries.evaluate();
                if (result.hasNext()) {
                    while (result.hasNext()) {
                        BindingSet bindingSet = result.next();
                        String symbolicName = bindingSet.getValue(SYMBOLIC_NAME_BINDING).stringValue();
                        resultList.add(symbolicName);
                    }
                }
            } catch (QueryEvaluationException ex) {
                LOG.error("Error when checking duplicates.", ex);
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
        } catch (RepositoryException | MalformedQueryException ex) {
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
        return resultList;
    }
}
