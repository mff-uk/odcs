package cz.cuni.mff.xrg.odcs.loader.rdf;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.httpconnection.utils.Authentificator;

import static cz.cuni.mff.xrg.odcs.rdf.enums.InsertType.*;

import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;

import static cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType.*;

import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.GraphNotEmptyException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InsertPartException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.help.ParamController;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.BaseRDFRepo;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.openrdf.model.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;

/**
 *
 * Responsible to load RDF data to SPARQL endpoint.
 *
 * @author Jiri Tomes
 */
public class SPARQLoader {

	private static Logger logger = Logger.getLogger(SPARQLoader.class);

	private RDFDataUnit rdfDataUnit;

	private DPUContext context;

	public SPARQLoader(RDFDataUnit rdfDataUnit, DPUContext context) {
		this.rdfDataUnit = rdfDataUnit;
		this.context = context;
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
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI List with names of graph where RDF data are
	 *                        loading.
	 * @param userName        String name needed for authentication.
	 * @param password        String password needed for authentication.
	 * @param graphType       One of way, how to solve loading RDF data to graph
	 *                        when is it is not empty (MERGE, OVERRIDE, FAIL).
	 * @param insertType      One of way, how solve loading RDF data parts to
	 *                        SPARQL endpoint (SKIP_BAD_TYPES,
	 *                        STOP_WHEN_BAD_PART).
	 * @param chunkSize       Size of insert part of triples which insert at
	 *                        once to SPARQL endpoint.
	 * @throws RDFException when loading data fault.
	 */
	public void loadToSPARQLEndpoint(URL endpointURL,
			List<String> namedGraph, String userName,
			String password, WriteGraphType graphType, InsertType insertType,
			long chunkSize)
			throws RDFException {

		//check that SPARQL endpoint URL is correct
		ParamController.testEndpointSyntax(endpointURL);

		ParamController.testNullParameter(namedGraph,
				"Named graph must be specifed");
		ParamController.testEmptyParameter(namedGraph,
				"Named graph must be specifed");

		ParamController.testPositiveParameter(chunkSize,
				"Chunk size must be number greater than 0");

		Authentificator.authenticate(userName, password);

		RepositoryConnection connection = null;

		try {

			connection = rdfDataUnit.getDataRepository().getConnection();

			for (int i = 0; i < namedGraph.size(); i++) {
				final String endpointGraph = namedGraph.get(i);

				//clean target graph if nessasarry - via using given WriteGraphType 
				prepareGraphTargetForLoading(endpointURL, endpointGraph,
						graphType);

				//starting to load data to target SPARQL endpoint
				loadGraphDataToEndpoint(endpointURL, endpointGraph, chunkSize,
						insertType);

				if (context.canceled()) {
					throw new InsertPartException(
							"Loading data to SPARQL endpoint " + endpointURL + " was canceled by user");
				}
			}

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

	private void loadGraphDataToEndpoint(URL endpointURL, String endpointGraph,
			long chunkSize, InsertType insertType) throws RDFException {

		final String tempGraph = endpointGraph + "/temp";

		switch (insertType) {
			case STOP_WHEN_BAD_PART:
				try {
					loadDataParts(endpointURL, tempGraph, insertType,
							chunkSize);
					if (!context.canceled()) {
						moveDataToTarget(endpointURL, tempGraph,
								endpointGraph);
					}


				} catch (InsertPartException e) {
					throw new RDFException(e.getMessage(), e);
				} finally {
					rdfDataUnit.clearEndpointGraph(endpointURL, tempGraph);
				}
				break;
			case SKIP_BAD_PARTS:
				loadDataParts(endpointURL, endpointGraph, insertType,
						chunkSize);
				break;
			case REPEAT_IF_BAD_PART:
				while (true) {
					try {
						loadDataParts(endpointURL, tempGraph, insertType,
								chunkSize);
						if (!context.canceled()) {
							moveDataToTarget(endpointURL, tempGraph,
									endpointGraph);
						}
						break; //loaded sucessfull - leave infinite loop

					} catch (InsertPartException e) {
						//log message with destription of insert part problem.
						logger.debug(e.getMessage());
					} finally {
						rdfDataUnit.clearEndpointGraph(endpointURL, tempGraph);
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
					rdfDataUnit.clearEndpointGraph(endpointURL, endpointGraph);
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

	private void loadDataParts(URL endpointURL, String endpointGraph,
			InsertType insertType, long chunkSize)
			throws RDFException {

		RepositoryResult<Statement> lazy = rdfDataUnit.getRepositoryResult();

		String part = getInsertQueryPart(chunkSize, lazy);

		long counter = 0;
		long partsCount = rdfDataUnit.getPartsCount(chunkSize);

		while (part != null) {
			counter++;

			if (context.canceled()) {
				//stop loading Parts
				logger.error("Loading data was canceled by user !!!");
				break;
			}
			final String query = part;
			part = getInsertQueryPart(chunkSize, lazy);

			final String processing = String.valueOf(counter) + "/" + String
					.valueOf(partsCount);

			try {
				InputStreamReader inputStreamReader = rdfDataUnit
						.getEndpointStreamReader(
						endpointURL, endpointGraph, query,
						RDFFormat.N3);

				inputStreamReader.close();

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

	private void moveDataToTarget(URL endpointURL, String tempGraph,
			String targetGraph) throws RDFException {

		String moveQuery = String.format("DEFINE sql:log-enable 2 \n"
				+ "ADD <%s> TO <%s>", tempGraph, targetGraph);

		String start = String.format(
				"Query for moving data from temp GRAPH <%s> to target GRAPH <%s> prepared.",
				tempGraph, targetGraph);

		logger.debug(start);

		try {
			InputStreamReader result = rdfDataUnit.getEndpointStreamReader(
					endpointURL,
					"", moveQuery, RDFFormat.RDFXML);
		} catch (RDFException e) {
			String exception = String.format(
					"Moving from temp GRAPH <%s> to target GRAPH <%s> FAILED.",
					tempGraph, targetGraph);

			logger.error(exception);
			throw new RDFException(e.getMessage(), e);
		}

		String finish = String.format(
				"All data from temp GRAPH <%s> to GRAPH <%s> were moved sucessfully",
				tempGraph, targetGraph);

		logger.debug(finish);
	}

	private long getSPARQLEnpointGraphSize(URL endpointURL, String endpointGraph)
			throws RDFException {
		String countQuery = "select count(*) as ?count where {?x ?y ?z}";

		InputStreamReader inputStreamReader = rdfDataUnit
				.getEndpointStreamReader(
				endpointURL, endpointGraph,
				countQuery, RDFFormat.RDFXML);

		long count = -1;

		try (Scanner scanner = new Scanner(inputStreamReader)) {

			String regexp = ">[0-9]+<";
			Pattern pattern = Pattern.compile(regexp);
			boolean find = false;

			while (scanner.hasNext() & !find) {
				String line = scanner.next();
				Matcher matcher = pattern.matcher(line);

				if (matcher.find()) {
					String number = line.substring(matcher.start() + 1, matcher
							.end() - 1);
					count = Long.parseLong(number);
					find = true;

				}

			}
		}

		return count;

	}

	private String getInsertQueryPart(long sizeSplit,
			RepositoryResult<Statement> lazy) {

		final String insertStart = "INSERT {";
		final String insertStop = "} ";

		StringBuilder builder = new StringBuilder();

		builder.append(insertStart);

		long count = 0;

		try {
			while (lazy.hasNext()) {

				Statement next = lazy.next();

				Resource subject = next.getSubject();
				URI predicate = next.getPredicate();
				Value object = next.getObject();

				String appendLine = getSubjectInsertText(subject) + " "
						+ getPredicateInsertText(predicate) + " "
						+ getObjectInsertText(object) + " .";

				builder.append(appendLine);

				count++;
				if (count == sizeSplit) {
					builder.append(insertStop);
					return builder.toString();

				}
			}

			if (count > 0) {
				builder.append(insertStop);
				return builder.toString();
			}
		} catch (RepositoryException e) {
			logger.debug(e.getMessage(), e);
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
}
