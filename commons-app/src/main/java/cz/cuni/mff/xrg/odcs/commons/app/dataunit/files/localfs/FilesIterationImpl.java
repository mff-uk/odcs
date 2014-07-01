package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import java.util.NoSuchElementException;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit.Entry;
import cz.cuni.mff.xrg.odcs.files.WritableFilesDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.RDFData;

public class FilesIterationImpl implements WritableFilesDataUnit.WritableFilesIteration {
    private RepositoryConnection connection = null;

    private RepositoryConnection connection2 = null;

    private RepositoryResult<Statement> result = null;

    private RDFData backingStore = null;

    public FilesIterationImpl(RDFData backingStore) {
        this.backingStore = backingStore;
    }

    @Override
    public void remove() throws DataUnitException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry next() throws DataUnitException {
        if (result == null) {
            init();
        }
        RepositoryResult<Statement> result2 = null;
        try {
            Statement statement = result.next();
            result2 = connection2.getStatements(statement.getSubject(), connection.getValueFactory().createURI(FilesDataUnit.FILESYSTEM_URI_PREDICATE), null, false, backingStore.getContexts().toArray(new URI[0]));
            Statement filesytemURIStatement = result2.next();
            return new FilesDataUnitEntryImpl(statement.getObject().stringValue(), filesytemURIStatement.getObject().stringValue());
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error iterating underlying repository", ex);
        } catch (NoSuchElementException ex) {
            this.close();
            throw ex;
        } finally {
            try {
                if (result2 != null) {
                    result2.close();
                }
            } catch (RepositoryException ex) {
                throw new DataUnitException("Error closing result", ex);
            }
        }
    }

    @Override
    public boolean hasNext() throws DataUnitException {
        if (result == null) {
            init();
        }

        try {
            boolean hasNext = result.hasNext();
            if (!hasNext) {
                this.close();
            }
            return hasNext;
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error in hasNext", ex);
        }
    }

    @Override
    public void close() throws DataUnitException {
        try {
            result.close();
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error closing result", ex);
        }
        try {
            connection.close();
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error closing connection", ex);
        }
        try {
            connection2.close();
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error closing connection", ex);
        }
    }

    private void init() throws DataUnitException {
        if (result == null) {
            if (connection == null) {
                try {
                    connection = backingStore.getConnection();
                } catch (RepositoryException ex) {
                    throw new DataUnitException("Error when connecting to backing RDF store.", ex);
                }
            }
            if (connection2 == null) {
                try {
                    connection2 = backingStore.getConnection();
                } catch (RepositoryException ex) {
                    throw new DataUnitException("Error when connecting to backing RDF store.", ex);
                }
            }
            try {
                result = connection.getStatements(null, connection.getValueFactory().createURI(FilesDataUnit.SYMBOLIC_NAME_PREDICATE), null, false, backingStore.getContexts().toArray(new URI[0]));
            } catch (RepositoryException ex) {
                throw new DataUnitException("Error obtaining file list.", ex);
            }
        }
    }

}
