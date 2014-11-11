package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit.Entry;

public class SetFileIteration implements FilesDataUnit.Iteration {
    private static final Logger LOG = LoggerFactory.getLogger(SetFileIteration.class);

    private Iterator<FilesDataUnit.Entry> iterator = null;

    private Object lock = new Object();

    protected static final String SYMBOLIC_NAME_BINDING = "symbolicName";

    protected static final String FILE_URI_BINDING = "fileUri";

    protected static final String SELECT = "SELECT ?" + SYMBOLIC_NAME_BINDING + " ?" + FILE_URI_BINDING + " %s WHERE { "
            + "?s <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + ";"
            + "<" + FilesDataUnit.PREDICATE_FILE_URI + "> ?" + FILE_URI_BINDING + ". "
            + "}";

    public SetFileIteration(MetadataDataUnit metadataDataUnit) throws DataUnitException {
        Set<FilesDataUnit.Entry> internalSet = new LinkedHashSet<>();
        RepositoryConnection connection = null;
        try {
            // In this case we can select from multiple graphs.
            final StringBuilder fromPart = new StringBuilder();
            for (URI graph : metadataDataUnit.getMetadataGraphnames()) {
                fromPart.append("FROM <");
                fromPart.append(graph.stringValue());
                fromPart.append("> ");
            }

            final String selectQuery = String.format(SELECT, fromPart.toString());
            LOG.debug("SetFileIteration -> {}", selectQuery);

            connection = metadataDataUnit.getConnection();
            TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, selectQuery);
//            DatasetImpl dataset = new DatasetImpl();
//            for (URI metadataGraphName : metadataDataUnit.getMetadataGraphnames()) {
//                dataset.addDefaultGraph(metadataGraphName);
//            }
//            query.setDataset(dataset);
            TupleQueryResult queryResult = null;
            try {
                queryResult = query.evaluate();
                while (queryResult.hasNext()) {
                    BindingSet item = queryResult.next();
                    internalSet.add(new FilesDataUnitEntryImpl(item.getValue(SYMBOLIC_NAME_BINDING).stringValue(), item.getValue(FILE_URI_BINDING).stringValue()));
                }
            } catch (QueryEvaluationException ex) {
                throw new DataUnitException("Could not select all files from repository", ex);
            } finally {
                if (queryResult != null) {
                    try {
                        queryResult.close();
                    } catch (QueryEvaluationException ex) {
                        LOG.warn("Error in close.", ex);
                    }
                }
            }
        } catch (DataUnitException | RepositoryException | MalformedQueryException ex) {
            throw new DataUnitException("Could not iterate files", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error in close.", ex);
                }
            }
        }
        iterator = internalSet.iterator();
    }

    @Override
    public boolean hasNext() throws DataUnitException {
        synchronized (lock) {
            return iterator.hasNext();
        }
    }

    @Override
    public void close() throws DataUnitException {
    }

    @Override
    public Entry next() throws DataUnitException {
        synchronized (lock) {
            return iterator.next();
        }
    }
}
