package cz.cuni.mff.xrg.odcs.extractor.rdf;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.httpconnection.utils.Authentificator;

import static cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType.*;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFCancelException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.handlers.StatisticalHandler;
import cz.cuni.mff.xrg.odcs.rdf.handlers.TripleCountHandler;
import cz.cuni.mff.xrg.odcs.rdf.help.ParamController;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.TripleCounter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.*;

/**
 *
 * Responsible to extract RDF data from SPARQL endpoint. Need special for SPARQL
 * extractor DPU - separed implementation from {@link BaseRDFRepo}.
 *
 * @author Jiri Tomes
 */
public class SPARQLExtractor {

	private static Logger logger = Logger.getLogger(SPARQLExtractor.class);

	/**
	 * Default construct query using for extraction without query in parameter.
	 */
	private static final String DEFAULT_CONSTRUCT_QUERY = "CONSTRUCT {?x ?y ?z} WHERE {?x ?y ?z}";

	private RDFDataUnit dataUnit;

	private DPUContext context;

	public SPARQLExtractor(RDFDataUnit dataUnit, DPUContext context) {
		this.dataUnit = dataUnit;
		this.context = context;
	}

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph without authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are stored.
	 * @throws RDFException when extraction data from SPARQL endpoint fail.
	 */
	public void extractFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphURI) throws RDFException {

		extractFromSPARQLEndpoint(endpointURL, defaultGraphURI,
				DEFAULT_CONSTRUCT_QUERY);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph using authentication (name,password).
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are stored.
	 * @param hostName        String name needed for authentication.
	 * @param password        String password needed for authentication.
	 *
	 * @throws RDFException when extraction data from SPARQL endpoint fail.
	 */
	public void extractFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphURI, String hostName, String password) throws RDFException {

		extractFromSPARQLEndpoint(endpointURL, defaultGraphURI,
				DEFAULT_CONSTRUCT_QUERY, hostName, password);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph using authentication (name,password).
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are loading.
	 * @param query           String SPARQL query.
	 * @param hostName        String name needed for authentication.
	 * @param password        String password needed for authentication.
	 *
	 * @throws RDFException when extraction data fault.
	 */
	public void extractFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphURI, String query, String hostName,
			String password) throws RDFException {

		extractFromSPARQLEndpoint(endpointURL, defaultGraphURI, query,
				hostName,
				password, RDFFormat.N3);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph without authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are loading.
	 * @param query           String SPARQL query.
	 * @throws RDFException when extraction data fault.
	 */
	public void extractFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphUri, String query) throws RDFException {

		List<String> endpointGraphsURI = new ArrayList<>();
		endpointGraphsURI.add(defaultGraphUri);

		extractFromSPARQLEndpoint(endpointURL, endpointGraphsURI, query, "",
				"",
				RDFFormat.N3, HandlerExtractType.STANDARD_HANDLER, false);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph using authentication (name,password).
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are loading.
	 * @param query           String SPARQL query.
	 * @param hostName        String name needed for authentication.
	 * @param password        String password needed for authentication.
	 * @param format          Type of RDF format for saving data (example:
	 *                        TURTLE, RDF/XML,etc.)
	 * @throws RDFException when extraction data fault.
	 */
	public void extractFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphUri, String query, String hostName,
			String password, RDFFormat format) throws RDFException {
		List<String> endpointGraphsURI = new ArrayList<>();
		endpointGraphsURI.add(defaultGraphUri);

		extractFromSPARQLEndpoint(endpointURL, endpointGraphsURI, query,
				hostName, password, format, HandlerExtractType.STANDARD_HANDLER,
				false);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * collection of URI graphs using authentication (name,password).
	 *
	 * @param endpointURL        Remote URL connection to SPARQL endpoint
	 *                           contains RDF data.
	 * @param defaultGraphsUri   List with names of graph where RDF data are
	 *                           loading.
	 * @param query              String SPARQL query.
	 * @param hostName           String name needed for authentication.
	 * @param password           String password needed for authentication.
	 * @param format             Type of RDF format for saving data (example:
	 *                           TURTLE, RDF/XML,etc.)
	 * @param handlerExtractType Possibilies how to choose handler for data
	 *                           extraction and how to solve finded problems
	 *                           with no valid data.
	 * @param extractFail        boolean value, if true stop pipeline(cause
	 *                           exception) when no triples were extracted. if
	 *                           false step triple count extraction criterium.
	 * @throws RDFException when extraction data fault.
	 */
	public void extractFromSPARQLEndpoint(
			URL endpointURL,
			List<String> endpointGraphsURI,
			String query,
			String hostName,
			String password,
			RDFFormat format,
			HandlerExtractType handlerExtractType, boolean extractFail) throws RDFException {

		ParamController.testEndpointSyntax(endpointURL);

		ParamController.testNullParameter(endpointGraphsURI,
				"Mandatory graph's name(s) in extractor from SPARQL is null.");
		ParamController.testEmptyParameter(endpointGraphsURI,
				"Mandatory graph's name(s) in extractor from SPARQL is empty.");

		ParamController.testNullParameter(query,
				"Mandatory construct query is null");
		ParamController.testEmptyParameter(query, "Construct query is empty");

		final int graphSize = endpointGraphsURI.size();

		RepositoryConnection connection = null;

		try {
			connection = dataUnit.getConnection();
			Authentificator.authenticate(hostName, password);

			for (int i = 0; i < graphSize; i++) {
				if (context.canceled()) {
					break;
				} else {
					final String endpointGraph = endpointGraphsURI.get(i);

					extractDataFromEnpointGraph(endpointURL, endpointGraph,
							query,
							format, connection, handlerExtractType, extractFail);
				}

			}
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
					logger.warn(
							"Failed to close connection to RDF repository while extracting from SPQRQL endpoint.",
							ex);
				}
			}
		}

	}

	private void extractDataFromEnpointGraph(URL endpointURL,
			String endpointGraph, String query, RDFFormat format,
			RepositoryConnection connection,
			HandlerExtractType handlerExtractType, boolean extractFail) throws RDFException {

		InputStreamReader inputStreamReader = dataUnit.getEndpointStreamReader(
				endpointURL, endpointGraph, query, format);

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

		handler.setGraphContext(dataUnit.getDataGraph());

		RDFParser parser = dataUnit.getRDFParser(format, handler);

		try {
			parser.parse(inputStreamReader, endpointGraph);

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
			dataUnit.cleanAllData();

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
}
