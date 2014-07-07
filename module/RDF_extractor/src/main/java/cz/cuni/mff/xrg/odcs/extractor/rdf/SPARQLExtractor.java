package cz.cuni.mff.xrg.odcs.extractor.rdf;

import java.io.BufferedReader;
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
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.ParseErrorListener;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;

import cz.cuni.mff.xrg.odcs.rdf.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.WritableRDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InsertPartException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFCancelException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.handlers.StatisticalHandler;
import cz.cuni.mff.xrg.odcs.rdf.handlers.TripleCountHandler;
import cz.cuni.mff.xrg.odcs.rdf.help.ParamController;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.TripleCounter;

/**
 * Responsible for the RDF data extraction from SPARQL endpoint. Class contains
 * special methods for SPARQL extractor DPU.
 * 
 * @author Jiri Tomes
 */
public class SPARQLExtractor {

    private static Logger logger = LoggerFactory
            .getLogger(SPARQLExtractor.class);

    /**
     * Default construct query using for extraction without query in parameter.
     */
    private static final String DEFAULT_CONSTRUCT_QUERY = "CONSTRUCT {?x ?y ?z} WHERE {?x ?y ?z}";

    /**
     * Count of attempts to reconnect if the connection fails.
     */
    private static final int DEFAULT_EXTRACTOR_RETRY_SIZE = -1;

    /**
     * Time in miliseconds how long to wait before trying to reconnect.
     */
    private static final long DEFAULT_EXTRACTOR_RETRY_TIME = 1000;

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

    private WritableRDFDataUnit dataUnit;

    private DPUContext context;

    /**
     * Count of reconnection if connection failed. For infinite loop use zero or
     * negative integer.
     */
    private int RETRY_CONNECTION_SIZE;

    /**
     * Time in ms how long wait before re-connection attempt.
     */
    private long RETRY_CONNECTION_TIME;

    /**
     * Request HTTP parameters neeed for setting SPARQL endpoint.
     */
    private ExtractorEndpointParams endpointParams;

    private String username;

    private String password;

    /**
     * Create new instance of SPARQLExtractor with given parameters.
     * 
     * @param dataUnit
     *            Instance of RDFDataUnit repository neeed for
     *            extraction from SPARQL endpoint.
     * @param retrySize
     *            Integer value as count of attempts to reconnect if
     *            the connection fails. For infinite loop use zero or
     *            negative integer
     * @param retryTime
     *            Long value as time in miliseconds how long to wait
     *            before trying to reconnect.
     * @param endpointParams
     *            Request HTTP parameters neeed for setting SPARQL
     *            endpoint.
     */
    public SPARQLExtractor(WritableRDFDataUnit dataUnit, DPUContext context,
            int retrySize, long retryTime,
            ExtractorEndpointParams endpointParams) {

        this.dataUnit = dataUnit;
        this.context = context;
        this.endpointParams = endpointParams;

        setRetryConnectionSize(retrySize);
        setRetryConnectionTime(retryTime);
    }

    /**
     * Create new instance of SPARQLExtractor with given parameters and default
     * retrySize and retryTime values.
     * 
     * @param dataUnit
     *            Instance of RDFDataUnit repository neeed for
     *            loading
     * @param context
     *            Given DPU context for DPU over it are executed.
     * @param endpointParams
     *            Request HTTP parameters neeed for setting SPARQL
     *            endpoint.
     */
    public SPARQLExtractor(WritableRDFDataUnit dataUnit, DPUContext context,
            ExtractorEndpointParams endpointParams) {
        this.dataUnit = dataUnit;
        this.context = context;
        this.endpointParams = endpointParams;

        setRetryConnectionSize(DEFAULT_EXTRACTOR_RETRY_SIZE);
        setRetryConnectionTime(DEFAULT_EXTRACTOR_RETRY_TIME);
    }

    /**
     * Extract RDF data from SPARQL endpoint to repository using only data from
     * URI graph without authentication.
     * 
     * @param endpointURL
     *            Remote URL connection to SPARQL endpoint contains RDF
     *            data.
     * @throws RDFException
     *             when extraction data from SPARQL endpoint fail.
     */
    public void extractFromSPARQLEndpoint(URL endpointURL) throws RDFException {

        extractFromSPARQLEndpoint(endpointURL, DEFAULT_CONSTRUCT_QUERY, "", "");
    }

