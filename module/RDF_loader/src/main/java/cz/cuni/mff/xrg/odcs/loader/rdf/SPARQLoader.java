package cz.cuni.mff.xrg.odcs.loader.rdf;

import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.dataset.CleverDataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible to load RDF data to SPARQL endpoint. Need special for SPARQL
 * loader DPU - separed implementation from {@link BaseRDFRepo}.
 * 
 * @author Jiri Tomes
 */
public class SPARQLoader {

    private static Logger logger = LoggerFactory.getLogger(SPARQLoader.class);

    /**
     * Default used encoding.
     */
    private static final String encode = "UTF-8";

    private RDFDataUnit inputRdfDataUnit;

    private RDFLoaderConfig config;

    private DPUContext context;

    private CloseableHttpClient httpClient;

    /**
     * Responsible for mapping (graph name -> count of loaded RDF triple to this
     * graph)
     */
    private Map<String, Long> graphSizeMap = new HashMap<>();

    /**
     * Constructor for using in DPUs calling.
     * 
     * @param rdfDataUnit
     *            Instance of RDFDataUnit repository neeed for
     *            loading
     * @param context
     *            Given DPU context for DPU over it are executed.
     * @param retrySize
     *            Integer value as count of attempts to reconnect if
     *            the connection fails. For infinite loop use zero or
     *            negative integer
     * @param retryTime
     *            Long value as time in miliseconds how long to wait
     *            before trying to reconnect.
     * @param endpointParams
     *            Request HTTP parameters neeed for setting target
     * @param username
     * @param password
     */
    public SPARQLoader(RDFDataUnit rdfDataUnit, DPUContext context,
            RDFLoaderConfig config) {
        this.inputRdfDataUnit = rdfDataUnit;
        this.context = context;
        this.config = config;
    }

