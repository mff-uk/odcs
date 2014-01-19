package cz.cuni.mff.xrg.odcs.loader.rdf;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.httpconnection.utils.Authentificator;
import static cz.cuni.mff.xrg.odcs.rdf.enums.InsertType.*;

import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;

import static cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType.*;

import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.GraphNotEmptyException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InsertPartException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.help.ParamController;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.BaseRDFRepo;
import cz.cuni.mff.xrg.odcs.rdf.repositories.VirtuosoRDFRepo;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.openrdf.model.*;
import org.openrdf.model.URI;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import org.openrdf.rio.RDFFormat;

/**
 *
 * Responsible to load RDF data to SPARQL endpoint. Need special for SPARQL
 * loader DPU - separed implementation from {@link BaseRDFRepo}.
 *
 * @author Jiri Tomes
 */
public class SPARQLoader {

	private static Logger logger = Logger.getLogger(SPARQLoader.class);

	/**
	 * Count of attempts to reconnect if the connection fails. For infinite loop
	 * use zero or negative integer.
	 */
	private static final int DEFAULT_LOADER_RETRY_SIZE = -1;

	/**
	 * Time in miliseconds how long to wait before trying to reconnect.
	 */
	private static final long DEFAUTL_LOADER_RETRY_TIME = 1000;

	/**
	 * Represents successful connection using HTTP.
	 */
	private static final int HTTP_OK_RESPONSE = 200;

	/**
	 * Represent http error code needed authorisation for connection using HTTP.
	 */
	private static final int HTTP_UNAUTORIZED_RESPONSE = 401;

	/**
	 * Represent http error code returns when inserting data in bad format.
	 */
	private static final int HTTP_BAD_RESPONSE = 400;

	/**
	 * Default used encoding.
	 */
	private static final String encode = "UTF-8";

	private RDFDataUnit rdfDataUnit;

	/**
	 * Count of reconnection if connection failed.
	 */
	private int retrySize;

	/**
	 * Time in ms how long wait before re-connection attempt.
	 */
	private long retryTime;

	private DPUContext context;

	/**
	 * Request HTTP parameters neeed for setting target SPARQL endpoint.
	 */
	private LoaderEndpointParams endpointParams;

	/**
	 * Constructor for using in DPUs calling.
	 *
	 * @param rdfDataUnit    Instance of RDFDataUnit repository neeed for
	 *                       loading
	 * @param context        Given DPU context for DPU over it are executed.
	 * @param retrySize      Integer value as count of attempts to reconnect if
	 *                       the connection fails. For infinite loop use zero or
	 *                       negative integer
	 * @param retryTime      Long value as time in miliseconds how long to wait
	 *                       before trying to reconnect.
	 * @param endpointParams Request HTTP parameters neeed for setting target
	 *                       SPARQL endpoint.
	 */
	public SPARQLoader(RDFDataUnit rdfDataUnit, DPUContext context,
			int retrySize, long retryTime, LoaderEndpointParams endpointParams) {
		this.rdfDataUnit = rdfDataUnit;
		this.context = context;
		this.retrySize = retrySize;
		this.retryTime = retryTime;
		this.endpointParams = endpointParams;
	}

	/**
	 * Constructor for using in DPUs calling with default retrySize and
	 * retryTime values.
	 *
	 * @param rdfDataUnit    Instance of RDFDataUnit repository neeed for
	 *                       loading
	 * @param context        Given DPU context for DPU over it are executed.
	 * @param endpointParams Request HTTP parameters neeed for setting target
	 *                       SPARQL endpoint.
	 */
	public SPARQLoader(RDFDataUnit rdfDataUnit, DPUContext context,
			LoaderEndpointParams endpointParams) {
		this.rdfDataUnit = rdfDataUnit;
		this.context = context;
		this.retrySize = DEFAULT_LOADER_RETRY_SIZE;
		this.retryTime = DEFAUTL_LOADER_RETRY_TIME;
		this.endpointParams = endpointParams;
	}

