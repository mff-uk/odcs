package cz.cuni.mff.xrg.odcs.frontend.container.rdf;

import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.enums.SelectFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.repositories.MyGraphQueryResult;
import cz.cuni.mff.xrg.odcs.rdf.repositories.MyRDFHandler;
import info.aduna.iteration.Iterations;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.*;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.openrdf.model.URI;

public class RepositoryFrontendHelper {
    private static final Logger log = LoggerFactory.getLogger(RepositoryFrontendHelper.class);

    public static File executeSelectQuery(RepositoryConnection connection, String selectQuery, String filePath,
            SelectFormatType selectType, URI dataGraph) throws InvalidQueryException {
        try {

            TupleQuery tupleQuery = connection.prepareTupleQuery(
                    QueryLanguage.SPARQL, selectQuery);
            DatasetImpl dataSet = new DatasetImpl();
            dataSet.addDefaultGraph(dataGraph);
            dataSet.addNamedGraph(dataGraph);
            tupleQuery.setDataset(dataSet);

            log.debug("Query {} is valid.", selectQuery);

            File file = new File(filePath);
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.debug(e.getMessage());
            }

            FileOutputStream os = new FileOutputStream(file);

            TupleQueryResultWriter tupleHandler;

            switch (selectType) {
                case XML:
                    tupleHandler = new SPARQLResultsXMLWriter(os);
                    break;
                case CSV:
                    tupleHandler = new SPARQLResultsCSVWriter(os);
                    break;
                case JSON:
                    tupleHandler = new SPARQLResultsJSONWriter(os);
                    break;
                case TSV:
                    tupleHandler = new SPARQLResultsTSVWriter(os);
                    break;
                default:
                    tupleHandler = new SPARQLResultsXMLWriter(os);

            }

            tupleQuery.evaluate(tupleHandler);
            return file;

        } catch (QueryEvaluationException | MalformedQueryException ex) {
            throw new InvalidQueryException(
                    "This query is probably not valid. " + ex.getMessage(),
                    ex);
        } catch (TupleQueryResultHandlerException ex) {
            log.error("Writing result to file fail. {}", ex.getMessage(),
                    ex);

        } catch (RepositoryException ex) {
            log.error("Connection to RDF repository failed. {}",
                    ex.getMessage(), ex);
        } catch (IOException ex) {
            log.error("Stream were not closed. {}", ex.getMessage(), ex);
        }

