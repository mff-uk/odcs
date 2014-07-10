package cz.cuni.mff.xrg.odcs.files.copyhelper;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;

public class CopyHelpers {
    private static final CopyHelpers selfie = new CopyHelpers();

    private CopyHelpers() {

    }

    public static CopyHelper create(FilesDataUnit source, WritableFilesDataUnit destination) {
        return selfie.new CopyHelperImpl(source, destination);
    }

    public static void copyMetadataAndContents(String symbolicName, FilesDataUnit source, WritableFilesDataUnit destination) throws DataUnitException {
        CopyHelper helper = create(source, destination);
        helper.copyMetadataAndContents(symbolicName);
        helper.close();
    }

    public static void copyMetadata(String symbolicName, FilesDataUnit source, WritableFilesDataUnit destination) throws DataUnitException {
        CopyHelper helper = create(source, destination);
        helper.copyMetadata(symbolicName);
        helper.close();
    }

    private class CopyHelperImpl implements CopyHelper {
        private final Logger LOG = LoggerFactory.getLogger(CopyHelperImpl.class);

        private FilesDataUnit source;

        private WritableFilesDataUnit destination;

        private RepositoryConnection connection = null;

        public CopyHelperImpl(FilesDataUnit source, WritableFilesDataUnit destination) {
            this.source = source;
            this.destination = destination;
        }

        @Override
        public void copyMetadata(String symbolicName) throws DataUnitException {
            try {
               if (connection == null) {
                   connection = source.getConnection();
               }
               Update update = connection.prepareUpdate(QueryLanguage.SPARQL,"");
               update.execute();
            } catch (RepositoryException | UpdateExecutionException | MalformedQueryException ex) {
                throw new DataUnitException("", ex);
            } finally {
                
            }
        }

        @Override
        public void copyMetadataAndContents(String symbolicName) {

        }

        @Override
        public void close() throws DataUnitException {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error in close.", ex);
                }
            }
        }
    }
}
