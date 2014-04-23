package cz.cuni.mff.xrg.odcs.rdf.repositories;

import info.aduna.iteration.Iterations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.model.Graph;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResults;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.ParseErrorListener;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.rdf.GraphUrl;
import cz.cuni.mff.xrg.odcs.rdf.enums.MyRDFHandler;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import cz.cuni.mff.xrg.odcs.rdf.enums.SelectFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.handlers.StatisticalHandler;
import cz.cuni.mff.xrg.odcs.rdf.handlers.TripleCountHandler;
import cz.cuni.mff.xrg.odcs.rdf.impl.MyGraphQueryResult;
import cz.cuni.mff.xrg.odcs.rdf.impl.OrderTupleQueryResultImpl;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.metadata.FileRDFMetadataExtractor;
import cz.cuni.mff.xrg.odcs.rdf.query.utils.QueryPart;

/**
 * Abstract class provides common parent methods for RDFDataUnit implementation.
 *
 * @author Jiri Tomes
 */
public abstract class BaseRDFRepo implements ManagableRdfDataUnit {

	private FileRDFMetadataExtractor fileRDFMetadataExtractor;

	/**
	 * Default name for graph using for store RDF data.
	 */
	protected static final String DEFAULT_GRAPH_NAME = "http://default";

	/**
	 * Logging information about execution of method using openRDF.
	 */
	private static final Logger logger = LoggerFactory.getLogger(BaseRDFRepo.class); 

	/**
	 * Graph resource for saving RDF triples.
	 */
	protected URI dataGraph;

	/**
	 * Default used encoding.
	 */
	protected final String encode = "UTF-8";

	public BaseRDFRepo() {
		this.fileRDFMetadataExtractor = new FileRDFMetadataExtractor(this);
	}

	@Override
	public Map<String, List<String>> getRDFMetadataForSubjectURI(
			String subjectURI, List<String> predicates) {
		return this.fileRDFMetadataExtractor.getMetadataForSubject(subjectURI,
				predicates);
	}

	@Override
	public Map<String, List<String>> getRDFMetadataForFile(String filePath,
			List<String> predicates) {
		return this.fileRDFMetadataExtractor.getMetadataForFilePath(filePath,
				predicates);
	}

	/**
	 * Transform RDF in repository by SPARQL updateQuery.
	 *
	 * @param updateQuery String value of update SPARQL query.
	 * @throws RDFException when transformation fault.
	 */
	@Override
	public void executeSPARQLUpdateQuery(String updateQuery)
			throws RDFException {
        DatasetImpl dataSet = new DatasetImpl();
        dataSet.addDefaultGraph(this.getDataGraph());
        dataSet.addNamedGraph(this.getDataGraph());
		executeSPARQLUpdateQuery(updateQuery, dataSet);
	}

	/**
	 * Transform RDF in repository by SPARQL updateQuery.
	 *
	 * @param updateQuery String value of update SPARQL query.
	 * @param dataset     Set of graph URIs used for update query.
	 * @throws RDFException when transformation fault.
	 */
	@Override
	public void executeSPARQLUpdateQuery(String updateQuery, Dataset dataset)
			throws RDFException {

		try {
			RepositoryConnection connection = getConnection();

			String newUpdateQuery = AddGraphToUpdateQuery(updateQuery);
			Update myupdate = connection.prepareUpdate(QueryLanguage.SPARQL,
					newUpdateQuery);
			myupdate.setDataset(dataset);

			logger.debug(
					"This SPARQL update query is valid and prepared for execution:");
			logger.debug(newUpdateQuery);

			myupdate.execute();
			//connection.commit();

			logger.debug("SPARQL update query for was executed successfully");

		} catch (MalformedQueryException e) {

			logger.debug(e.getMessage());
			throw new RDFException(e.getMessage(), e);

		} catch (UpdateExecutionException ex) {

			final String message = "SPARQL query was not executed !!!";
			logger.debug(message);
			logger.debug(ex.getMessage());

			throw new RDFException(message + ex.getMessage(), ex);


		} catch (RepositoryException ex) {
			throw new RDFException(
					"Connection to repository is not available. "
					+ ex.getMessage(), ex);
		}

	}

