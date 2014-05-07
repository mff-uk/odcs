package cz.cuni.mff.xrg.odcs.loader.rdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.GraphNotEmptyException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InsertPartException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.help.ParamController;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

/**
 *
 * Responsible to load RDF data to SPARQL endpoint. Need special for SPARQL
 * loader DPU - separed implementation from {@link BaseRDFRepo}.
 *
 * @author Jiri Tomes
 */
public class SPARQLoader {

	private static Logger logger = LoggerFactory.getLogger(SPARQLoader.class);

	/**
	 * How many triples is possible to add to SPARQL endpoind at once.
	 */
	private static final long DEFAULT_CHUNK_SIZE = 10;

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
	 * Represents prefix of the OK response code (could be 200, but also 204,
	 * etc)
	 */
	private static final int HTTP_OK_RESPONSE_PREFIX = 2;

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
	 * Count of reconnection if connection failed. For infinite loop use zero or
	 * negative integer.
	 */
	private int RETRY_CONNECTION_SIZE;

	/**
	 * Time in ms how long wait before re-connection attempt.
	 */
	private long RETRY_CONNECTION_TIME;

	private DPUContext context;

	/**
	 * Request HTTP parameters neeed for setting target SPARQL endpoint.
	 */
	private LoaderEndpointParams endpointParams;

    private String password;
    private String username;

	/**
	 *
	 * @return default size of statements for chunk to load to SPARQL endpoint.
	 */
	public static long getDefaultChunkSize() {
		return DEFAULT_CHUNK_SIZE;
	}

	/**
	 * Responsible for mapping (graph name -> count of loaded RDF triple to this
	 * graph)
	 */
	private Map<String, Long> graphSizeMap = new HashMap<>();
    
        //to hold config.useGraphProtocol
        private final boolean useGraphProtocol;

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
     * @param username
     * @param password
     */
	public SPARQLoader(RDFDataUnit rdfDataUnit, DPUContext context,
                       int retrySize, long retryTime, LoaderEndpointParams endpointParams, boolean useGraphProtocol, String username, String password) {
		this.rdfDataUnit = rdfDataUnit;
		this.context = context;
		this.endpointParams = endpointParams;
                this.useGraphProtocol = useGraphProtocol;
        this.username = username;
        this.password = password;
		setRetryConnectionSize(retrySize);
		setRetryConnectionTime(retryTime);
	}

	/**
	 * Constructor for using in DPUs calling with default retrySize and
	 * retryTime values.
	 *
     * @param rdfDataUnit    Instance of RDFDataUnit repository neeed for
     *                       loading
     * @param context        Given DPU context for DPU over it are executed.
     * @param endpointParams Request HTTP parameters neeed for setting target
     * @param username
     * @param password
     */
	public SPARQLoader(RDFDataUnit rdfDataUnit, DPUContext context,
                       LoaderEndpointParams endpointParams, boolean useGraphProtocol, String username, String password) {
		this.rdfDataUnit = rdfDataUnit;
		this.context = context;
		this.endpointParams = endpointParams;
                this.useGraphProtocol = useGraphProtocol;
        this.username = username;
        this.password = password;
		setRetryConnectionSize(DEFAULT_LOADER_RETRY_SIZE);
		setRetryConnectionTime(DEFAUTL_LOADER_RETRY_TIME);
	}
        
	/**
	 *
	 * Set time in miliseconds how long to wait before trying to reconnect.
	 *
	 * @param retryTimeValue time in milisecond for waiting before trying to
	 *                       reconnect.
	 * @throws IllegalArgumentException if time is 0 or negative long number.
	 */
	public final void setRetryConnectionTime(long retryTimeValue) throws IllegalArgumentException {
		if (retryTimeValue >= 0) {
			RETRY_CONNECTION_TIME = retryTimeValue;
		} else {
			throw new IllegalArgumentException(
					"Retry connection time must be positive number or 0");
		}
	}

