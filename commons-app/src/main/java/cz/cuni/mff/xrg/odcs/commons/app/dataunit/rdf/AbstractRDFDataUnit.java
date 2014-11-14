package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.metadata.AbstractWritableMetadataDataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;

/**
 * Abstract class provides common parent methods for RDFDataUnit implementation.
 *
 */
public abstract class AbstractRDFDataUnit extends AbstractWritableMetadataDataUnit implements ManagableRdfDataUnit {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRDFDataUnit.class);

    /**
     * Base URI available to the user.
     */
    private final URI baseDataGraphURI;

    public AbstractRDFDataUnit(String dataUnitName, String writeContextString) {
        super(dataUnitName, writeContextString);
        baseDataGraphURI = new URIImpl(writeContextString + "/user/");
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
        checkForMultithreadAccess();
        return new RDFDataUnitIterationImpl(this);
    }

    @Override
    public URI getBaseDataGraphURI() throws DataUnitException {
        return baseDataGraphURI;
    }

    @Override
    public void addExistingDataGraph(String symbolicName, URI existingDataGraphURI) throws DataUnitException {
        checkForMultithreadAccess();
        RepositoryConnection connection = null;
        try {
            // TODO michal.klempa think of not connecting everytime
            connection = getConnectionInternal();
            connection.begin();
            final ValueFactory valueFactory = connection.getValueFactory();
            final URI subject = addEntry(symbolicName, connection);
            // Add graph uri.
            connection.add(
                    subject,
                    valueFactory.createURI(RDFDataUnit.PREDICATE_DATAGRAPH_URI),
                    existingDataGraphURI,
                    getMetadataWriteGraphname()
                    );
            connection.commit();
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error when adding data graph.", ex);
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
    public URI addNewDataGraph(String symbolicName) throws DataUnitException {
        checkForMultithreadAccess();
        RepositoryConnection connection = null;
        try {
            // TODO michal.klempa think of not connecting everytime
            connection = getConnectionInternal();
            connection.begin();
            final ValueFactory valueFactory = connection.getValueFactory();
            // Add pure entry.
            final URI subject = addEntry(symbolicName, connection);
            // Add graph uri - we use subject value as a graph name as we know that subject will
            // be unique always.
            connection.add(
                    subject,
                    valueFactory.createURI(RDFDataUnit.PREDICATE_DATAGRAPH_URI),
                    subject,
                    getMetadataWriteGraphname());
            connection.commit();
            return subject;
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error when adding data graph.", ex);
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
}