	/**
	 *
	 * @return List of all application graphs keeps in Virtuoso storage in case
	 *         of Virtuoso repository. When is used local repository as storage,
	 *         this method return an empty list.
	 */
	@Override
	public List<String> getApplicationGraphs() {
		List<String> result = new ArrayList<>();

		try {
			String select = "select distinct ?g where {graph ?g {?s ?p ?o}}";
			TupleQueryResult tupleResult = executeSelectQueryAsTuples(select);

			String prefix = GraphUrl.getGraphPrefix();

			for (BindingSet set : Iterations.asList(tupleResult)) {

				for (String name : set.getBindingNames()) {
					String graphName = set.getValue(name).stringValue();

					if (graphName.startsWith(prefix)) {
						result.add(graphName);
					}
				}
			}
			tupleResult.close();
		} catch (InvalidQueryException | QueryEvaluationException e) {
			logger.debug(e.getMessage());
		}

		return result;
	}

	/**
	 * Delete all application graphs keeps in Virtuoso storage in case of
	 * Virtuoso repository. When is used local repository as storage, this
	 * method has no effect.
	 *
	 * @return Info string message about removing application graphs.
	 */
	@Override
	public String deleteApplicationGraphs() {

		List<String> graphs = getApplicationGraphs();

		String returnMessage;

		if (graphs.isEmpty()) {
			returnMessage = "NO APPLICATIONS GRAPHS to DELETE";
			logger.info(returnMessage);
		} else {
			for (String nextGraph : graphs) {
				deleteNamedGraph(nextGraph);
			}
			returnMessage = "TOTAL deleted: " + graphs.size() + " application graphs";
			logger.info(returnMessage);

		}

		return returnMessage;
	}

	private void deleteNamedGraph(String graphName) {

		String deleteQuery = String.format("CLEAR GRAPH <%s>", graphName);
		try {
			executeSPARQLUpdateQuery(deleteQuery);
			logger.info("Graph {} was sucessfully deleted", graphName);
		} catch (RDFException e) {
			logger.debug(e.getMessage());
		}

	}

	private MyRDFHandler getHandlerForConstructQuery(File file,
			RDFFormatType formatType) throws IOException {

		createNewFile(file);

		FileOutputStream os = new FileOutputStream(file);

		MyRDFHandler goal = new MyRDFHandler(os, formatType);

		return goal;

	}

	/**
	 * Make construct query over repository data and return file where RDF data
	 * as result are saved.
	 *
	 * @param constructQuery String representation of SPARQL query.
	 * @param formatType     Choosed type of format RDF data in result.
	 * @param filePath       String path to file where result with RDF data is
	 *                       stored.
	 * @return File with RDF data in defined format as result of construct
	 *         query.
	 * @throws InvalidQueryException when query is not valid or creating file
	 *                               fail.
	 */
	@Override
	public File executeConstructQuery(String constructQuery,
			RDFFormatType formatType, String filePath) throws InvalidQueryException {

		try {
			RepositoryConnection connection = getConnection();

			GraphQuery graphQuery = connection.prepareGraphQuery(
					QueryLanguage.SPARQL,
					constructQuery);
            DatasetImpl dataSet = new DatasetImpl();
            dataSet.addDefaultGraph(this.getDataGraph());
            dataSet.addNamedGraph(this.getDataGraph());
			graphQuery.setDataset(dataSet);

			logger.debug("Query {} is valid.", constructQuery);

			try {

				File file = new File(filePath);
				MyRDFHandler goal = getHandlerForConstructQuery(file, formatType);

				graphQuery.evaluate(goal);

				logger.debug(
						"Query {} has not null result.", constructQuery);

				return file;

			} catch (QueryEvaluationException ex) {
				throw new InvalidQueryException(
						"This query is probably not valid. " + ex
						.getMessage(),
						ex);
			} catch (IOException ex) {
				logger.error("Problems with file stream : {}", ex.getMessage(),
						ex);
			}

		} catch (MalformedQueryException ex) {
			throw new InvalidQueryException(
					"This query is probably not valid. "
					+ ex.getMessage(), ex);
		} catch (RepositoryException ex) {
			logger.error("Connection to RDF repository failed. {}", ex
					.getMessage(), ex);
		} catch (RDFHandlerException ex) {
			logger.error("RDF handler failed. " + ex.getMessage(), ex);
		}

		throw new InvalidQueryException(
				"Creating File with RDF data fault.");
	}