    /**
     * Load RDF data from repository to SPARQL endpointURL to the collection of
     * URI graphs with endpoint authentication (name,password).
     * 
     * @param endpointURL
     *            Remote URL connection to SPARQL endpoint contains RDF
     *            data.
     * @param graphs
     *            List with names of graph where RDF data are loading.
     * @param userName
     *            String name needed for authentication.
     * @param password
     *            String password needed for authentication.
     * @param graphType
     *            One of way, how to solve loading RDF data to graph
     *            when is it is not empty (MERGE, OVERRIDE, FAIL).
     * @param insertType
     *            One of way, how solve loading RDF data parts to SPARQL
     *            endpoint (SKIP_BAD_TYPES, STOP_WHEN_BAD_PART).
     * @param chunkSize
     *            Size of insert part of triples which insert at once to
     *            SPARQL endpoint.
     */
    public void loadToSPARQLEndpoint()
            throws DPUException {
        String endpointURL = config.getSPARQL_endpoint();
        try {
            if (endpointURL.contains("sparql-auth")) {
                //sparql auth endpoint
                endpointURL = endpointURL.replace("sparql-auth", "sparql-graph-crud-auth");
                //use https:// so that there is no extra negotiation
                //graphProtocolEndpoint = graphProtocolEndpoint.replaceFirst("http://", "https://");

                //prepare new credentialProvider
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                        AuthScope.ANY,
                        new UsernamePasswordCredentials(config.getHost_name(), config.getPassword()));

                //create new client using the given creadentials
                httpClient = HttpClients.custom()
                        .setDefaultCredentialsProvider(credsProvider)
                        .build();

            } else if (endpointURL.contains("sparql")) {
                //only sparql endpoint
                endpointURL = endpointURL.replace("sparql", "sparql-graph-crud");

                //no authentization needed
                httpClient = HttpClients.custom()
                        .build();
            } else {
                logger.error("Strange endpoint address {}, CRUD endpoint was not set properly. Loader will probably not be able to connect to the target endpoint.  ", endpointURL.toString());
                httpClient = HttpClients.custom()
                        .build();
            }

            boolean shouldRepeat = true;
            int retryCount = 0;
            GraphPairCollection collection = null;
            while (shouldRepeat) {
                shouldRepeat = false;
                retryCount++;

                try {
                    for (String graph : config.getGraphsUri()) {
                        Long graphSizeBefore = getSPARQLEndpointGraphSize(
                                graph);

                        context.sendMessage(DPUContext.MessageType.INFO, String.format(
                                "Target graph <%s> contains %s RDF triples before loading to SPARQL endpoint %s",
                                graph, graphSizeBefore, config.getSPARQL_endpoint()));
                        checkCancel();
                    }

                    for (String endpointGraph : config.getGraphsUri()) {
                        //clean target graph if nessasarry - via using given WriteGraphType 
                        if (WriteGraphType.FAIL.equals(config.getGraphOption())) {
                            //if target graph is not empty, exception is thrown

                            long SPARQLGraphSize = getSPARQLEndpointGraphSize(
                                    endpointGraph);

                            if (SPARQLGraphSize > 0) {
                                throw new DPUException(
                                        "Graph <" + endpointGraph + "> is not empty (has "
                                                + SPARQLGraphSize
                                                + " triples) - Loading to SPARQL endpoint FAILs.");
                            }
                        }
                        checkCancel();
                    }
                    //starting to load data to target SPARQL endpoint

                    collection = getGraphPairs(config.getGraphsUri());

                    //SPARQL graph protocol is used
                    checkCancel();
                    loadDataPartsUsingGraphStoreProtocol(endpointURL, collection.getTempGraphs());

                    checkCancel();
                    noteGraphSize(collection);

                    checkCancel();
                    moveDataToTarget(collection);
                    
                    clearEndpointGraphs(endpointURL, collection.getTempGraphs());

                    for (String graph : config.getGraphsUri()) {
                        checkCancel();

                        Long graphSizeAfter = getSPARQLEndpointGraphSize(
                                graph);

                        context.sendMessage(DPUContext.MessageType.INFO, String.format(
                                "Target graph <%s> contains %s RDF triples after loading to SPARQL endpoint %s",
                                graph, graphSizeAfter, endpointURL.toString()));

                        long loadedTriples = getLoadedTripleCount(graph);

                        context.sendMessage(DPUContext.MessageType.INFO, String.format(
                                "Loaded %s triples to SPARQL endpoint %s",
                                loadedTriples, endpointURL.toString()));
                    }
                    
                } catch (DPUNonFatalException ex) {
                    checkCancel();

                    shouldRepeat = false;
                    switch (config.getInsertOption()) {
                        case SKIP_BAD_PARTS:
                        case REPEAT_IF_BAD_PART:
                            if (retryCount == config.getRetrySize()) {
                                final String errorMessage = String.format(
                                        "Count of retryConnection for Loading data to SPARQL endpoint is OVER (TOTAL %s ATTEMPTS). ",
                                        retryCount);
                                logger.debug(errorMessage);

                                throw new DPUException(errorMessage, ex);
                            } else {
                                final String errorMessage = String.format(
                                        "Attempt %s Loading data to SPARQL endpoint failed. Reason of cause :%s",
                                        retryCount, ex.getMessage());

                                logger.warn(errorMessage);
                                context.sendMessage(DPUContext.MessageType.WARNING, errorMessage, "", ex);

                                try {
                                    //sleep and attempt to reconnect
                                    Thread.sleep(config.getRetryTime());

                                } catch (InterruptedException ex1) {
                                    logger.debug(ex1.getMessage());
                                }
                                shouldRepeat = true;
                                checkCancel();
                            }
                            break;
                        case STOP_WHEN_BAD_PART:
                            throw new DPUException(ex);
                    }
                }
            }

        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException ex) {
                context.sendMessage(DPUContext.MessageType.WARNING, "Error in close httpClient", "", ex);
            }
        }
    }

    /**
     * Create pair graph-temp_graph for each graph as target of loading and add
     * it to collection which returns.
     * 
     * @param graphs
     *            Collection of names of graphs where are data loader
     * @return instance of {@link GraphPairCollection} with pairs
     *         graph,tempGraph.
     * @throws DPUException 
     */
    private GraphPairCollection getGraphPairs(List<String> graphs) throws DPUException {

        GraphPairCollection collection = new GraphPairCollection();

        for (String nextGraph : graphs) {
            String tempGraph = String.format("%s/temp", nextGraph);
            collection.add(nextGraph, tempGraph);
            clearEndpointGraph(tempGraph);
            checkCancel();
        }

        return collection;
    }

    private void noteGraphSize(GraphPairCollection collection) throws DPUException {
        graphSizeMap.clear();
        for (GraphPair nextPair : collection.getGraphPairs()) {
            long size = getSPARQLEndpointGraphSize(nextPair
                    .getTempGraphName());

            graphSizeMap.put(nextPair.getGraphName(), size);
            checkCancel();

        }
    }

    /**
     * Return how many RDF triples was loaded to the given target graph.
     * 
     * @param graph
     *            String value of URI graph where data are stored.
     * @return count of loaded RDF triples to the given target graph.
     */
    private long getLoadedTripleCount(String graph) {
        if (graphSizeMap.containsKey(graph)) {
            return graphSizeMap.get(graph);
        } else {
            return 0;
        }
    }

    /**
     * For each pair from collection move data from temp_graph to graph.
     * 
     * @param endpoint
     *            target endpoint where data are loaded to.
     * @param collection
     *            collection contains pairs graph-temp_graph
     * @throws DPUException 
     */
    private void moveDataToTarget(GraphPairCollection collection) throws DPUException {

        for (GraphPair nextPair : collection.getGraphPairs()) {
            moveDataToTarget(nextPair.getTempGraphName(), nextPair.getGraphName());
            checkCancel();

        }
    }

    /**
     * Move data after successfully loading all data part from temp graph to
     * graph defined before start loading.
     * 
     * @param endpointURL
     *            target endpoint where data are loaded to.
     * @param tempGraph
     *            String value of graph from data are moved
     * @param targetGraph
     *            String value of graph to data are moved.
     */
    private void moveDataToTarget(String tempGraph, String targetGraph) throws DPUNonFatalException {
        String moveQuery;
        if (WriteGraphType.OVERRIDE.equals(config.getGraphOption())) {
            moveQuery = String.format("MOVE <%s> TO <%s>", tempGraph, targetGraph);
        } else {
            moveQuery = String.format("ADD <%s> TO <%s>", tempGraph, targetGraph);
        }
        logger.debug(String.format(
                "Query for moving data from temp GRAPH <%s> to target GRAPH <%s> prepared.",
                tempGraph, targetGraph));

        CloseableHttpResponse response = getEndpointStreamReader(
                config.getSPARQL_endpoint(), Collections.<String> emptyList(), moveQuery);
        EntityUtils.consumeQuietly(response.getEntity());

        logger.debug(String.format(
                "All data from temp GRAPH <%s> to GRAPH <%s> were moved sucessfully",
                tempGraph, targetGraph));
    }

    private CloseableHttpResponse getEndpointStreamReader(String endpointURL, List<String> endpointGraph, String query) throws DPUNonFatalException {
        LoaderPostType postType = config.getEndpointParams().getPostType();

        switch (postType) {
            case POST_UNENCODED_QUERY:
                return getUnencodedQueryStreamReader(endpointURL, endpointGraph, query);
            case POST_URL_ENCODER:
            default:
                return getEncodedStreamReader(endpointURL, endpointGraph, query, RDFFormat.RDFXML);

        }

    }

    /**
     * Returns graph size for given graph and SPARQL endpoint.
     * 
     * @param endpointURL
     *            URL of SPARQL endpoint where we can find graph size.
     * @param endpointGraph
     *            String name of graph which size we can find out.
     * @return graph size for given graph and SPARQL endpoint.
     * @throws IOException
     * @throws IllegalStateException
     */
    private long getSPARQLEndpointGraphSize(String endpointGraph) throws DPUNonFatalException {

        String countQuery = "SELECT (count(*) as ?count) WHERE {?x ?y ?z}";

        List<String> targetGraphs = new LinkedList<>();
        targetGraphs.add(endpointGraph);

        long count = -1;
        String endpointURL = config.getSPARQL_endpoint();

        CloseableHttpResponse response = getEndpointStreamReader(
                endpointURL, targetGraphs,
                countQuery);

        Scanner scanner = null;
        try {
            scanner = new Scanner(response.getEntity().getContent());
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
        } catch (IOException ex) {
            throw new DPUNonFatalException(ex);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            EntityUtils.consumeQuietly(response.getEntity());
        }

        return count;
    }

    private void clearEndpointGraphs(String endpoint, List<String> endpointGraphs) throws DPUException {
        for (String nextGraph : endpointGraphs) {
            clearEndpointGraph(nextGraph);
            checkCancel();
        }
    }

    /**
     * Removes all RDF data in defined graph using connecion to SPARQL endpoint
     * address. For data deleting is necessarry to have endpoint with update
     * rights.
     * 
     * @param endpointURL
     *            URL address of update endpoint connect to.
     * @param endpointGraph
     *            Graph name in URI format.
     */
    private void clearEndpointGraph(String endpointGraph) throws DPUNonFatalException {
        String endpointURL = config.getSPARQL_endpoint();

        //TODO Virtuoso specific part DEFINE sql:log-enable 3 - because of non-effective clearing of graphs which caused that removing graph was often unsuccessful
//        logger.warn("Virtuoso specific extension of the query is used: DEFINE sql:log-enable 3");
//        String deleteQuery = String.format("DEFINE sql:log-enable 3 CLEAR GRAPH <%s>", endpointGraph);
        String deleteQuery = String.format("CLEAR GRAPH <%s>", endpointGraph);

        CloseableHttpResponse response = getEndpointStreamReader(
                endpointURL, Collections.<String> emptyList(), deleteQuery);
        EntityUtils.consumeQuietly(response.getEntity());
    }

    private String getGraphParam(String graphParam, List<String> graphs, boolean isFirst) {

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
     * @param endpointURL
     *            URL of endpoint we can to connect to.
     * @param endpointGraphURI
     *            Name of graph as URI string we want to
     *            extract/load RDF data.
     * @param query
     *            SPARQL query to execute on sparql endpoint
     * @param format
     *            RDF data format for given returned RDF data.
     * @return Result of given SPARQL query apply to given graph. If it produce
     *         some RDF data, there are in specified RDF format.
     */
    private CloseableHttpResponse getEncodedStreamReader(String endpointURL, List<String> endpointGraphURI, String query, RDFFormat format) throws DPUNonFatalException {

        String queryParam = String.format("?%s=%s", config.getEndpointParams()
                .getQueryParam(),
                getEncodedString(query));

        String defaultGraphParam = getGraphParam(config.getEndpointParams()
                .getDefaultGraphParam(), endpointGraphURI, false);

        String formatParam = String.format("&format=%s", getEncoder(format));

        final String parameters = queryParam + defaultGraphParam + formatParam;

        logger.debug("Target endpoint: {}", endpointURL.toString());
        logger.debug("Request content: {}", parameters);
        logger.debug("Request method: POST with URL encoder");

        HttpPost post = new HttpPost(endpointURL + parameters);
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
        //text/turtle not supported by Virtuoso
        //post.addHeader("Content-Type", "text/turtle");

        //create new file entity being submitted via HTTP POST
        logger.info("Executing HTTP POST Request " + post.getRequestLine());
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(post);
            logger.info("Result: {}, {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());

            int httpResponseCode = response.getStatusLine().getStatusCode();
            String httpResponseMessage = response.getStatusLine().getReasonPhrase();
            logger.debug("HTTP Response code: {}", httpResponseCode);
            logger.debug("HTTP Response message: {}", httpResponseMessage);

            int firstResponseNumber = getFirstResponseNumber(
                    httpResponseCode);

            if (firstResponseNumber != 2) {

//                        String errorMessage = getHTTPResponseErrorMessage(
//                                httpConnection);

                throw new DPUNonFatalException(
                        httpResponseMessage + "\n\nURL endpoint: " + endpointURL
                                .toString() + " POST content: " + parameters);
            } else {
                return response;
            }
        } catch (IOException ex) {
            throw new DPUNonFatalException(ex);
        }
    }

    private String getEncoder(RDFFormat format) {
        String encoder = getEncodedString(format.getDefaultMIMEType());
        return encoder;
    }

    private String getEncodedString(String text) {
        String result = null;
        try {
            result = URLEncoder.encode(text, encode);

        } catch (UnsupportedEncodingException ex) {
            // Impossible
//            throw new DPUException(ex);
        }
        return result;
    }

    private void loadDataPartsUsingGraphStoreProtocol(String endpointURL, List<String> endpointGraphs) throws DPUException {

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
            connection = inputRdfDataUnit.getConnection();

            GraphQuery graphQuery = connection.prepareGraphQuery(QueryLanguage.SPARQL, "CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o } ");
            CleverDataset dataSet = new CleverDataset();
            dataSet.addDefaultGraphs(inputRdfDataUnit.getDataGraphnames());
            dataSet.addNamedGraphs(inputRdfDataUnit.getDataGraphnames());
            graphQuery.setDataset(dataSet);
            logger.debug("Dataset: {}", dataSet);

            RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, fos);
            graphQuery.evaluate(writer);

            logger.debug("Phase 1 Finished: data is serialized to RDF/XML file");

        } catch (RepositoryException | DataUnitException | QueryEvaluationException | RDFHandlerException | MalformedQueryException ex) {
            throw new DPUNonFatalException(ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    context.sendMessage(DPUContext.MessageType.WARNING, "Error closing connection", ex.getMessage(), ex);
                }
            }
            try {
                fos.flush();
                fos.close();
            } catch (IOException ex) {
                throw new DPUNonFatalException(ex);
            }
        }
        checkCancel();

        logger.debug("Phase 2 Started : data is being submitted to the server");

        //final String parameters = "?graph=http://test/loader/graphProtocol";     
        String requestUriParameters = getGraphParam("graph", endpointGraphs, true);
        logger.debug("Target endpoint: {}", endpointURL);
        logger.debug("Parameters of the request: {}", requestUriParameters);
        logger.debug("Request method: SPARQL Graph protocol");

        //prepare new post request
        HttpPost post = new HttpPost(endpointURL + requestUriParameters);

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
        HttpResponse response = null;
        try {
            response = httpClient.execute(post);
            logger.info("Result: {}, {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
        } catch (IOException ex) {
            throw new DPUNonFatalException(ex);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }
        logger.debug("Phase 2 Finished : data is being submitted to the server");

        logger.info("Loading data using graph store protocol finished");
    }

    /**
     * @param endpointURL
     *            URL of endpoint we can to connect to.
     * @param endpointGraphURI
     *            Name of graph as URI string we want to
     *            extract/load RDF data.
     * @param query
     *            SPARQL query to execute on sparql endpoint
     * @return Result of given SPARQL query apply to given graph. If it produce
     *         some RDF data, there are in specified RDF format.
     */
    private CloseableHttpResponse getUnencodedQueryStreamReader(String endpointURL,
            List<String> endpointGraphURI, String query) throws DPUNonFatalException {

        String defaultGraphParam = getGraphParam(config.getEndpointParams()
                .getDefaultGraphParam(), endpointGraphURI, true);

        final String parameters = defaultGraphParam;

        logger.debug("Target endpoint: {}", endpointURL.toString());
        logger.debug("Request query: {}", query);
        logger.debug("Parameters in URL adress: {}", parameters);
        logger.debug("Request method: POST with unencoded query");

        HttpPost post = new HttpPost(endpointURL + parameters);
        post.addHeader("Content-Type", "application/sparql-update");

        //create new file entity being submitted via HTTP POST
        logger.info("Executing HTTP POST Request " + post.getRequestLine());

        CloseableHttpResponse response;
        try {
            response = httpClient.execute(post);
            logger.info("Result: {}, {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());

            int httpResponseCode = response.getStatusLine().getStatusCode();
            String httpResponseMessage = response.getStatusLine().getReasonPhrase();
            logger.debug("HTTP Response code: {}", httpResponseCode);
            logger.debug("HTTP Response message: {}", httpResponseMessage);

            int firstResponseNumber = getFirstResponseNumber(
                    httpResponseCode);

            if (firstResponseNumber != 2) {

                throw new DPUNonFatalException(
                        httpResponseMessage + "\n\nURL endpoint: " + endpointURL
                                .toString() + " POST content: " + parameters);
            } else {
                return response;
            }
        } catch (IOException ex) {
            throw new DPUNonFatalException(ex);
        }
    }

    /**
     * Returns the first digit of the http response code.
     * 
     * @param httpResponseCode
     *            number of HTTP response code
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

    private void checkCancel() throws DPUException {
        if (context.canceled()) {
            throw new DPUException("Loading data to SPARQL endpoint was canceled by user");
        }
    }
}
