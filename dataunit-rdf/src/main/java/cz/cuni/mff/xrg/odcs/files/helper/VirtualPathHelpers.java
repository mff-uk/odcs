package cz.cuni.mff.xrg.odcs.files.helper;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.CleverDataset;

public class VirtualPathHelpers {
    private static final VirtualPathHelpers selfie = new VirtualPathHelpers();

    private VirtualPathHelpers() {
    }

    public static VirtualPathHelper create(FilesDataUnit filesDataUnit) {
        return selfie.new VirtualPathHelperImpl(filesDataUnit);
    }

    public static WritableVirtualPathHelper create(WritableFilesDataUnit writableFilesDataUnit) {
        return selfie.new WritableVirtualPathHelperImpl(writableFilesDataUnit);
    }

    public static String getVirtualPath(FilesDataUnit filesDataUnit, String symbolicName) throws DataUnitException {
        VirtualPathHelper helper = create(filesDataUnit);
        String result = helper.getVirtualPath(symbolicName);
        helper.close();
        return result;
    }

    public static void setVirtualPath(WritableFilesDataUnit writableFilesDataUnit, String symbolicName, String virtualPath) throws DataUnitException {
        WritableVirtualPathHelper helper = create(writableFilesDataUnit);
        helper.setVirtualPath(symbolicName, virtualPath);
        helper.close();
    }

    private class VirtualPathHelperImpl implements VirtualPathHelper {
        private final Logger LOG = LoggerFactory.getLogger(VirtualPathHelperImpl.class);

        private static final String VIRTUAL_PATH_BINDING_NAME = "virtualPath";

        private static final String SYMBOLIC_NAME_BINDING_NAME = "symbolicName";

        private static final String SELECT_VIRTUAL_PATH = "select ?" + VIRTUAL_PATH_BINDING_NAME + " where "
                + "{?subject " + FilesDataUnit.PREDICATE_SYMBOLIC_NAME + " ?" + SYMBOLIC_NAME_BINDING_NAME + " "
                + ". ?subject " + VirtualPathHelper.PREDICATE_VIRTUAL_PATH + " ?" + VIRTUAL_PATH_BINDING_NAME + " }";

        private FilesDataUnit filesDataUnit;

        private RepositoryConnection connection = null;

        public VirtualPathHelperImpl(FilesDataUnit filesDataUnit) {
            this.filesDataUnit = filesDataUnit;
        }

        @Override
        public String getVirtualPath(String symbolicName) throws DataUnitException {
            TupleQueryResult tupleQueryResult = null;
            String result = null;
            try {
                if (connection == null) {
                    connection = filesDataUnit.getConnection();
                }
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, SELECT_VIRTUAL_PATH);
                tupleQuery.setBinding(SYMBOLIC_NAME_BINDING_NAME, connection.getValueFactory().createLiteral(symbolicName));
                CleverDataset dataset = new CleverDataset();
                dataset.addDefaultGraphs(filesDataUnit.getMetadataGraphnames());
                tupleQuery.setDataset(dataset);
                tupleQueryResult = tupleQuery.evaluate();
                if (tupleQueryResult.hasNext()) {
                    BindingSet bindingSet = tupleQueryResult.next();
                    result = bindingSet.getBinding(VIRTUAL_PATH_BINDING_NAME).getValue().stringValue();
                }
            } catch (QueryEvaluationException | RepositoryException | MalformedQueryException ex) {
                throw new DataUnitException("", ex);
            } finally {
                if (tupleQueryResult != null) {
                    try {
                        tupleQueryResult.close();
                    } catch (QueryEvaluationException ex) {
                        LOG.warn("Error in close.", ex);
                    }
                }
            }
            return result;
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

    private class WritableVirtualPathHelperImpl implements WritableVirtualPathHelper {
        private final Logger LOG = LoggerFactory.getLogger(WritableVirtualPathHelperImpl.class);

        private static final String VIRTUAL_PATH_BINDING_NAME = "virtualPath";

        private static final String SYMBOLIC_NAME_BINDING_NAME = "symbolicName";

        private static final String UPDATE_VIRTUAL_PATH = "select ?" + VIRTUAL_PATH_BINDING_NAME + " where "
                + "{?subject " + FilesDataUnit.PREDICATE_SYMBOLIC_NAME + " ?" + SYMBOLIC_NAME_BINDING_NAME + " "
                + ". ?subject " + VirtualPathHelper.PREDICATE_VIRTUAL_PATH + " ?" + VIRTUAL_PATH_BINDING_NAME + " }";

        private WritableFilesDataUnit writableFilesDataUnit;

        private VirtualPathHelper virtualPathHelper;

        private RepositoryConnection connection = null;

        public WritableVirtualPathHelperImpl(WritableFilesDataUnit writableFilesDataUnit) {
            this.writableFilesDataUnit = writableFilesDataUnit;
            this.virtualPathHelper = new VirtualPathHelperImpl(writableFilesDataUnit);
        }

        @Override
        public String getVirtualPath(String symbolicName) throws DataUnitException {
            return this.virtualPathHelper.getVirtualPath(symbolicName);
        }

        @Override
        public void setVirtualPath(String symbolicName, String virtualPath) throws DataUnitException {
            try {
                if (connection == null) {
                    connection = writableFilesDataUnit.getConnection();
                }
                Update update = connection.prepareUpdate(QueryLanguage.SPARQL, UPDATE_VIRTUAL_PATH);
                update.setBinding(SYMBOLIC_NAME_BINDING_NAME, connection.getValueFactory().createLiteral(symbolicName));
                CleverDataset dataset = new CleverDataset();
                dataset.addDefaultGraphs(writableFilesDataUnit.getMetadataGraphnames());
                update.setDataset(dataset);
                update.execute();
            } catch (RepositoryException | MalformedQueryException | UpdateExecutionException ex) {
                throw new DataUnitException("", ex);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (RepositoryException ex) {
                        LOG.warn("Error in close.", ex);
                    }
                }
            }
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
