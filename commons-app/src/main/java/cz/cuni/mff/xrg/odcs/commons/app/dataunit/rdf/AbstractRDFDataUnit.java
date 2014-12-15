package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf;

import java.util.concurrent.atomic.AtomicInteger;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
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

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.metadata.AbstractWritableMetadataDataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.helpers.dataunit.dataset.DatasetBuilder;

/**
 * Abstract class provides common parent methods for RDFDataUnit implementation.
 *
 * @author Jiri Tomes
 */
public abstract class AbstractRDFDataUnit extends AbstractWritableMetadataDataUnit implements ManagableRdfDataUnit {
    private static final String SYMBOLIC_NAME_BINDING = "symbolicName";

    private static final String DATA_GRAPH_BINDING = "dataGraph";

    private static final String UPDATE_EXISTING_GRAPH =
            "DELETE "
                    + "{ "
                    + "?s <" + RDFDataUnit.PREDICATE_DATAGRAPH_URI + "> ?o "
                    + "} "
                    + "INSERT "
                    + "{ "
                    + "?s <" + RDFDataUnit.PREDICATE_DATAGRAPH_URI + "> ?" + DATA_GRAPH_BINDING + " "
                    + "} "
                    + "WHERE "
                    + "{"
                    + "?s <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
                    + "?s <" + RDFDataUnit.PREDICATE_DATAGRAPH_URI + "> ?o "
                    + "}";

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRDFDataUnit.class);

    protected URI baseDataGraphURI;

    protected AtomicInteger atomicInteger = new AtomicInteger();

    public AbstractRDFDataUnit(String dataUnitName, String writeContextString) {
        super(dataUnitName, writeContextString);
        baseDataGraphURI = new URIImpl(writeContextString + "/baseDataGraph");
    }

    @Override
    public ManagableDataUnit.Type getType() {
        return ManagableDataUnit.Type.RDF;
    }

    @Override
    public boolean isType(ManagableDataUnit.Type dataUnitType) {
        return getType().equals(dataUnitType);
    }

    @Override
    public RDFDataUnit.Iteration getIteration() throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            LOG.info("More than more thread is accessing this data unit");
        }

        return new RDFDataUnitIterationImpl(this);
    }

    @Override
    public URI getBaseDataGraphURI() throws DataUnitException {
        return baseDataGraphURI;
    }

    @Override
    public void addExistingDataGraph(String symbolicName, URI existingDataGraphURI) throws DataUnitException {
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
            Statement statement2 = valueFactory.createStatement(
                    blankNodeId,
                    valueFactory.createURI(RDFDataUnit.PREDICATE_DATAGRAPH_URI),
                    existingDataGraphURI
                    );
            connection.add(statement, getMetadataWriteGraphname());
            connection.add(statement2, getMetadataWriteGraphname());
            connection.commit();
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error when adding data graph.", ex);
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
    public URI addNewDataGraph(String symbolicName) throws DataUnitException {
        URI generatedURI = new URIImpl(baseDataGraphURI.stringValue() + "/" + String.valueOf(atomicInteger.getAndIncrement()));
        this.addExistingDataGraph(symbolicName, generatedURI);
        return generatedURI;
    }

    @Override
    public void updateExistingDataGraph(String symbolicName, URI newDataGraphURI) throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            LOG.info("More than more thread is accessing this data unit");
        }

        RepositoryConnection connection = null;
        RepositoryResult<Statement> result = null;
        try {
            // TODO michal.klempa think of not connecting everytime
            connection = getConnectionInternal();
            connection.begin();
            ValueFactory valueFactory = connection.getValueFactory();
            Literal symbolicNameLiteral = valueFactory.createLiteral(symbolicName);
            try {
                Update update = connection.prepareUpdate(QueryLanguage.SPARQL, UPDATE_EXISTING_GRAPH);
                update.setBinding(SYMBOLIC_NAME_BINDING, symbolicNameLiteral);
                update.setBinding(DATA_GRAPH_BINDING, newDataGraphURI);

                update.setDataset(new DatasetBuilder()
                        .addDefaultGraph(getMetadataWriteGraphname())
                        .withInsertGraph(getMetadataWriteGraphname())
                        .addDefaultRemoveGraph(getMetadataWriteGraphname())
                        .build());
                update.execute();
            } catch (MalformedQueryException | UpdateExecutionException ex) {
                // Not possible
                throw new DataUnitException(ex);
            }
            connection.commit();
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error when adding data graph.", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                    // eat close exception, we cannot do anything clever here
                }
            }
            if (result != null) {
                try {
                    result.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error in close", ex);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }
    }
}
