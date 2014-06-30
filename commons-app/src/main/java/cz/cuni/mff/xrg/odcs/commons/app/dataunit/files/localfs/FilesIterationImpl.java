package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import java.util.NoSuchElementException;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit.FilesDataUnitEntry;
import cz.cuni.mff.xrg.odcs.files.WritableFilesDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.RDFData;

public class FilesIterationImpl implements WritableFilesDataUnit.WritableFilesIteration {
    private RepositoryConnection connection = null;

    private RepositoryResult<Statement> result = null;
    
    private RDFData backingStore = null;
    
    private String symbolicNamePredicate;
    
    public FilesIterationImpl(RDFData backingStore, String symbolicNamePredicate) {
        this.backingStore = backingStore;
        this.symbolicNamePredicate = symbolicNamePredicate;
    }
    
    @Override
    public void remove() throws DataUnitException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilesDataUnitEntry next() throws DataUnitException {
        if (result == null) {
            init();
        }

        try {
            Statement statement = result.next();
            return new FilesDataUnitEntryImpl(statement.getObject().stringValue(), statement.getSubject().stringValue());
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error iterating underlying repository", ex);
        } catch (NoSuchElementException ex) {
            this.close();
            throw ex;
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
            try {
                result = connection.getStatements(null, connection.getValueFactory().createURI(symbolicNamePredicate), null, false, backingStore.getContexts().toArray(new URI[0]));
            } catch (RepositoryException ex) {
                throw new DataUnitException("Error obtaining file list.", ex);
            }
        }
    }

}