	/**
	 * Set Count of attempts to reconnect if the connection fails. For infinite
	 * loop use zero or negative integer
	 *
	 * @param retrySizeValue as interger with count of attemts to reconnect.
	 */
	public final void setRetryConnectionSize(int retrySizeValue) {
		RETRY_CONNECTION_SIZE = retrySizeValue;
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
				graphType, insertType, SPARQLoader.getDefaultChunkSize());
	}

	/**
	 * Load RDF data from repository to SPARQL endpointURL to the collection of
	 * URI graphs without endpoint authentication.
	 *
	 * @param endpointURL       Remote URL connection to SPARQL endpoint
	 *                          contains RDF data.
	 * @param endpointGraphsURI List with names of graph where RDF data are
	 *                          loading.
	 * @param graphType         One of way, how to solve loading RDF data to
	 *                          graph when is it is not empty (MERGE, OVERRIDE,
	 *                          FAIL).
	 * @param insertType        One of way, how solve loading RDF data parts to
	 *                          SPARQL endpoint (SKIP_BAD_TYPES,
	 *                          STOP_WHEN_BAD_PART).
	 * @throws RDFException when loading data to SPARQL endpoint fail.
	 */
	public void loadToSPARQLEndpoint(URL endpointURL,
			List<String> endpointGraphsURI, WriteGraphType graphType,
			InsertType insertType) throws RDFException {

		loadToSPARQLEndpoint(endpointURL, endpointGraphsURI, "", "",
				graphType, insertType, SPARQLoader.getDefaultChunkSize());
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
				graphType, insertType, SPARQLoader.getDefaultChunkSize());
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


		RepositoryConnection connection = null;

		try {

			connection = rdfDataUnit.getConnection();


			for (int i = 0; i < graphs.size(); i++) {
				final String endpointGraph = graphs.get(i);

				//clean target graph if nessasarry - via using given WriteGraphType 
                                prepareGraphTargetForLoading(endpointURL, endpointGraph, graphType);

				if (context.canceled()) {
					logger.error(
							"Loading data to SPARQL endpoint " + endpointURL + " was canceled by user");
					break;
				}

			}

			//starting to load data to target SPARQL endpoint
			loadGraphDataToEndpoint(endpointURL, graphs, chunkSize, insertType, userName, password);


		} catch (RepositoryException ex) {
			throw new RDFException("Repository connection failed. " + ex
					.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					context.sendMessage(MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
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

		int graphNumber = 0;

		for (String nextGraph : graphs) {
			graphNumber++;
			String tempGraph = String.format("%s/%s/temp", rdfDataUnit
					.getDataGraph(), graphNumber);
			collection.add(nextGraph, tempGraph);
		}

		return collection;
	}

	private void noteGraphSize(URL endpointURL, GraphPairCollection collection)
			throws RDFException {

		graphSizeMap.clear();

		for (GraphPair nextPair : collection.getGraphPairs()) {
			long size = getSPARQLEndpointGraphSize(endpointURL, nextPair
					.getTempGraphName());

			graphSizeMap.put(nextPair.getGraphName(), size);
		}
	}

	/**
	 * Return how many RDF triples was loaded to the given target graph.
	 *
	 * @param graph String value of URI graph where data are stored.
	 * @return count of loaded RDF triples to the given target graph.
	 */
	public long getLoadedTripleCount(String graph) {
		if (graphSizeMap.containsKey(graph)) {
			return graphSizeMap.get(graph);
		} else {
			return 0;
		}
	}

	private void loadGraphDataToEndpoint(URL endpointURL,
			List<String> targetGraphs,
			long chunkSize, InsertType insertType, String userName,
			String password) throws RDFException {

		final GraphPairCollection collection = getGraphPairs(targetGraphs);

		switch (insertType) {
			case STOP_WHEN_BAD_PART:
			case SKIP_BAD_PARTS:
				try {
                                       if (useGraphProtocol) {
                                           //SPARQL graph protocol is used
                                            loadDataPartsUsingGraphStoreProtocol(endpointURL, collection.getTempGraphs(), userName, password);
                                       }
                                       else {
                                           //normal execution, sparql-auth. 
                                           loadDataParts(endpointURL, collection.getTempGraphs(),
							insertType,
							chunkSize);
                                       }
                                    
					noteGraphSize(endpointURL, collection);

					if (!context.canceled()) {
						moveDataToTarget(endpointURL, collection);
					}


				} catch (InsertPartException e) {
					throw new RDFException(e.getMessage(), e);
				} finally {
					clearEndpointGraphs(endpointURL, collection.getTempGraphs());
				}
				break;

			case REPEAT_IF_BAD_PART:
				while (true) {
					try {
                                              if (useGraphProtocol) {
                                                  //SPARQL graph protocol is used
                                                   loadDataPartsUsingGraphStoreProtocol(endpointURL, collection.getTempGraphs(), userName, password);
                                              }
                                              else {
                                                  //normal execution, sparql-auth. 
                                                  loadDataParts(endpointURL, collection.getTempGraphs(),
                                                               insertType,
                                                               chunkSize);
                                              }	
                                            
                                            noteGraphSize(endpointURL, collection);
                                        
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
				case OVERRIDE:
					// clear graph
					clearEndpointGraph(endpointURL, endpointGraph);
					break;
				case FAIL:
					//if target graph is not empty, exception is thrown

					long SPARQLGraphSize = getSPARQLEndpointGraphSize(
							endpointURL, endpointGraph);


					if (SPARQLGraphSize > 0) {
						throw new GraphNotEmptyException(
								"Graph <" + endpointGraph + "> is not empty (has "
								+ SPARQLGraphSize
								+ " triples) - Loading to SPARQL endpoint FAILs.");
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

		long partsCount = 0;
		RepositoryConnection connection = null;
        try {
        	connection = rdfDataUnit.getConnection();
            long size = connection.size(rdfDataUnit.getDataGraph());
            partsCount = size / chunkSize;
            if (size % chunkSize > 0) {
                partsCount++;
            }
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        } finally {
        	if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					context.sendMessage(MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
				}
			}
        }


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

				logger.info(
						"Data part " + processing + " loaded successful");

			} catch (InsertPartException e) {
				String message;

				switch (insertType) {
					case SKIP_BAD_PARTS: //go to next part
						message = "Data part " + processing + " was skipped. "
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
		String moveQuery;

		moveQuery = String.format("ADD <%s> TO <%s>", tempGraph, targetGraph);

		String start = String.format(
				"Query for moving data from temp GRAPH <%s> to target GRAPH <%s> prepared.",
				tempGraph, targetGraph);

		logger.debug(start);

		int retryCount = 0;

		while (true) {
			try {
				try (InputStreamReader result = getWholeRepEndpointStreamReader(
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
				retryCount++;

				if (retryCount > RETRY_CONNECTION_SIZE && !hasInfinityRetryConnection()) {
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
						Thread.sleep(RETRY_CONNECTION_TIME);

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

	private InputStreamReader getWholeRepEndpointStreamReader(URL endpointURL,
			String query) throws RDFException {

//		return getEncodedStreamReader(endpointURL, new LinkedList<String>(),
//				query, RDFFormat.RDFXML);
                //the graph is not specified, because it is in the query!
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
	 * Returns graph size for given graph and SPARQL endpoint required
	 * authentification.
	 * @param endpointURL   URL of SPARQL endpoint where we can find graph size.
     *
     * @param endpointGraph String name of graph which size we can find out.
	 * @param hostName      String value of hostname.
	 * @param password      String value of password.
	 * @return graph size for given graph and SPARQL endpoint.
	 * @throws RDFException if endpoint is not available or cause problems.
	 */
	public long getSPARQLEndpointGraphSize(URL endpointURL, String endpointGraph,
			String hostName, String password) throws RDFException {

		return getSPARQLEndpointGraphSize(endpointURL, endpointGraph);
	}

	/**
	 * Returns graph size for given graph and SPARQL endpoint.
	 *
	 * @param endpointURL   URL of SPARQL endpoint where we can find graph size.
	 * @param endpointGraph String name of graph which size we can find out.
	 * @return graph size for given graph and SPARQL endpoint.
	 * @throws RDFException if endpoint is not available or cause problems.
	 */
	public long getSPARQLEndpointGraphSize(URL endpointURL,
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
				retryCount++;

				if (retryCount > RETRY_CONNECTION_SIZE && !hasInfinityRetryConnection()) {
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
						Thread.sleep(RETRY_CONNECTION_TIME);

					} catch (InterruptedException ex) {
						logger.debug(ex.getMessage());
					}
				}
			}
		}


		return count;

	}

	private boolean hasInfinityRetryConnection() {
		if (RETRY_CONNECTION_SIZE < 0) {
			return true;
		} else {
			return false;
		}
	}

	private GraphQueryResult getTriplesPart(String constructQuery) throws InvalidQueryException {
		RepositoryConnection connection = null;
		try {
			connection = rdfDataUnit.getConnection();

			GraphQuery graphQuery = connection.prepareGraphQuery(
					QueryLanguage.SPARQL,
					constructQuery);
            DatasetImpl dataSet = new DatasetImpl();
            dataSet.addDefaultGraph(rdfDataUnit.getDataGraph());
            dataSet.addNamedGraph(rdfDataUnit.getDataGraph());
			graphQuery.setDataset(dataSet);

			GraphQueryResult result = graphQuery.evaluate();
			return result;

		} catch (QueryEvaluationException | MalformedQueryException ex) {
			throw new InvalidQueryException(
					"Given query for lazy triples is probably not valid. "
					+ ex.getMessage(), ex);
		} catch (RepositoryException ex) {
			
			logger.error("Connection to RDF repository failed. "
					+ ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					context.sendMessage(MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
				}
			}
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

				builder.delete(0, builder.length());
				retryCount++;
				String error = String.format("Problem by creating %s"
						+ ". data part - ATTEMPT number %s: ", loadedPartsCount,
						retryCount);

				if (retryCount > RETRY_CONNECTION_SIZE && RETRY_CONNECTION_SIZE >= 0) {
					throw new RDFException(error + e.getMessage(), e);

				} else {
					logger.debug(error + e.getMessage());
					try {
						//sleep and attempt to reconnect
						Thread.sleep(RETRY_CONNECTION_TIME);

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
	 *
	 * @throws RDFException When you dont have update right for this action, or
	 *                      connection is lost before succesfully ending.
	 */
	public void clearEndpointGraph(URL endpointURL, String endpointGraph)
			throws RDFException {

                //TODO Virtuoso specific part DEFINE sql:log-enable 3 - because of non-effective clearing of graphs which caused that removing graph was often unsuccessful
                logger.warn("Virtuoso specific extension of the query is used: DEFINE sql:log-enable 3");
		String deleteQuery = String.format("DEFINE sql:log-enable 3 CLEAR GRAPH <%s>", endpointGraph); 

		int retryCount = 0;

		while (true) {
			try {
				try (InputStreamReader inputStreamReader = getWholeRepEndpointStreamReader(
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
				retryCount++;

				if (retryCount > RETRY_CONNECTION_SIZE && !hasInfinityRetryConnection()) {
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
						Thread.sleep(RETRY_CONNECTION_TIME);

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
        if (this.username.length() > 0) {
            String userPass = this.username + ":" + this.password;
            Base64 coder = new Base64(-1);
            byte[] userPassEncoded = coder.encode(userPass.getBytes());
            String basicAuth = "Basic " + new String(userPassEncoded);
            httpConnection.setRequestProperty("Authorization", basicAuth);
        }
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

		logger.debug("Target endpoint: {}", endpointURL.toString());
		logger.debug("Request content: {}", parameters);
		logger.debug("Request method: POST with URL encoder");

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

				int httpResponseCode = httpConnection.getResponseCode();
				String httpResponseMessage = httpConnection.getResponseMessage();

				logger.debug("HTTP Response code: {}", httpResponseCode);
				logger.debug("HTTP Response message: {}", httpResponseMessage);

				int firstResponseNumber = getFirstResponseNumber(
						httpResponseCode);

				if (firstResponseNumber != HTTP_OK_RESPONSE_PREFIX) {

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

                                logger.error("There was problem submitting the file {}", e.getLocalizedMessage());
				if (!retryConnectionAgain(retryCount, endpointURL.toString())) {
                                        //it should not try again
                                    
					final String errorMessage = "Count of retryConnection is OVER (TOTAL " + retryCount + " ATTEMPTS). "
							+ "Endpoint HTTP connection stream cannot be opened. ";

					logger.debug(errorMessage);

					if (httpConnection != null) {
						httpConnection.disconnect();
					}

					throw new RDFException(errorMessage + e.getMessage(), e);
				}
                                //otherwise, it si trying to connect again


			}
		}
	}

	private boolean retryConnectionAgain(int retryCount, String targetEndpoint) {

		final String message = String
				.format("%s/%s attempt to reconnect %s FAILED", retryCount,
				getRetryConnectionSizeAsString(), targetEndpoint);

		if (retryCount > RETRY_CONNECTION_SIZE && !hasInfinityRetryConnection()) {
			return false;

		} else {
			logger.debug(message);
			try {
				//sleep and attempt to reconnect
				Thread.sleep(RETRY_CONNECTION_TIME);

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
			if (RETRY_CONNECTION_SIZE == 0) {
				return "only 1";
			} else {
				return String.valueOf(RETRY_CONNECTION_SIZE);
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
        
        
        private String getInsertQueryGraphStoreProtocol() throws RDFException {

	

		StringBuilder builder = new StringBuilder();


		int retryCount = 0;
                int count = 0;
		while (true) {
			try {

                            logger.debug("PProcessing of triples started");
				
                             RepositoryConnection connection = null;
                             try {
                            	 connection = rdfDataUnit.getConnection();
                                
                                String queryResFile = "";
                                FileOutputStream fos = new FileOutputStream(new File(queryResFile));
                                RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, fos);
                           
                                
                                connection.prepareGraphQuery(QueryLanguage.SPARQL,"CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o } ").evaluate(writer);
                            } catch (MalformedQueryException | RDFHandlerException | RepositoryException | FileNotFoundException ex) {
                            	context.sendMessage(MessageType.ERROR, ex.getMessage(), ex.fillInStackTrace().toString());
                            } finally {
                            	if (connection != null) {
                    				try {
                    					connection.close();
                    				} catch (RepositoryException ex) {
                    					context.sendMessage(MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                    				}
                    			}
                            }
                                
                                 GraphQueryResult lazy = getTriplesPart("CONSTRUCT {?a ?b ?c} WHERE {?a ?b ?c}");
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
					
				}

                                logger.debug("PProcessed {} triples", count);
				if (count > 0) {
					
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

				builder.delete(0, builder.length());
				retryCount++;
				String error = String.format("Problem preparing data to be loaded - ATTEMPT number %s: ", 
						retryCount);

				if (retryCount > RETRY_CONNECTION_SIZE && RETRY_CONNECTION_SIZE >= 0) {
					throw new RDFException(error + e.getMessage(), e);

				} else {
					logger.debug(error + e.getMessage());
					try {
						//sleep and attempt to reconnect
						Thread.sleep(RETRY_CONNECTION_TIME);

					} catch (InterruptedException ex) {
						logger.debug(ex.getMessage(), ex);
					}
				}

			}

		}
		return null;
	}
        
        private void loadDataPartsUsingGraphStoreProtocol(URL endpointURL, List<String> endpointGraphs, String userName,
			String password) throws RDFException {

            logger.info("Loading data using graph store protocol started");
             String fileData = context.getWorkingDir() + File.separator + "data.ttl";
             FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(new File(fileData));
            } catch (FileNotFoundException ex) {
                 logger.error(ex.getLocalizedMessage());
            }
            RepositoryConnection connection = null;
             try {
                 logger.debug("Phase 1 Started: data is serialized to RDF/XML file");
                 connection= rdfDataUnit.getConnection();
                
                GraphQuery graphQuery = connection.prepareGraphQuery(QueryLanguage.SPARQL, "CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o } ");
            DatasetImpl dataSet = new DatasetImpl();
            dataSet.addDefaultGraph(rdfDataUnit.getDataGraph());
            dataSet.addNamedGraph(rdfDataUnit.getDataGraph());
	        graphQuery.setDataset(dataSet);
                logger.debug("Dataset: {}", dataSet);

                connection.begin();
                
                RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, fos);
		graphQuery.evaluate(writer);
              
                connection.commit();
                
                logger.debug("Phase 1 Finished: data is serialized to RDF/XML file");
                
            } catch (MalformedQueryException ex) {
                logger.error(ex.getLocalizedMessage());
            } catch (QueryEvaluationException ex) {
               logger.error(ex.getLocalizedMessage());
            } catch (RDFHandlerException ex) {
                logger.error(ex.getLocalizedMessage());
            } catch (RepositoryException ex) {
                logger.error(ex.getLocalizedMessage());
            }finally {
            	if (connection != null) {
    				try {
    					connection.close();
    				} catch (RepositoryException ex) {
    					context.sendMessage(MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
    				}
    			}
                 try {
                     fos.flush();
                     fos.close();
                 } catch (IOException ex) {
                     logger.error(ex.getLocalizedMessage());
                 }
            }

           logger.debug("Phase 2 Started : data is being submitted to the server");  
           sparqlGraphProtocolPOSTTest(endpointURL, endpointGraphs, fileData, userName, password);
           logger.debug("Phase 2 Finished : data is being submitted to the server");  
           
           logger.info("Loading data using graph store protocol finished");


      }

        
        private void sparqlGraphProtocolPOSTTest(URL endpointURL, List<String> endpointGraph, String fileData, String userName,
			String password) throws RDFException {

                String graphProtocolEndpoint = null;
                
                //create new client using the given creadentials
                CloseableHttpClient httpclient = null;
                boolean authentizationRequired = false;
                
                if (endpointURL.toString().contains("sparql-auth")) {
                    //sparql auth endpoint
                    graphProtocolEndpoint = endpointURL.toString().replace("sparql-auth", "sparql-graph-crud-auth");
                    //use https:// so that there is no extra negotiation
                    //graphProtocolEndpoint = graphProtocolEndpoint.replaceFirst("http://", "https://");
                    
                    //prepare new credentialProvider
                    CredentialsProvider credsProvider = new BasicCredentialsProvider();
                    credsProvider.setCredentials(
                        AuthScope.ANY,
                        new UsernamePasswordCredentials(userName, password));

                    //create new client using the given creadentials
                    httpclient = HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider)
                    .build();
                    
                    authentizationRequired = true;
                    
                }
                else if (endpointURL.toString().contains("sparql")) {
                    //only sparql endpoint
                    graphProtocolEndpoint = endpointURL.toString().replace("sparql", "sparql-graph-crud");
                    
                    //no authentization needed
                    //create new client using the given creadentials
                    httpclient = HttpClients.custom()          
                    .build();
                    
                }
                else {
                    logger.error("Strange endpoint address {}, CRUD endpoint was not set properly. Loader will probably not be able to connect to the target endpoint.  ", endpointURL.toString());
                }
                
                //final String parameters = "?graph=http://test/loader/graphProtocol";     
                String requestUriParameters = getGraphParam("graph", endpointGraph, true);
		logger.debug("Target endpoint: {}", graphProtocolEndpoint);
		logger.debug("Parameters of the request: {}", requestUriParameters);
		logger.debug("Request method: SPARQL Graph protocol");

                //prepare new post request
                HttpPost post = new HttpPost(graphProtocolEndpoint + requestUriParameters);
                if (authentizationRequired) { 
                    post.addHeader("X-Requested-Auth", "Digest");
                    //Basic authentication not supported by Virtuoso
                    //using Digest authentication which is supported by Virtuoso
                } 
               
                
                post.addHeader("Content-Type", "application/xml"); 
                //text/turtle not supported by Virtuoso
                //post.addHeader("Content-Type", "text/turtle");
                
                //create new file entity being submitted via HTTP POST
                 FileEntity fileEntity = new FileEntity(
                    new File(fileData), ContentType.create("application/rdf+xml", "UTF-8"));
                 post.setEntity(fileEntity);
                 //TODO chunked mode is not working, problem with Virtuoso?
                 //fileEntity.setChunked(true);
                 logger.info("Is the transfer chunked? {}", fileEntity.isChunked());


                logger.info("Executing HTTP POST Request " + post.getRequestLine());
                if (httpclient == null) {
                    context.sendMessage(MessageType.ERROR, "Graph Store Protocol: HTTP POST failed, http client cannot be initialized");
                }
                try {
                    HttpResponse response = httpclient.execute(post);
                    logger.info("Result: {}, {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
                
                } catch(IOException e) {
                    context.sendMessage(MessageType.ERROR, "Graph Store Protocol: HTTP POST failed, " + e.getLocalizedMessage());
                }
                
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

		logger.debug("Target endpoint: {}", endpointURL.toString());
		logger.debug("Request query: {}", query);
		logger.debug("Parameters in URL adress: {}", parameters);
		logger.debug("Request method: POST with unencoded query");

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

				int httpResponseCode = httpConnection.getResponseCode();
				String httpResponseMessage = httpConnection.getResponseMessage();

				logger.debug("HTTP Response code: {}", httpResponseCode);
				logger.debug("HTTP Response message: {}", httpResponseMessage);

				int firstResponseNumber = getFirstResponseNumber(
						httpResponseCode);

				if (firstResponseNumber != HTTP_OK_RESPONSE_PREFIX) {

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

	/**
	 * Returns the first digit of the http response code.
	 *
	 * @param httpResponseCode number of HTTP response code
	 * @return The first digit of the http response code.
	 */
	private int getFirstResponseNumber(int httpResponseCode) {

		try {
			int firstNumberResponseCode = Integer.valueOf((String.valueOf(
					httpResponseCode)).substring(0, 1));

			return firstNumberResponseCode;

		} catch (NumberFormatException e) {
			logger.error(e.getLocalizedMessage());
			logger.debug(
					"Strange response code. First char of response code set to 0");
			return 0;
		}
	}

   

   
}