	/**
	 * Load RDF data from repository to SPARQL endpointURL to the one URI graph
	 * with endpoint authentication (name,password).
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.
	 * @param name            String name needed for authentication.
	 * @param password        String password needed for authentication.
	 * @param graphType       One of way, how to solve loading RDF data to graph
	 *                        when is it is not empty (MERGE, OVERRIDE, FAIL).
	 * @param insertType      One of way, how solve loading RDF data parts to
	 *                        SPARQL endpoint (SKIP_BAD_TYPES,
	 *                        STOP_WHEN_BAD_PART).
	 * @throws RDFException when loading data fault.
	 */
	public void loadToSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
			String name, String password, WriteGraphType graphType,
			InsertType insertType) throws RDFException {
		List<String> endpointGraphsURI = new ArrayList<>();
		endpointGraphsURI.add(defaultGraphURI);

		loadToSPARQLEndpoint(endpointURL, endpointGraphsURI, name, password,
				graphType, insertType, BaseRDFRepo.getDefaultChunkSize());
	}

	/**
	 * Load RDF data from repository to SPARQL endpointURL to the collection of
	 * URI graphs without endpoint authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI List with names of graph where RDF data are
	 *                        loading.
	 * @param graphType       One of way, how to solve loading RDF data to graph
	 *                        when is it is not empty (MERGE, OVERRIDE, FAIL).
	 * @param insertType      One of way, how solve loading RDF data parts to
	 *                        SPARQL endpoint (SKIP_BAD_TYPES,
	 *                        STOP_WHEN_BAD_PART).
	 * @throws RDFException when loading data to SPARQL endpoint fail.
	 */
	public void loadToSPARQLEndpoint(URL endpointURL,
			List<String> endpointGraphsURI, WriteGraphType graphType,
			InsertType insertType) throws RDFException {

		loadToSPARQLEndpoint(endpointURL, endpointGraphsURI, "", "",
				graphType, insertType, BaseRDFRepo.getDefaultChunkSize());
	}

	/**
	 * Load RDF data from repository to SPARQL endpointURL to the one URI graph
	 * without endpoint authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.
	 * @param graphType       One of way, how to solve loading RDF data to graph
	 *                        when is it is not empty (MERGE, OVERRIDE, FAIL).
	 * @param insertType      One of way, how solve loading RDF data parts to
	 *                        SPARQL endpoint (SKIP_BAD_TYPES,
	 *                        STOP_WHEN_BAD_PART).
	 * @throws RDFException when loading data fail.
	 */
	public void loadToSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType, InsertType insertType) throws RDFException {
		List<String> endpointGraphsURI = new ArrayList<>();
		endpointGraphsURI.add(defaultGraphURI);

		loadToSPARQLEndpoint(endpointURL, endpointGraphsURI, "", "",
				graphType, insertType, BaseRDFRepo.getDefaultChunkSize());
	}

	/**
	 * Load RDF data from repository to SPARQL endpointURL to the collection of
	 * URI graphs with endpoint authentication (name,password).
	 *
	 * @param endpointURL Remote URL connection to SPARQL endpoint contains RDF
	 *                    data.
	 * @param graphs      List with names of graph where RDF data are loading.
	 * @param userName    String name needed for authentication.
	 * @param password    String password needed for authentication.
	 * @param graphType   One of way, how to solve loading RDF data to graph
	 *                    when is it is not empty (MERGE, OVERRIDE, FAIL).
	 * @param insertType  One of way, how solve loading RDF data parts to SPARQL
	 *                    endpoint (SKIP_BAD_TYPES, STOP_WHEN_BAD_PART).
	 * @param chunkSize   Size of insert part of triples which insert at once to
	 *                    SPARQL endpoint.
	 * @throws RDFException when loading data fault.
	 */
	public void loadToSPARQLEndpoint(URL endpointURL,
			List<String> graphs, String userName,
			String password, WriteGraphType graphType, InsertType insertType,
			long chunkSize)
			throws RDFException {

		//check that SPARQL endpoint URL is correct
		ParamController.testEndpointSyntax(endpointURL);

		ParamController.testNullParameter(graphs,
				"Default graph must be specifed");
		ParamController.testEmptyParameter(graphs,
				"Default graph must be specifed");

		ParamController.testPositiveParameter(chunkSize,
				"Chunk size must be number greater than 0");

		Authentificator.authenticate(userName, password);

		RepositoryConnection connection = null;

		try {

			connection = rdfDataUnit.getConnection();


			for (int i = 0; i < graphs.size(); i++) {
				final String endpointGraph = graphs.get(i);

				//clean target graph if nessasarry - via using given WriteGraphType 
				prepareGraphTargetForLoading(endpointURL, endpointGraph,
						graphType);

				if (context.canceled()) {
					logger.error(
							"Loading data to SPARQL endpoint " + endpointURL + " was canceled by user");
					break;
				}

			}

			//starting to load data to target SPARQL endpoint
			loadGraphDataToEndpoint(endpointURL, graphs, chunkSize,
					insertType);


		} catch (RepositoryException ex) {
			throw new RDFException("Repository connection failed. " + ex
					.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn("Failed to close connection to RDF repository. "
							+ ex.getMessage(), ex);
				}
			}

		}
	}

	/**
	 * Create pair graph-temp_graph for each graph as target of loading and add
	 * it to collection which returns.
	 *
	 * @param graphs Collection of names of graphs where are data loader
	 * @return instance of {@link GraphPairCollection} with pairs
	 *         graph,tempGraph.
	 */
	private GraphPairCollection getGraphPairs(List<String> graphs) {

		GraphPairCollection collection = new GraphPairCollection();

		for (String nextGraph : graphs) {
			String tempGraph = nextGraph + "/temp";
			collection.add(nextGraph, tempGraph);
		}

		return collection;
	}

	private void loadGraphDataToEndpoint(URL endpointURL,
			List<String> targetGraphs,
			long chunkSize, InsertType insertType) throws RDFException {

		final GraphPairCollection collection = getGraphPairs(targetGraphs);

		switch (insertType) {
			case STOP_WHEN_BAD_PART:
				try {
					loadDataParts(endpointURL, collection.getTempGraphs(),
							insertType,
							chunkSize);
					if (!context.canceled()) {
						moveDataToTarget(endpointURL, collection);
					}


				} catch (InsertPartException e) {
					throw new RDFException(e.getMessage(), e);
				} finally {
					clearEndpointGraphs(endpointURL, collection.getTempGraphs());
				}
				break;
			case SKIP_BAD_PARTS:
				loadDataParts(endpointURL, targetGraphs, insertType,
						chunkSize);
				break;
			case REPEAT_IF_BAD_PART:
				while (true) {
					try {
						loadDataParts(endpointURL, collection.getTempGraphs(),
								insertType,
								chunkSize);
						if (!context.canceled()) {
							moveDataToTarget(endpointURL, collection);
						}
						break; //loaded sucessfull - leave infinite loop

					} catch (InsertPartException e) {
						//log message with destription of insert part problem.
						logger.debug(e.getMessage());
					} finally {
						clearEndpointGraphs(endpointURL, collection
								.getTempGraphs());
					}
				}
				break;
		}
	}

	private void prepareGraphTargetForLoading(URL endpointURL,
			String endpointGraph,
			WriteGraphType graphType) throws RDFException {

		try {
			switch (graphType) {
				case MERGE:
					break;
				case OVERRIDE: {
					// clear graph
					clearEndpointGraph(endpointURL, endpointGraph);
				}
				break;
				case FAIL: {
					//if target graph is not empty, exception is thrown

					long SPARQLGraphSize = getSPARQLEnpointGraphSize(
							endpointURL, endpointGraph);


					if (SPARQLGraphSize > 0) {
						throw new GraphNotEmptyException(
								"Graph <" + endpointGraph + "> is not empty (has "
								+ SPARQLGraphSize
								+ " triples) - Loading to SPARQL endpoint FAILs.");
					}

				}

				break;

			}
		} catch (GraphNotEmptyException ex) {
			logger.error(ex.getMessage());

			throw new RDFException(ex.getMessage(), ex);
		}
	}

	private String getConstructQuery(long limit, long offset) {
		if (offset < 0 | limit < 0) {
			return "CONSTRUCT {?a ?b ?c} WHERE {?a ?b ?c}";
		} else {
			return String.format(
					"CONSTRUCT {?a ?b ?c} WHERE {?a ?b ?c} LIMIT %s OFFSET %s",
					limit, offset);
		}
	}

	private void loadDataParts(URL endpointURL, List<String> endpointGraphs,
			InsertType insertType, long chunkSize)
			throws RDFException {

		long counter = 0;

		String part = getInsertQueryPart(chunkSize, counter);

		long partsCount = rdfDataUnit.getPartsCount(chunkSize);

		while (part != null) {
			counter++;

			if (context.canceled()) {
				//stop loading Parts
				logger.error("Loading data was canceled by user !!!");
				break;
			}
			final String query = part;
			part = getInsertQueryPart(chunkSize, counter);

			final String processing = String.valueOf(counter) + "/" + String
					.valueOf(partsCount);

			try {
				try (InputStreamReader inputStreamReader = getEndpointStreamReader(
						endpointURL, endpointGraphs, query)) {
				}

				logger.debug(
						"Data " + processing + " part loaded successful");

			} catch (InsertPartException e) {
				String message;

				switch (insertType) {
					case SKIP_BAD_PARTS: //go to next part
						message = "Data " + processing + " part was skiped. "
								+ e.getMessage();
						logger.warn(message);
						break;
					case STOP_WHEN_BAD_PART:
					case REPEAT_IF_BAD_PART:

						message = "Inserting failed to " + processing + " data part. "
								+ e.getMessage();
						logger.error(message);

						throw new InsertPartException(message, e);

				}

			} catch (IOException e) {
				throw new RDFException(e.getMessage(), e);
			}
		}


	}

	/**
	 * For each pair from collection move data from temp_graph to graph.
	 *
	 * @param endpoint   target endpoint where data are loaded to.
	 * @param collection collection contains pairs graph-temp_graph
	 * @throws RDFException if some data moving failed
	 */
	private void moveDataToTarget(URL endpoint, GraphPairCollection collection)
			throws RDFException {

		for (GraphPair nextPair : collection.getGraphPairs()) {
			moveDataToTarget(endpoint, nextPair.getTempGraphName(), nextPair
					.getGraphName());
		}
	}

	/**
	 * Move data after successfully loading all data part from temp graph to
	 * graph defined before start loading.
	 *
	 * @param endpointURL target endpoint where data are loaded to.
	 * @param tempGraph   String value of graph from data are moved
	 * @param targetGraph String value of graph to data are moved.
	 * @throws RDFException if data moving failded.
	 */
	private void moveDataToTarget(URL endpointURL, String tempGraph,
			String targetGraph) throws RDFException {

		boolean useExtension = false;

		if (rdfDataUnit instanceof VirtuosoRDFRepo) {
			VirtuosoRDFRepo repo = (VirtuosoRDFRepo) rdfDataUnit;
			useExtension = repo.isUsedExtension();
		}

		String moveQuery;

		if (useExtension) {
			moveQuery = String.format("DEFINE sql:log-enable 2 \n"
					+ "ADD <%s> TO <%s>", tempGraph, targetGraph);
		} else {
			moveQuery = String
					.format("ADD <%s> TO <%s>", tempGraph, targetGraph);
		}

		String start = String.format(
				"Query for moving data from temp GRAPH <%s> to target GRAPH <%s> prepared.",
				tempGraph, targetGraph);

		logger.debug(start);

		int retryCount = 0;

		while (true) {
			try {
				try (InputStreamReader result = getEndpointStreamReader(
						endpointURL, moveQuery)) {
				}

				//Move data to target graph successfuly - stop the infinity loop
				break;


			} catch (IOException e) {
				throw new RDFException(e.getMessage(), e);

			} catch (RDFException e) {
				if (context.canceled()) {
					//stop moving data
					logger.error("Moving data was canceled by user !!!");
					break;
				}
				rdfDataUnit.restartConnection();
				retryCount++;

				if (retryCount > retrySize && !hasInfinityRetryConnection()) {
					final String errorMessage = String.format(
							"Count of retryConnection for MOVE data FROM TEMP GRAPH <%s> TO GRAPH <%s> is OVER (TOTAL %s ATTEMPTS). ",
							tempGraph, targetGraph, retryCount);
					logger.debug(errorMessage);

					throw new RDFException(errorMessage + e.getMessage(), e);
				} else {

					final String errorMessage = String.format(
							"Attempt %s for MOVE data FROM TEMP GRAPH <%s> TO GRAPH <%s> failed. Reason of cause :%s",
							tempGraph, targetGraph, retryCount, e.getMessage());

					logger.debug(errorMessage);


					try {
						//sleep and attempt to reconnect
						Thread.sleep(retryTime);

					} catch (InterruptedException ex) {
						logger.debug(ex.getMessage());
					}
				}
			}
		}

		String finish = String.format(
				"All data from temp GRAPH <%s> to GRAPH <%s> were moved sucessfully",
				tempGraph, targetGraph);

		logger.debug(finish);

	}

	private InputStreamReader getEndpointStreamReader(URL endpointURL,
			String query) throws RDFException {

		return getEndpointStreamReader(endpointURL, new LinkedList<String>(),
				query);
	}

	private InputStreamReader getEndpointStreamReader(URL endpointURL,
			List<String> endpointGraph, String query) throws RDFException {

		LoaderPostType postType = endpointParams.getPostType();

		switch (postType) {
			case POST_UNENCODED_QUERY:
				return getUnencodedQueryStreamReader(endpointURL, endpointGraph,
						query);
			case POST_URL_ENCODER:
			default:
				return getEncodedStreamReader(endpointURL, endpointGraph, query,
						RDFFormat.RDFXML);

		}

	}

	/**
	 * Returns graph size for given graph and SPARQL endpoint.
	 *
	 * @param endpointURL   URL of SPARQL endpoint where we can find graph size.
	 * @param endpointGraph String name of graph which size we can find out.
	 * @return graph size for given graph and SPARQL endpoint.
	 * @throws RDFException if endpoint is not available or cause problems.
	 */
	public long getSPARQLEnpointGraphSize(URL endpointURL,
			String endpointGraph)
			throws RDFException {

		String countQuery = "SELECT (count(*) as ?count) WHERE {?x ?y ?z}";

		List<String> targetGraphs = new LinkedList<>();
		targetGraphs.add(endpointGraph);

		long count = -1;

		int retryCount = 0;

		while (true) {
			try {
				try (InputStreamReader inputStreamReader = getEndpointStreamReader(
						endpointURL, targetGraphs,
						countQuery)) {

					Scanner scanner = new Scanner(inputStreamReader);

					String regexp = ">[0-9]+<";
					Pattern pattern = Pattern.compile(regexp);
					boolean find = false;

					while (scanner.hasNext() & !find) {
						String line = scanner.next();
						Matcher matcher = pattern.matcher(line);

						if (matcher.find()) {
							String number = line.substring(matcher.start() + 1,
									matcher
									.end() - 1);
							count = Long.parseLong(number);
							find = true;

						}

					}
				}

				//Finding graph size successfuly - stop the infinity loop
				break;

			} catch (IOException e) {
				throw new RDFException(e.getMessage(), e);

			} catch (RDFException e) {
				if (context.canceled()) {
					//stop finding graph size.
					logger.error(
							"Finding enpoint graph size was canceled by user !!!");
					break;
				}
				rdfDataUnit.restartConnection();
				retryCount++;

				if (retryCount > retrySize && !hasInfinityRetryConnection()) {
					final String errorMessage = String.format(
							"Count of retryConnection for FINDING SIZE for ENDPOINT GRAPH <%s> is OVER (TOTAL %s ATTEMPTS). ",
							endpointGraph, retryCount);
					logger.debug(errorMessage);

					throw new RDFException(errorMessage + e.getMessage(), e);
				} else {

					final String errorMessage = String.format(
							"Attempt %s FINDING SIZE for ENDPOINT GRAPH <%s> failed. Reason of cause :%s",
							retryCount, endpointGraph, e.getMessage());

					logger.debug(errorMessage);


					try {
						//sleep and attempt to reconnect
						Thread.sleep(retryTime);

					} catch (InterruptedException ex) {
						logger.debug(ex.getMessage());
					}
				}
			}
		}


		return count;

	}

	private boolean hasInfinityRetryConnection() {
		if (retrySize < 0) {
			return true;
		} else {
			return false;
		}
	}

	private GraphQueryResult getTriplesPart(String constructQuery) throws InvalidQueryException {
		try {
			RepositoryConnection connection = rdfDataUnit.getConnection();

			GraphQuery graphQuery = connection.prepareGraphQuery(
					QueryLanguage.SPARQL,
					constructQuery);

			graphQuery.setDataset(rdfDataUnit.getDataSet());

			GraphQueryResult result = graphQuery.evaluate();
			return result;

		} catch (QueryEvaluationException | MalformedQueryException ex) {
			throw new InvalidQueryException(
					"Given query for lazy triples is probably not valid. "
					+ ex.getMessage(), ex);
		} catch (RepositoryException ex) {

			logger.error("Connection to RDF repository failed. "
					+ ex.getMessage(), ex);
		}
		throw new InvalidQueryException(
				"Getting GraphQueryResult for lazy triples failed.");
	}

	private String getInsertQueryPart(long sizeSplit,
			long loadedPartsCount) throws RDFException {

		final String insertStart = "INSERT {";
		final String insertStop = "} ";

		StringBuilder builder = new StringBuilder();

		builder.append(insertStart);

		long count = 0;
		final long offset = sizeSplit * loadedPartsCount;

		int retryCount = 0;

		while (true) {
			try {

				GraphQueryResult lazy = getTriplesPart(getConstructQuery(
						sizeSplit,
						offset));

				while (lazy.hasNext()) {

					Statement next = lazy.next();

					Resource subject = next.getSubject();
					URI predicate = next.getPredicate();
					Value object = next.getObject();

					StringBuilder appendLine = new StringBuilder();

					appendLine.append(getSubjectInsertText(subject));
					appendLine.append(" ");
					appendLine.append(getPredicateInsertText(predicate));
					appendLine.append(" ");
					appendLine.append(getObjectInsertText(object));
					appendLine.append(" .");

					builder.append(appendLine);

					count++;
					if (count == sizeSplit) {
						builder.append(insertStop);
						lazy.close();
						return builder.toString();

					}
				}

				if (count > 0) {
					builder.append(insertStop);
					lazy.close();
					return builder.toString();
				}

				return null;

			} catch (InvalidQueryException | QueryEvaluationException e) {

				if (context.canceled()) {
					//stop loading Parts
					logger.error("Loading data was canceled by user !!!");
					break;
				}

				rdfDataUnit.restartConnection();
				builder.delete(0, builder.length());
				retryCount++;
				String error = String.format("Problem by creating %s"
						+ ". data part - ATTEMPT number %s: ", loadedPartsCount,
						retryCount);

				if (retryCount > retrySize && retrySize >= 0) {
					throw new RDFException(error + e.getMessage(), e);

				} else {
					logger.debug(error + e.getMessage());
					try {
						//sleep and attempt to reconnect
						Thread.sleep(retryTime);

					} catch (InterruptedException ex) {
						logger.debug(ex.getMessage(), ex);
					}
				}

			}

		}
		return null;
	}

	private String getSubjectInsertText(Resource subject) throws IllegalArgumentException {

		if (subject instanceof URI) {
			return prepareURIresource((URI) subject);
		}

		if (subject instanceof BNode) {
			return prepareBlankNodeResource((BNode) subject);
		}
		throw new IllegalArgumentException("Subject must be URI or blank node");
	}

	private String getPredicateInsertText(URI predicate) {
		if (predicate instanceof URI) {
			return prepareURIresource((URI) predicate);
		}
		throw new IllegalArgumentException("Predicatemust be URI");

	}

	private String getObjectInsertText(Value object) throws IllegalArgumentException {

		if (object instanceof URI) {
			return prepareURIresource((URI) object);
		}

		if (object instanceof BNode) {
			return prepareBlankNodeResource((BNode) object);
		}

		if (object instanceof Literal) {
			return prepareLiteral((Literal) object);
		}

		throw new IllegalArgumentException(
				"Object must be URI, blank node or literal");
	}

	private String prepareURIresource(URI uri) {
		return "<" + uri.stringValue() + ">";
	}

	private String prepareBlankNodeResource(BNode bnode) {
		return "_:" + bnode.getID();
	}

	private String getEscapedLabel(String label) {

		String result = label.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\'", "\\\'");

		return result;
	}

	private String prepareLiteral(Literal literal) {

		String label = getEscapedLabel(literal.getLabel());

		String result = "\"\"\"" + label + "\"\"\"";
		if (literal.getLanguage() != null) {
			//there is language tag
			return result + "@" + literal.getLanguage();
		} else if (literal.getDatatype() != null) {
			return result + "^^" + prepareURIresource(literal.getDatatype());
		}
		//plain literal (return in """)
		return result;

	}

	private void clearEndpointGraphs(URL endpoint, List<String> endpointGraphs)
			throws RDFException {

		for (String nextGraph : endpointGraphs) {
			clearEndpointGraph(endpoint, nextGraph);
		}
	}

	/**
	 * Removes all RDF data in defined graph using connecion to SPARQL endpoint
	 * address. For data deleting is necessarry to have endpoint with update
	 * rights.
	 *
	 * @param endpointURL   URL address of update endpoint connect to.
	 * @param endpointGraph Graph name in URI format.
	 * @param context       DPU context for checking manual canceling in case of
	 *                      infinite loop (no recovery error).
	 * @throws RDFException When you dont have update right for this action, or
	 *                      connection is lost before succesfully ending.
	 */
	public void clearEndpointGraph(URL endpointURL, String endpointGraph)
			throws RDFException {

		String deleteQuery = String.format("CLEAR GRAPH <%s>", endpointGraph);

		int retryCount = 0;

		while (true) {
			try {
				try (InputStreamReader inputStreamReader = getEndpointStreamReader(
						endpointURL, deleteQuery)) {
				}

				//Clear graph successfuly - stop the infinity loop
				break;

			} catch (IOException e) {
				final String message = String.format(
						"InputStreamReader was not closed. %s", e.getMessage());
				logger.error(message, e);

			} catch (RDFException e) {
				if (context.canceled()) {
					//stop clear graph
					final String message = String.format(
							"CLEAR GRAPH<%s> was canceled by user !!!",
							endpointGraph);
					logger.error(message, e);
					break;
				}
				rdfDataUnit.restartConnection();
				retryCount++;

				if (retryCount > retrySize && !hasInfinityRetryConnection()) {
					final String errorMessage = String.format(
							"Count of retryConnection for CLEAR GRAPH <%s> is OVER (TOTAL %s ATTEMPTS). ",
							endpointGraph, retryCount);
					logger.debug(errorMessage);

					throw new RDFException(errorMessage + e.getMessage(), e);
				} else {
					final String message = String.format(
							"Attempt %s to CLEAR GRAPH<%s> failed. Reason:%s",
							retryCount,
							endpointGraph, e.getMessage());

					logger.error(message);

					try {
						//sleep and attempt to reconnect
						Thread.sleep(retryTime);

					} catch (InterruptedException ex) {
						logger.debug(ex.getMessage());
					}
				}
			}
		}


	}

	private void setPOSTConnection(HttpURLConnection httpConnection,
			String parameters,
			String contentType) throws IOException {

		httpConnection.setRequestMethod("POST");
		httpConnection.setRequestProperty("Content-Type", contentType);
		httpConnection.setRequestProperty("Accept", "*/*");
		httpConnection.setRequestProperty("Content-Length", ""
				+ Integer.toString(parameters.length()));

		httpConnection.setUseCaches(false);
		httpConnection.setDoInput(true);
		httpConnection.setDoOutput(true);

	}

	private String getHTTPResponseErrorMessage(HttpURLConnection httpConnection)
			throws IOException {

		StringBuilder message = new StringBuilder(
				httpConnection.getHeaderField(0));

		int httpResponseCode = httpConnection.getResponseCode();

		if (httpResponseCode == HTTP_UNAUTORIZED_RESPONSE) {
			message.append(
					". Your USERNAME and PASSWORD for connection is wrong.");
		} else {

			if (httpResponseCode == HTTP_BAD_RESPONSE) {
				message.append(
						". Inserted data has wrong format");
			}

			try (InputStream errorStream = httpConnection
					.getErrorStream()) {

				try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(
						errorStream, Charset.forName(encode)))) {

					StringBuilder inputStringBuilder = new StringBuilder();
					String line = reader.readLine();
					while (line != null) {
						inputStringBuilder.append(line);
						inputStringBuilder.append('\n');
						line = reader.readLine();
					}

					String cause = ". Caused by " + inputStringBuilder
							.toString();

					message.append(cause);

				}
			}

		}

		return message.toString();
	}

	private String getGraphParam(String graphParam, List<String> graphs,
			boolean isFirst)
			throws RDFException {

		if (graphs.isEmpty()) {
			return "";
		} else {
			StringBuilder result = new StringBuilder();

			for (String nextGraphURI : graphs) {
				if (isFirst) {
					result.append("?");
					isFirst = false;
				} else {
					result.append("&");
				}
				result.append(graphParam);
				result.append("=");
				result.append(getEncodedString(nextGraphURI));

			}

			return result.toString();
		}

	}

	/**
	 *
	 * @param endpointURL      URL of endpoint we can to connect to.
	 * @param endpointGraphURI Name of graph as URI string we want to
	 *                         extract/load RDF data.
	 * @param query            SPARQL query to execute on sparql endpoint
	 * @param format           RDF data format for given returned RDF data.
	 *
	 * @return Result of given SPARQL query apply to given graph. If it produce
	 *         some RDF data, there are in specified RDF format.
	 * @throws RDFException if unknown host, connection problems, no permission
	 *                      for this action.
	 */
	private InputStreamReader getEncodedStreamReader(URL endpointURL,
			List<String> endpointGraphURI, String query,
			RDFFormat format) throws RDFException {

		String queryParam = String.format("%s=%s", endpointParams
				.getQueryParam(),
				getEncodedString(query));

		String defaultGraphParam = getGraphParam(endpointParams
				.getDefaultGraphParam(), endpointGraphURI, false);

		String formatParam = String.format("&format=%s", getEncoder(format));

		final String parameters = queryParam + defaultGraphParam + formatParam;

		URL call = null;
		try {
			call = new URL(endpointURL.toString());
		} catch (MalformedURLException e) {
			final String message = "Malfolmed URL exception by construct extract URL. ";
			logger.debug(message);
			throw new RDFException(message + e.getMessage(), e);
		}

		HttpURLConnection httpConnection = null;

		int retryCount = 0;

		while (true) {
			try {
				httpConnection = (HttpURLConnection) call.openConnection();

				setPOSTConnection(httpConnection, parameters,
						"application/x-www-form-urlencoded");

				try (OutputStream os = httpConnection.getOutputStream()) {
					os.write(parameters.getBytes());
					os.flush();
				}

				if (httpConnection.getResponseCode() != HTTP_OK_RESPONSE) {

					String errorMessage = getHTTPResponseErrorMessage(
							httpConnection);

					throw new InsertPartException(
							errorMessage + "\n\n" + "URL endpoint: " + endpointURL
							.toString() + " POST content: " + parameters);
				} else {

					InputStreamReader inputStreamReader = new InputStreamReader(
							httpConnection.getInputStream(), Charset.forName(
							encode));

					return inputStreamReader;
				}

			} catch (UnknownHostException e) {
				final String message = "Unknown host: ";
				throw new RDFException(message + e.getMessage(), e);

			} catch (IOException e) {
				retryCount++;

				if (!retryConnectionAgain(retryCount, endpointURL.toString())) {

					final String errorMessage = "Count of retryConnection is OVER (TOTAL " + retryCount + " ATTEMPTS). "
							+ "Endpoint HTTP connection stream cannot be opened. ";

					logger.debug(errorMessage);

					if (httpConnection != null) {
						httpConnection.disconnect();
					}

					throw new RDFException(errorMessage + e.getMessage(), e);
				}


			}
		}
	}

	private boolean retryConnectionAgain(int retryCount, String targetEndpoint) {

		final String message = String
				.format("%s/%s attempt to reconnect %s FAILED", retryCount,
				getRetryConnectionSizeAsString(), targetEndpoint);

		if (retryCount > retrySize && !hasInfinityRetryConnection()) {
			return false;

		} else {
			logger.debug(message);
			try {
				//sleep and attempt to reconnect
				Thread.sleep(retryTime);

			} catch (InterruptedException ex) {
				logger.debug(ex.getMessage());
			}
			return true;
		}
	}

	private String getRetryConnectionSizeAsString() {
		if (hasInfinityRetryConnection()) {
			return "infinity";
		} else {
			if (retrySize == 0) {
				return "only 1";
			} else {
				return String.valueOf(retrySize);
			}
		}
	}

	private String getEncoder(RDFFormat format) throws RDFException {
		String encoder = getEncodedString(format.getDefaultMIMEType());
		return encoder;
	}

	private String getEncodedString(String text) throws RDFException {
		String result = null;
		try {
			result = URLEncoder.encode(text, encode);

		} catch (UnsupportedEncodingException e) {
			String message = "Encode " + encode + " is not supported. ";
			logger.debug(message);
			throw new RDFException(message + e.getMessage(), e);
		}
		return result;
	}

	/**
	 *
	 * @param endpointURL      URL of endpoint we can to connect to.
	 * @param endpointGraphURI Name of graph as URI string we want to
	 *                         extract/load RDF data.
	 * @param query            SPARQL query to execute on sparql endpoint
	 *
	 * @return Result of given SPARQL query apply to given graph. If it produce
	 *         some RDF data, there are in specified RDF format.
	 * @throws RDFException if unknown host, connection problems, no permission
	 *                      for this action.
	 */
	private InputStreamReader getUnencodedQueryStreamReader(URL endpointURL,
			List<String> endpointGraphURI, String query) throws RDFException {

		String defaultGraphParam = getGraphParam(endpointParams
				.getDefaultGraphParam(), endpointGraphURI, true);

		final String parameters = defaultGraphParam;

		URL call = null;
		try {
			call = new URL(endpointURL.toString() + parameters);

		} catch (MalformedURLException e) {
			final String message = "Malfolmed URL exception by construct extract URL. ";
			logger.debug(message);
			throw new RDFException(message + e.getMessage(), e);
		}

		HttpURLConnection httpConnection = null;

		int retryCount = 0;

		while (true) {
			try {

				httpConnection = (HttpURLConnection) call.openConnection();
				setPOSTConnection(httpConnection, query,
						"application/sparql-update");

				try (OutputStream os = httpConnection.getOutputStream()) {
					os.write(query.getBytes());
					os.flush();
				}

				int responseCode = httpConnection.getResponseCode();

				if (responseCode != HTTP_OK_RESPONSE) {

					String errorMessage = getHTTPResponseErrorMessage(
							httpConnection);

					throw new InsertPartException(
							errorMessage + "\n\n" + "URL endpoint: " + endpointURL
							.toString() + " POST direct content: " + query);
				} else {

					InputStreamReader inputStreamReader = new InputStreamReader(
							httpConnection.getInputStream(), Charset.forName(
							encode));

					return inputStreamReader;
				}

			} catch (UnknownHostException e) {
				final String message = "Unknown host: ";
				throw new RDFException(message + e.getMessage(), e);

			} catch (IOException e) {
				retryCount++;

				if (!retryConnectionAgain(retryCount, endpointURL.toString())) {

					final String errorMessage = "Count of retryConnection is OVER (TOTAL " + retryCount + " ATTEMPTS). "
							+ "Endpoint HTTP connection stream cannot be opened. ";

					logger.debug(errorMessage);

					if (httpConnection != null) {
						httpConnection.disconnect();
					}

					throw new RDFException(errorMessage + e.getMessage(), e);
				}


			}
		}
	}
}