	/**
	 * Add all RDF triples in defined graph to reposiotory.
	 *
	 * @param graphInstance Concrete graph contains RDF triples.
	 */
    @Override
    public void addTriplesFromGraph(Graph graphInstance) {
        if (graphInstance != null) {
            try {
                RepositoryConnection connection = getConnection();
                connection.add(graphInstance, dataGraph);
            } catch (RepositoryException e) {
                logger.debug(e.getMessage());
            }
        }
    }

	/**
	 * Make construct query over graph URIs in dataSet and return interface
	 * Graph as result contains iterator for statements (triples).
	 *
	 * @param constructQuery String representation of SPARQL query.
	 * @param dataSet        Set of graph URIs used for construct query.
	 * @return Interface Graph as result of construct SPARQL query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	@Override
	public Graph executeConstructQuery(String constructQuery, Dataset dataSet)
			throws InvalidQueryException {

		try {
			RepositoryConnection connection = getConnection();

			GraphQuery graphQuery = connection.prepareGraphQuery(
					QueryLanguage.SPARQL,
					constructQuery);

			graphQuery.setDataset(dataSet);

			logger.debug("Query {} is valid.", constructQuery);

			try {

				MyGraphQueryResult result = new MyGraphQueryResult(graphQuery
						.evaluate());

				logger.debug(
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
			logger.error("Connection to RDF repository failed. {}",
					ex.getMessage(), ex);
		}

		throw new InvalidQueryException(
				"Getting GraphQueryResult using SPARQL construct query failed.");
	}

	/**
	 * Make construct query over repository data and return interface Graph as
	 * result contains iterator for statements (triples).
	 *
	 * @param constructQuery String representation of SPARQL query.
	 * @return Interface Graph as result of construct SPARQL query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	@Override
	public Graph executeConstructQuery(String constructQuery) throws InvalidQueryException {
        DatasetImpl dataSet = new DatasetImpl();
        dataSet.addDefaultGraph(this.getDataGraph());
        dataSet.addNamedGraph(this.getDataGraph());
		return executeConstructQuery(constructQuery, dataSet);
	}

	/**
	 * Make select query over repository data and return file as SPARQL XML
	 * result.
	 *
	 * @param selectQuery String representation of SPARQL query
	 * @param filePath    String path to file for saving result of query in
	 *                    SPARQL XML syntax.
	 * @param selectType  One of possible format for result of SPARQL select
	 *                    query.
	 * @return File contains result of given SPARQL select query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	@Override
	public File executeSelectQuery(String selectQuery,
			String filePath, SelectFormatType selectType)
			throws InvalidQueryException {

		try {
			RepositoryConnection connection = getConnection();

			TupleQuery tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, selectQuery);
            DatasetImpl dataSet = new DatasetImpl();
            dataSet.addDefaultGraph(this.getDataGraph());
            dataSet.addNamedGraph(this.getDataGraph());
			tupleQuery.setDataset(dataSet);

			logger.debug("Query {} is valid.", selectQuery);

			File file = new File(filePath);
			createNewFile(file);

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
			logger.error("Writing result to file fail. {}", ex.getMessage(),
					ex);

		} catch (RepositoryException ex) {
			logger.error("Connection to RDF repository failed. {}",
					ex.getMessage(), ex);
		} catch (IOException ex) {
			logger.error("Stream were not closed. {}", ex.getMessage(), ex);
		}

		throw new InvalidQueryException(
				"Creating File with RDF data fault.");

	}

	/**
	 * Make select query over repository data and return tables as result.
	 *
	 * @param selectQuery String representation of SPARQL select query.
	 * @return <code>Map&lt;String,List&lt;String&gt;&gt;</code> as table, where
	 *         map key is column name * * and <code>List&lt;String&gt;</code>
	 *         are string values in this column. When query is invalid, return *
	 *         * * * empty <code>Map</code>.
	 * @throws InvalidQueryException when query is not valid.
	 */
	@Override
	public Map<String, List<String>> executeSelectQuery(
			String selectQuery)
			throws InvalidQueryException {

		Map<String, List<String>> map = new LinkedHashMap<>();

		List<BindingSet> listBindings = new ArrayList<>();
		TupleQueryResult result = null;
		try {
			result = executeSelectQueryAsTuples(selectQuery);

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
					logger.warn("Failed to close RDF tuple result. {}",
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
	 *
	 * @param uriResource Subject or object URI as resource use to describe it.
	 * @return Graph contains all RDF triples as result of descibe for given
	 *         Resource URI. If graph is empty, there is are no triples for
	 *         describe Resource URI.
	 * @throws InvalidQueryException if resource is not URI type (e.g.
	 *                               BlankNode, some type of Literal (in object
	 *                               case))
	 */
	@Override
	public Graph describeURI(Resource uriResource) throws InvalidQueryException {

		if (uriResource instanceof URI) {
			String describeQuery = String.format("DESCRIBE <%s>", uriResource
					.toString());

			Graph result = executeConstructQuery(describeQuery);

			return result;
		} else {
			throw new InvalidQueryException(
					"Resource " + uriResource.toString() + "is not URI type");
		}

	}

	private long getSizeForConstruct(String constructQuery) throws InvalidQueryException {
		long size = 0;

		RepositoryConnection connection = null;

		try {
			connection = getConnection();

			GraphQuery graphQuery = connection.prepareGraphQuery(
					QueryLanguage.SPARQL,
					constructQuery);
            DatasetImpl dataSet = new DatasetImpl();
            dataSet.addDefaultGraph(this.getDataGraph());
            dataSet.addNamedGraph(this.getDataGraph());
			graphQuery.setDataset(dataSet);
			try {
				GraphQueryResult result = graphQuery.evaluate();

				Model model = QueryResults.asModel(result);
				size = model.size();
				result.close();

			} catch (QueryEvaluationException ex) {

				throw new InvalidQueryException(
						"This query is probably not valid. " + ex
						.getMessage(),
						ex);
			}

		} catch (MalformedQueryException ex) {
			throw new InvalidQueryException(
					"This query is probably not valid as construct query. "
					+ ex.getMessage(), ex);
		} catch (RepositoryException ex) {
			logger.error("Connection to RDF repository failed. {}",
					ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn(
							"Failed to close connection to RDF repository while querying. {}",
							ex.getMessage(), ex);
				}
			}
		}

		return size;
	}

	private long getSizeForSelect(QueryPart queryPart) throws InvalidQueryException {

		final String sizeVar = "selectSize";

		final String sizeQuery = String.format(
				"%s SELECT (count(*) AS ?%s) WHERE {%s}", queryPart
				.getQueryPrefixes(), sizeVar,
				queryPart.getQueryWithoutPrefixes());
		try {
			RepositoryConnection connection = getConnection();

			TupleQuery tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, sizeQuery);
            DatasetImpl dataSet = new DatasetImpl();
            dataSet.addDefaultGraph(this.getDataGraph());
            dataSet.addNamedGraph(this.getDataGraph());
			tupleQuery.setDataset(dataSet);
			try {
				TupleQueryResult tupleResult = tupleQuery.evaluate();
				if (tupleResult.hasNext()) {
					String selectSize = tupleResult.next()
							.getValue(sizeVar).stringValue();
					long resultSize = Long.parseLong(selectSize);

					tupleResult.close();
					return resultSize;
				}
				throw new InvalidQueryException(
						"Query: " + queryPart.getQuery() + " has no bindings for information about its size");
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
			logger.error("Connection to RDF repository failed. {}",
					ex.getMessage(), ex);
		}

		return 0;
	}

	/**
	 * For Browsing all data in graph return its size {count of rows}.
	 */
	@Override
	public long getResultSizeForDataCollection() throws InvalidQueryException {
		final String selectQuery = "SELECT ?x ?y ?z WHERE {?x ?y ?z}";
		return getSizeForSelect(new QueryPart(selectQuery));
	}

	/**
	 * For given valid SELECT of CONSTRUCT query return its size {count of rows
	 * returns for given query).
	 *
	 * @param query Valid SELECT/CONTRUCT query for asking.
	 * @return size for given valid query as long.
	 * @throws InvalidQueryException if query is not valid.
	 */
	@Override
	public long getResultSizeForQuery(String query) throws InvalidQueryException {

		long size = 0;

		QueryPart queryPart = new QueryPart(query);
		SPARQLQueryType type = queryPart.getSPARQLQueryType();

		switch (type) {
			case SELECT:
				size = getSizeForSelect(queryPart);
				break;
			case CONSTRUCT:
			case DESCRIBE:
				size = getSizeForConstruct(query);
				break;
			case UNKNOWN:
				throw new InvalidQueryException(
						"Given query: " + query + "have to be SELECT or CONSTRUCT type.");
		}

		return size;


	}

	/**
	 * Make ORDERED SELECT QUERY (select query contains ORDER BY keyword) over
	 * repository data and return {@link OrderTupleQueryResultImpl} class as
	 * result.
	 *
	 * This ordered select query must not containt LIMIT nad OFFSET
	 * keywords.
	 *
	 * For no problem behavior check you setting "MaxSortedRows" param in your
	 * virtuoso.ini file before using. For more info
	 *
	 * @see OrderTupleQueryResultImpl class description.
	 *
	 * @param orderSelectQuery String representation of SPARQL select query.
	 * @return {@link OrderTupleQueryResultImpl} representation of ordered
	 *         select query.
	 * @throws InvalidQueryException when query is not valid or containst LIMIT
	 *                               or OFFSET keyword.
	 */
	@Override
	public OrderTupleQueryResultImpl executeOrderSelectQueryAsTuples(
			String orderSelectQuery) throws InvalidQueryException {

//		QueryValidator validator = new SPARQLQueryValidator(orderSelectQuery,
//				SPARQLQueryType.SELECT);
//
//		boolean hasLimit = orderSelectQuery.toLowerCase().contains("limit");
//		boolean hasOffset = orderSelectQuery.toLowerCase().contains("offset");
//
//		if (validator.isQueryValid()) {
//
//			if (hasLimit) {
//				throw new InvalidQueryException(
//						"Query: " + orderSelectQuery + " contains LIMIT keyword which is forbidden.");
//			}
//
//			if (hasOffset) {
//				throw new InvalidQueryException(
//						"Query: " + orderSelectQuery + " contains OFFSET keyword which is forbidden.");
//			}

			OrderTupleQueryResultImpl result = new OrderTupleQueryResultImpl(
					orderSelectQuery, this);
			return result;
//		} else {
//			throw new InvalidQueryException(
//					"Query: " + orderSelectQuery + " is not valid SELECT query");
//		}
	}

	/**
	 * Make select query over repository data and return MyTupleQueryResult
	 * class as result.
	 *
	 * @param selectQuery String representation of SPARQL select query.
	 * @return MyTupleQueryResult representation of SPARQL select query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	@Override
	public TupleQueryResult executeSelectQueryAsTuples(
			String selectQuery)
			throws InvalidQueryException {

		try {
			RepositoryConnection connection = getConnection();

			TupleQuery tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, selectQuery);
            DatasetImpl dataSet = new DatasetImpl();
            dataSet.addDefaultGraph(this.getDataGraph());
            dataSet.addNamedGraph(this.getDataGraph());
			tupleQuery.setDataset(dataSet);

			logger.debug("Query {} is valid.", selectQuery);

			try {
				TupleQueryResult tupleResult = tupleQuery.evaluate();
				logger.debug(
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
			logger.error("Connection to RDF repository failed. {}",
					ex.getMessage(), ex);
		}
		throw new InvalidQueryException(
				"Getting TupleQueryResult using SPARQL select query failed.");
	}

	/**
	 *
	 * @param updateQuery String value of SPARQL update query.
	 * @return String extension of given update query works with set repository
	 *         GRAPH.
	 */
	public String AddGraphToUpdateQuery(String updateQuery) {

		String regex = "(insert|delete)\\s\\{";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(updateQuery.toLowerCase());

		boolean hasResult = matcher.find();
		boolean hasWith = updateQuery.toLowerCase().contains("with");

		if (hasResult && !hasWith) {

			int index = matcher.start();

			String first = updateQuery.substring(0, index);
			String second = updateQuery.substring(index, updateQuery.length());

			String graphName = " WITH <" + dataGraph.stringValue() + "> ";

			String newQuery = first + graphName + second;
			return newQuery;


		} else {

			logger.debug("WITH graph clause was not added, "
					+ "because the query was: {}", updateQuery);

			regex = "(insert|delete)\\sdata\\s\\{";
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(updateQuery.toLowerCase());

			hasResult = matcher.find();

			if (hasResult) {

				int start = matcher.start();
				int end = matcher.end();

				String first = updateQuery.substring(0, start);
				String second = updateQuery.substring(end, updateQuery.length());

				String myString = updateQuery.substring(start, end);
				String graphName = myString.replace("{",
						"{ GRAPH <" + dataGraph.stringValue() + "> {");

				second = second.replaceFirst("}", "} }");
				String newQuery = first + graphName + second;

				return newQuery;

			}
		}
		return updateQuery;


	}

	/**
	 * Created file from given parameter. If file is null, nothing is created.
	 *
	 * @param file file instance, you can create on file path.
	 */
	protected void createNewFile(File file) {

		if (file == null) {
			return;
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			logger.debug(e.getMessage());
		}

	}

	/**
	 * Return URI representation of graph where RDF data are stored.
	 *
	 * @return graph with stored data as URI.
	 */
	@Override
	public URI getDataGraph() {
		return dataGraph;
	}

	/**
	 * Set data graph storage for given data in RDF format.
	 *
	 * @param newDataGraph new graph represented as URI.
	 */
	@Override
	public void setDataGraph(URI newDataGraph) {
		dataGraph = newDataGraph;
		logger.info("Set new data graph - " + dataGraph.stringValue());
	}
	
	/**
	 * Set new graph as default for working data in RDF format.
	 *
	 * @param newStringDataGraph String name of graph as URI - starts with
	 *                           prefix http://).
	 */
	@Override
	public void setDataGraph(String newStringDataGraph) {
		if (!newStringDataGraph.toLowerCase().startsWith("http://")) {
			newStringDataGraph = "http://" + newStringDataGraph;
		}
		URI newGraph = new URIImpl(newStringDataGraph);

		setDataGraph(newGraph);
	}	
}
