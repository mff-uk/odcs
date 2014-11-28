package eu.unifiedviews.dataunit.rdf.impl;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import eu.unifiedviews.commons.dataunit.AbstractWritableMetadataDataUnit;
import eu.unifiedviews.commons.dataunit.ManagableDataUnit;
import eu.unifiedviews.commons.rdf.ConnectionSource;
import eu.unifiedviews.commons.rdf.FaultTolerantHelper;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;

/**
 * Abstract class provides common parent methods for RDFDataUnitImpl implementation.
 *
 */
class RDFDataUnitImpl extends AbstractWritableMetadataDataUnit implements ManageableWritableRDFDataUnit {

    /**
     * Base URI available to the user.
     */
    private final URI baseDataGraphURI;

    public RDFDataUnitImpl(String dataUnitName, String writeContextString, ConnectionSource connectionSource) {
        super(dataUnitName, writeContextString, connectionSource);
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

        if (connectionSource.isRetryOnFailure()) {
            return new RDFDataUnitIterationEager(this, connectionSource);
        } else {
            return new RDFDataUnitIterationLazy(this);
        }
    }

    @Override
    public URI getBaseDataGraphURI() throws DataUnitException {
        return baseDataGraphURI;
    }

    @Override
    public void addExistingDataGraph(final String symbolicName, final URI existingDataGraphURI) throws DataUnitException {
        checkForMultithreadAccess();

        final URI entrySubject = this.creatEntitySubject();
        try {
            // TODO michal.klempa think of not connecting everytime
            FaultTolerantHelper.execute(connectionSource, new FaultTolerantHelper.Code() {
                @Override
                public void execute(RepositoryConnection connection) throws DataUnitException, RepositoryException {
                    addEntry(entrySubject, symbolicName, connection);
                    final ValueFactory valueFactory = connection.getValueFactory();
                    // Add file uri.
                    connection.add(
                            entrySubject,
                            valueFactory.createURI(RDFDataUnitImpl.PREDICATE_DATAGRAPH_URI),
                            existingDataGraphURI,
                            getMetadataWriteGraphname()
                    );
                }
            });
        } catch (RepositoryException ex) {
            throw new DataUnitException("Problem with repositry.", ex);
        }
    }

    @Override
    public URI addNewDataGraph(final String symbolicName) throws DataUnitException {
        checkForMultithreadAccess();

        final URI entrySubject = this.creatEntitySubject();
        try {
            // TODO michal.klempa think of not connecting everytime
            FaultTolerantHelper.execute(connectionSource, new FaultTolerantHelper.Code() {
                @Override
                public void execute(RepositoryConnection connection) throws DataUnitException, RepositoryException {
                    addEntry(entrySubject, symbolicName, connection);
                    final ValueFactory valueFactory = connection.getValueFactory();
                    // Add file uri.
                    connection.add(
                            entrySubject,
                            valueFactory.createURI(RDFDataUnitImpl.PREDICATE_DATAGRAPH_URI),
                            entrySubject,
                            getMetadataWriteGraphname()
                    );
                }
            });
        } catch (RepositoryException ex) {
            throw new DataUnitException("Problem with repositry.", ex);
        }
        return entrySubject;
    }

}