        throw new InvalidQueryException(
                "Creating File with RDF data fault.");

    }

    /**
     * Make select query over repository data and return tables as result.
     * 
     * @param selectQuery
     *            String representation of SPARQL select query.
     * @return <code>Map&lt;String,List&lt;String&gt;&gt;</code> as table, where
     *         map key is column name * * and <code>List&lt;String&gt;</code> are string values in this column. When query is invalid, return *
     *         * * * empty <code>Map</code>.
     * @throws InvalidQueryException
     *             when query is not valid.
     */
    public static Map<String, List<String>> executeSelectQuery(RepositoryConnection connection,
            String selectQuery, URI dataGraph)
            throws InvalidQueryException {

        Map<String, List<String>> map = new LinkedHashMap<>();

        List<BindingSet> listBindings = new ArrayList<>();
        TupleQueryResult result = null;
        try {
            result = executeSelectQueryAsTuples(connection, selectQuery, dataGraph);

            List<String> names = result.getBindingNames();

            for (String name : names) {
                map.put(name, new LinkedList<String>());
            }

            listBindings = Iterations.asList(result);
        } catch (QueryEvaluationException ex) {
            throw new InvalidQueryException(
                    "This query is probably not valid. " + ex
                            .getMessage(),
                    ex);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (QueryEvaluationException ex) {
                    log.warn("Failed to close RDF tuple result. {}",
                            ex.getMessage(), ex);
                }
            }
        }

        for (BindingSet bindingNextSet : listBindings) {
            for (Binding next : bindingNextSet) {

                String name = next.getName();
                Value value = next.getValue();

                String stringValue;

                if (value != null) {
                    stringValue = value.stringValue();
                } else {
                    stringValue = "";
                }

                if (map.containsKey(name)) {
                    map.get(name).add(stringValue);
                }

            }
        }

        return map;
    }

    /**
     * Make select query over repository data and return MyTupleQueryResult
     * class as result.
     * 
     * @param selectQuery
     *            String representation of SPARQL select query.
     * @return MyTupleQueryResult representation of SPARQL select query.
     * @throws InvalidQueryException
     *             when query is not valid.
     */
    public static TupleQueryResult executeSelectQueryAsTuples(RepositoryConnection connection,
            String selectQuery, URI dataGraph)
            throws InvalidQueryException {

        try {

            TupleQuery tupleQuery = connection.prepareTupleQuery(
                    QueryLanguage.SPARQL, selectQuery);
            DatasetImpl dataSet = new DatasetImpl();
            dataSet.addDefaultGraph(dataGraph);
            dataSet.addNamedGraph(dataGraph);
            tupleQuery.setDataset(dataSet);

            log.debug("Query {} is valid.", selectQuery);

            try {
                TupleQueryResult tupleResult = tupleQuery.evaluate();
                log.debug(
                        "Query {} has not null result.", selectQuery);

                return tupleResult;

            } catch (QueryEvaluationException ex) {
                throw new InvalidQueryException(
                        "This query is probably not valid. " + ex
                                .getMessage(),
                        ex);
            }

        } catch (MalformedQueryException ex) {
            throw new InvalidQueryException(
                    "This query is probably not valid. "
                            + ex.getMessage(), ex);
        } catch (RepositoryException ex) {
            log.error("Connection to RDF repository failed. {}",
                    ex.getMessage(), ex);
        }
        throw new InvalidQueryException(
                "Getting TupleQueryResult using SPARQL select query failed.");
    }

    /**
     * Returns graph contains all RDF triples as result of describe query for
     * given Resource URI. If graph is empty, there is are no triples for
     * describing.
     * 
     * @param uriResource
     *            Subject or object URI as resource use to describe it.
     * @return Graph contains all RDF triples as result of describe query for
     *         given Resource URI. If graph is empty, there is are no triples
     *         for describing.
     * @throws InvalidQueryException
     *             if resource is not URI type (e.g.
     *             BlankNode, some type of Literal (in object
     *             case))
     */
    public static Graph describeURI(RepositoryConnection connection, URI dataGraph, Resource uriResource) throws InvalidQueryException {

        if (uriResource instanceof URI) {
            String describeQuery = String.format("DESCRIBE <%s>", uriResource
                    .toString());

            Graph result = executeConstructQuery(connection, dataGraph, describeQuery);

            return result;
        } else {
            throw new InvalidQueryException(
                    "Resource " + uriResource.toString() + "is not URI type");
        }

    }

    /**
     * Make construct query over repository data and return interface Graph as
     * result contains iterator for statements (triples).
     * 
     * @param constructQuery
     *            String representation of SPARQL query.
     * @return Interface Graph as result of construct SPARQL query.
     * @throws InvalidQueryException
     *             when query is not valid.
     */
    public static Graph executeConstructQuery(RepositoryConnection connection, URI dataGraph, String constructQuery) throws InvalidQueryException {
        DatasetImpl dataSet = new DatasetImpl();
        dataSet.addDefaultGraph(dataGraph);
        dataSet.addNamedGraph(dataGraph);
        return executeConstructQuery(connection, constructQuery, dataSet);
    }

    /**
     * Make construct query over graph URIs in dataSet and return interface
     * Graph as result contains iterator for statements (triples).
     * 
     * @param constructQuery
     *            String representation of SPARQL query.
     * @param dataSet
     *            Set of graph URIs used for construct query.
     * @return Interface Graph as result of construct SPARQL query.
     * @throws InvalidQueryException
     *             when query is not valid.
     */
    public static Graph executeConstructQuery(RepositoryConnection connection, String constructQuery, Dataset dataSet)
            throws InvalidQueryException {

        try {

            GraphQuery graphQuery = connection.prepareGraphQuery(
                    QueryLanguage.SPARQL,
                    constructQuery);

            graphQuery.setDataset(dataSet);

            log.debug("Query {} is valid.", constructQuery);

            try {

                MyGraphQueryResult result = new MyGraphQueryResult(graphQuery
                        .evaluate());

                log.debug(
                        "Query {} has not null result.", constructQuery);
                return result.asGraph();

            } catch (QueryEvaluationException ex) {
                throw new InvalidQueryException(
                        "This query is probably not valid. " + ex
                                .getMessage(),
                        ex);
            }

        } catch (MalformedQueryException ex) {
            throw new InvalidQueryException(
                    "This query is probably not valid. "
                            + ex.getMessage(), ex);
        } catch (RepositoryException ex) {
            log.error("Connection to RDF repository failed. {}",
                    ex.getMessage(), ex);
        }

        throw new InvalidQueryException(
                "Getting GraphQueryResult using SPARQL construct query failed.");
    }

    /**
     * Make construct query over repository data and return file where RDF data
     * as result are saved.
     * 
     * @param constructQuery
     *            String representation of SPARQL query.
     * @param formatType
     *            Choosed type of format RDF data in result.
     * @param filePath
     *            String path to file where result with RDF data is
     *            stored.
     * @return File with RDF data in defined format as result of construct
     *         query.
     * @throws InvalidQueryException
     *             when query is not valid or creating file
     *             fail.
     */
    public static File executeConstructQuery(RepositoryConnection connection, URI dataGraph, String constructQuery,
            RDFFormatType formatType, String filePath) throws InvalidQueryException {

        try {

            GraphQuery graphQuery = connection.prepareGraphQuery(
                    QueryLanguage.SPARQL,
                    constructQuery);
            DatasetImpl dataSet = new DatasetImpl();
            dataSet.addDefaultGraph(dataGraph);
            dataSet.addNamedGraph(dataGraph);
            graphQuery.setDataset(dataSet);

            log.debug("Query {} is valid.", constructQuery);

            try {

                File file = new File(filePath);
                MyRDFHandler goal = getHandlerForConstructQuery(file, formatType);

                graphQuery.evaluate(goal);

                log.debug(
                        "Query {} has not null result.", constructQuery);

                return file;

            } catch (QueryEvaluationException ex) {
                throw new InvalidQueryException(
                        "This query is probably not valid. " + ex
                                .getMessage(),
                        ex);
            } catch (IOException ex) {
                log.error("Problems with file stream : {}", ex.getMessage(),
                        ex);
            }

        } catch (MalformedQueryException ex) {
            throw new InvalidQueryException(
                    "This query is probably not valid. "
                            + ex.getMessage(), ex);
        } catch (RepositoryException ex) {
            log.error("Connection to RDF repository failed. {}", ex
                    .getMessage(), ex);
        } catch (RDFHandlerException ex) {
            log.error("RDF handler failed. " + ex.getMessage(), ex);
        }

        throw new InvalidQueryException(
                "Creating File with RDF data fault.");
    }

    private static MyRDFHandler getHandlerForConstructQuery(File file,
            RDFFormatType formatType) throws IOException {

        try {
            file.createNewFile();
        } catch (IOException e) {
            log.debug(e.getMessage());
        }

        FileOutputStream os = new FileOutputStream(file);

        MyRDFHandler goal = new MyRDFHandler(os, formatType);

        return goal;

    }

}