    /**
     * Extract RDF data from SPARQL endpoint to repository using only data from
     * URI graph without authentication..
     * 
     * @param endpointURL
     *            Remote URL connection to SPARQL endpoint contains RDF
     *            data.
     * @param query
     *            String SPARQL query.
     * @throws RDFException
     *             when extraction data fault.
     */
    public void extractFromSPARQLEndpoint(URL endpointURL,
            String query) throws RDFException {

        extractFromSPARQLEndpoint(endpointURL, query, "", "");
    }

    /**
     * Extract RDF data from SPARQL endpoint to repository using only data from
     * URI graph using authentication (name,password).
     * 
     * @param endpointURL
     *            Remote URL connection to SPARQL endpoint contains RDF
     *            data.
     * @param hostName
     *            String name needed for authentication.
     * @param password
     *            String password needed for authentication.
     * @throws RDFException
     *             when extraction data from SPARQL endpoint fail.
     */
    public void extractFromSPARQLEndpoint(URL endpointURL,
            String hostName, String password) throws RDFException {

        extractFromSPARQLEndpoint(endpointURL, DEFAULT_CONSTRUCT_QUERY, hostName,
                password);
    }

    /**
     * Extract RDF data from SPARQL endpoint to repository using only data from
     * URI graph using authentication (name,password).
     * 
     * @param endpointURL
     *            Remote URL connection to SPARQL endpoint contains RDF
     *            data.
     * @param query
     *            String SPARQL query.
     * @param hostName
     *            String name needed for authentication.
     * @param password
     *            String password needed for authentication.
     * @throws RDFException
     *             when extraction data fault.
     */
    public void extractFromSPARQLEndpoint(URL endpointURL,
            String query, String hostName,
            String password) throws RDFException {

        extractFromSPARQLEndpoint(endpointURL, query,
                hostName, password, RDFFormat.N3);
    }

    /**
     * Extract RDF data from SPARQL endpoint to repository using only data from
     * URI graph using authentication (name,password).
     * 
     * @param endpointURL
     *            Remote URL connection to SPARQL endpoint contains RDF
     *            data.
     * @param query
     *            String SPARQL query.
     * @param hostName
     *            String name needed for authentication.
     * @param password
     *            String password needed for authentication.
     * @param format
     *            Type of RDF format for saving data (example: TURTLE,
     *            RDF/XML,etc.)
     * @throws RDFException
     *             when extraction data fault.
     */
    public void extractFromSPARQLEndpoint(URL endpointURL,
            String query, String hostName, String password, RDFFormat format)
            throws RDFException {

        extractFromSPARQLEndpoint(endpointURL, query,
                hostName, password, format, HandlerExtractType.STANDARD_HANDLER,
                false);
    }

