package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.openrdf.model.BNode;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.metadata.AbstractWritableMetadataDataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.helpers.dataunit.rdfhelper.RDFHelper;

/**
 * Abstract class provides common parent methods for RDFDataUnit implementation.
 * 
 * @author Jiri Tomes
 */
public abstract class AbstractRDFDataUnit extends AbstractWritableMetadataDataUnit implements ManagableRdfDataUnit {

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

    // TODO move to helper
    @Override
    public Set<URI> getDataGraphnames() throws DataUnitException {
        RDFDataUnit.Iteration iteration = this.getIteration();
        Set<URI> resultSet = new LinkedHashSet<>();
        try {
            while (iteration.hasNext()) {
                RDFDataUnit.Entry entry = iteration.next();
                resultSet.add(entry.getDataGraphURI());
            }
        } finally {
            iteration.close();
        }
        return resultSet;
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
            // TODO michal.klempa - add one query at isReleaseReady instead of this
//            BooleanQuery fileExistsQuery = connection.prepareBooleanQuery(QueryLanguage.SPARQL, String.format(FILE_EXISTS_ASK_QUERY, proposedSymbolicName));
//            if (fileExistsQuery.evaluate()) {
//                connection.rollback();
//                throw new IllegalArgumentException("File with symbolic name "
//                        + proposedSymbolicName + " already exists in scope of this data unit. Symbolic name must be unique.");
//            }
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
    public void addAll(RDFDataUnit otherRDFDataUnit) throws DataUnitException {
        if (!this.getClass().equals(otherRDFDataUnit.getClass())) {
            throw new IllegalArgumentException("Incompatible DataUnit class. This DataUnit is of class "
                    + this.getClass().getCanonicalName() + " and it cannot merge other DataUnit of class " + otherRDFDataUnit.getClass().getCanonicalName() + ".");
        }

        RepositoryConnection connection = null;
        try {
            connection = getConnection();

            String targetGraphName = getBaseDataGraphURI().stringValue();
            for (URI sourceGraph : RDFHelper.getGraphsArray(otherRDFDataUnit)) {
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
}
