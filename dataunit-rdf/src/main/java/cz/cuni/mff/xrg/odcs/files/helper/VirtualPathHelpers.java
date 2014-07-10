package cz.cuni.mff.xrg.odcs.files.helper;

import java.sql.Statement;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;

public abstract class VirtualPathHelpers {
    public VirtualPathHelper create(FilesDataUnit filesDataUnit) {
        return new VirtualPathHelperImpl(filesDataUnit);
    }

    private class VirtualPathHelperImpl implements VirtualPathHelper {
        private final Logger LOG = LoggerFactory.getLogger(VirtualPathHelperImpl.class);
        private FilesDataUnit filesDataUnit;

        public VirtualPathHelperImpl(FilesDataUnit filesDataUnit) {
            this.filesDataUnit = filesDataUnit;
        }

        @Override
        public String getVirtualPath(String symbolicName) throws DataUnitException {
            RepositoryConnection connection = null;
            RepositoryResult<Statement> repositoryResult = null;
            try {
                connection = filesDataUnit.getConnection();
//                repositoryResult = connection.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT WHERE { ");
            } finally {
                if (repositoryResult != null) {
                    try {
                        repositoryResult.close();
                    } catch (RepositoryException ex) {
                        LOG.warn("Error in close.", ex);
                    }
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (RepositoryException ex) {
                        LOG.warn("Error in close.", ex);
                    }
                }
            }
            
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setVirtualPath(String symbolicName, String virtualPath) {
            // TODO Auto-generated method stub
            
        }
    }
}