    /**
     * Extract RDF data from SPARQL endpoint to repository using only data from
     * collection of URI graphs using authentication (name,password).
     * 
     * @param endpointURL
     *            Remote URL connection to SPARQL endpoint
     *            contains RDF data.
     * @param query
     *            String SPARQL query.
     * @param hostName
     *            String name needed for authentication.
     * @param password
     *            String password needed for authentication.
     * @param format
     *            Type of RDF format for saving data (example:
     *            TURTLE, RDF/XML,etc.)
     * @param handlerExtractType
     *            Possibilies how to choose handler for data
     *            extraction and how to solve finded problems
     *            with no valid data.
     * @param extractFail
     *            boolean value, if true stop pipeline(cause
     *            exception) when no triples were extracted. if
     *            false step triple count extraction criterium.
     * @throws RDFException
     *             when extraction data fault.
     */
    public void extractFromSPARQLEndpoint(
            URL endpointURL,
            String query,
            String hostName,
            String password,
            RDFFormat format,
            HandlerExtractType handlerExtractType, boolean extractFail) throws RDFException {

        ParamController.testEndpointSyntax(endpointURL);

        ParamController.testNullParameter(query,
                "Mandatory construct query is null");
        ParamController.testEmptyParameter(query, "Construct query is empty");

        this.username = hostName;
        this.password = password;

        RepositoryConnection connection = null;

        try {
            connection = dataUnit.getConnection();
            connection.begin();

            extractDataFromEnpointGraph(endpointURL, query,
                    format, connection, handlerExtractType, extractFail);

            connection.commit();
        } catch (RepositoryException e) {
            final String message = "Repository connection failed: " + e
                    .getMessage();

            logger.debug(message);

            throw new RDFException(message, e);

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    context.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                }
            }
        }

    }

    /**
     * Set time in miliseconds how long to wait before trying to reconnect.
     * 
     * @param retryTimeValue
     *            time in milisecond for waiting before trying to
     *            reconnect.
     * @throws IllegalArgumentException
     *             if time is 0 or negative long number.
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
     * @param retrySizeValue
     *            as interger with count of attemts to reconnect.
     */
    public final void setRetryConnectionSize(int retrySizeValue) {
        RETRY_CONNECTION_SIZE = retrySizeValue;
    }

    private void extractDataFromEnpointGraph(URL endpointURL,
            String query, RDFFormat format,
            RepositoryConnection connection,
            HandlerExtractType handlerExtractType, boolean extractFail) throws RDFException {

        InputStreamReader inputStreamReader = getEndpointStreamReader(
                endpointURL, query, format);

        TripleCountHandler handler;

        boolean failWhenMistake = false;

        switch (handlerExtractType) {
            case STANDARD_HANDLER:
                handler = new TripleCountHandler(connection, context);
                break;
            case ERROR_HANDLER_CONTINUE_WHEN_MISTAKE:
                handler = new StatisticalHandler(connection, context);
                break;
            case ERROR_HANDLER_FAIL_WHEN_MISTAKE:
                handler = new StatisticalHandler(connection, context);
                failWhenMistake = true;
                break;
            default:
                handler = new TripleCountHandler(connection);
                break;
        }

        handler.setGraphContext(dataUnit.getWriteContext());

        RDFParser parser = getRDFParser(format, handler);

        try {

            parser.parse(inputStreamReader, "");

            if (extractFail) {
                caseNoTriples(handler);
            }

            if (handler instanceof StatisticalHandler) {
                StatisticalHandler errorHandler = (StatisticalHandler) handler;

                if (errorHandler.hasFindedProblems() && failWhenMistake) {

                    throw new RDFException(errorHandler
                            .getFindedProblemsAsString());
                }
            }

        } catch (IOException ex) {
            final String message = "Http connection can can not open stream. ";
            logger.error(message);

            throw new RDFException(message + ex.getMessage(), ex);

        } catch (RDFCancelException e) {
            logger.debug(e.getMessage());
            try {
                connection.clear(dataUnit.getWriteContext());
            } catch (RepositoryException e1) {
                logger.debug(e.getMessage());
                throw new RDFException(e1.getMessage(), e1);
            }

        } catch (RDFHandlerException | RDFParseException ex) {
            logger.error(ex.getMessage(), ex);
            throw new RDFException(ex.getMessage(), ex);
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException ex) {
                    logger.error("InputStream reader was not closed: " + ex
                            .getMessage(), ex);
                }
            }
        }

    }

    private void caseNoTriples(TripleCounter handler) throws RDFException {

        if (handler.isEmpty()) {
            throw new RDFException("No extracted triples from SPARQL endpoint");
        }
    }

    private InputStreamReader getEndpointStreamReader(URL endpointURL,
            String query, RDFFormat format) throws RDFException {

        ExtractorRequestType requestType = endpointParams.getRequestType();

        switch (requestType) {
            case GET_URL_ENCODER:
                return getEncodedGETStreamReader(endpointURL, query, format);

            case POST_UNENCODED_QUERY:
                return getUnencodedQueryStreamReader(endpointURL, query, format);
            case POST_URL_ENCODER:
            default:
                return getEncodedPOSTStreamReader(endpointURL, query, format);

        }
    }

    private String getGraphParam(String graphParam, List<String> graphs,
            boolean isFirstParam)
            throws RDFException {

        if (graphs.isEmpty()) {
            return "";
        } else {
            StringBuilder result = new StringBuilder();

            for (String nextGraphURI : graphs) {
                if (isFirstParam) {
                    isFirstParam = false;
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
     * @param query
     *            SPARQL query to execute on sparql endpoint
     * @param format
     *            RDF data format for given returned RDF data.
     * @return Result of given SPARQL query apply to given graph. If it produce
     *         some RDF data, there are in specified RDF format.
     * @throws RDFException
     *             if unknown host, connection problems, no permission
     *             for this action.
     */
    private InputStreamReader getEncodedGETStreamReader(URL endpointURL,
            String query, RDFFormat format) throws RDFException {

        String queryParam = String.format("?%s=%s", endpointParams
                .getQueryParam(), getEncodedString(query));

        String defaultGraphParam = getGraphParam(endpointParams
                .getDefaultGraphParam(), endpointParams
                .getDefaultGraphURI(), false);

        String namedGraphParam = getGraphParam(endpointParams
                .getNamedGraphParam(), endpointParams
                .getNamedGraphURI(), false);

        String formatParam = String.format("&format=%s", getEncoder(format));

        final String parameters = queryParam + defaultGraphParam + namedGraphParam + formatParam;

        logger.debug("Target endpoint: {}", endpointURL.toString());
        logger.debug("SPARQL query: {}", query);
        logger.debug("Request content: {}", parameters);
        logger.debug("Request method: GET");

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
                httpConnection.setRequestMethod("GET");
                httpConnection.setUseCaches(false);
                httpConnection.setDoInput(true);
                httpConnection.setDoOutput(true);
                if (this.username.length() > 0) {
                    String userPass = this.username + ":" + this.password;
                    Base64 coder = new Base64(-1);
                    byte[] userPassEncoded = coder.encode(userPass.getBytes());
                    String basicAuth = "Basic " + new String(userPassEncoded);
                    httpConnection.setRequestProperty("Authorization", basicAuth);
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
        httpConnection.setRequestProperty("Content-Length", ""
                + Integer.toString(parameters.getBytes().length));

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

    /**
     * @param endpointURL
     *            URL of endpoint we can to connect to.
     * @param query
     *            SPARQL query to execute on sparql endpoint
     * @param format
     *            RDF data format for given returned RDF data.
     * @return Result of given SPARQL query apply to given graph. If it produce
     *         some RDF data, there are in specified RDF format.
     * @throws RDFException
     *             if unknown host, connection problems, no permission
     *             for this action.
     */
    private InputStreamReader getEncodedPOSTStreamReader(URL endpointURL,
            String query, RDFFormat format) throws RDFException {

        String queryParam = String.format("%s=%s", endpointParams
                .getQueryParam(), getEncodedString(query));

        String defaultGraphParam = getGraphParam(endpointParams
                .getDefaultGraphParam(),
                endpointParams.getDefaultGraphURI(), false);

        String namedGraphParam = getGraphParam(endpointParams
                .getNamedGraphParam(), endpointParams
                .getNamedGraphURI(), false);

        String formatParam = String.format("&format=%s", getEncoder(format));

        final String parameters = queryParam + defaultGraphParam + namedGraphParam + formatParam;

        logger.debug("Target endpoint: {}", endpointURL.toString());
        logger.debug("SPARQL query: {}", query);
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
     * @throws RDFException
     *             if unknown host, connection problems, no permission
     *             for this action.
     */
    private InputStreamReader getUnencodedQueryStreamReader(URL endpointURL,
            String query,
            RDFFormat format) throws RDFException {

        String formatParam = String.format("?format=%s", getEncoder(format));

        String defaultGraphParam = getGraphParam(endpointParams
                .getDefaultGraphParam(), endpointParams
                .getDefaultGraphURI(), false);

        String namedGraphParam = getGraphParam(endpointParams
                .getNamedGraphParam(), endpointParams
                .getNamedGraphURI(), false);

        final String parameters = formatParam + defaultGraphParam + namedGraphParam;

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
                        "application/sparql-query");

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
                                    .toString() + " POST direct query: " + query);
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

    private boolean hasInfinityRetryConnection() {
        if (RETRY_CONNECTION_SIZE < 0) {
            return true;
        } else {
            return false;
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

    /**
     * Create RDF parser for given RDF format and set RDF handler where are data
     * insert to.
     * 
     * @param format
     *            RDF format witch is set to RDF parser
     * @param handler
     *            Type of handler where RDF parser used for parsing. If
     *            handler is {@link StatisticalHandler} type, is set error
     *            listener for fix errors here.
     * @return RDFParser for given RDF format and handler.
     */
    private RDFParser getRDFParser(RDFFormat format, TripleCountHandler handler) {
        RDFParser parser = Rio.createParser(format);
        parser.setRDFHandler(handler);

        ParserConfig config = parser.getParserConfig();

        config.addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);

        parser.setParserConfig(config);

        if (handler instanceof StatisticalHandler) {
            final StatisticalHandler statisticalHandler = (StatisticalHandler) handler;

            parser.setParseErrorListener(new ParseErrorListener() {
                @Override
                public void warning(String msg, int lineNo, int colNo) {
                    statisticalHandler.addWarning(msg, lineNo, colNo);
                }

                @Override
                public void error(String msg, int lineNo, int colNo) {
                    statisticalHandler.addError(msg, lineNo, colNo);
                }

                @Override
                public void fatalError(String msg, int lineNo, int colNo) {
                    statisticalHandler.addError(msg, lineNo, colNo);
                }
            });
        }

        return parser;
    }
}
