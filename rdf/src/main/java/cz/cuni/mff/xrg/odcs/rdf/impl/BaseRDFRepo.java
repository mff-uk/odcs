package cz.cuni.mff.xrg.odcs.rdf.impl;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import cz.cuni.mff.xrg.odcs.rdf.enums.SelectFormatType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.GraphNotEmptyException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InsertPartException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.TripleCounter;
import info.aduna.iteration.EmptyIteration;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openrdf.model.*;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.*;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.*;
import org.slf4j.Logger;

/**
 * Abstract class provides common parent methods for RDFDataUnit implementation.
 *
 * @author Jiri Tomes
 */
public abstract class BaseRDFRepo implements RDFDataUnit, Closeable {

	/**
	 * How many triples is possible to add to SPARQL endpoind at once.
	 */
	protected static final long DEFAULT_CHUNK_SIZE = 10;

	/**
	 * Count of attempts to reconnect if the connection fails. For infinite loop
	 * use zero or negative integer.
	 */
	protected static final int RETRY_CONNECTION_SIZE = 20;

	/**
	 * Time in miliseconds how long to wait before trying to reconnect.
	 */
	protected static final long RETRY_CONNECTION_TIME = 1000;

	/**
	 * Represent successfully connection using HTTP.
	 */
	protected static final int HTTP_OK_RESPONSE = 200;

	/**
	 * Represent http error code needed authorisation for connection using HTTP.
	 */
	protected static final int HTTP_UNAUTORIZED_RESPONSE = 401;

	/**
	 * Represent http error code returns when inserting data in bad format.
	 */
	protected static final int HTTP_BAD_RESPONSE = 400;

	/**
	 * Default name for graph using for store RDF data.
	 */
	protected static final String DEFAULT_GRAPH_NAME = "http://default";

	/**
	 * Default construct query using for extraction without query in parameter.
	 */
	protected static final String DEFAULT_CONSTRUCT_QUERY = "construct {?x ?y ?z} where {?x ?y ?z}";

	/**
	 * Logging information about execution of method using openRDF.
	 */
	protected Logger logger;

	/**
	 * RDF data storage component.
	 */
	protected Repository repository;

	/**
	 * Graph resource for saving RDF triples.
	 */
	protected URI graph;

	/**
	 * DataUnit's name.
	 */
	protected String dataUnitName;

	/**
	 * Default used encoding.
	 */
	protected final String encode = "UTF-8";

	/**
	 * If the repository is used only for reading data or not.
	 */
	protected boolean isReadOnly;

	/**
	 * Singletone connection for repository.
	 */
	private RepositoryConnection repoConnection;

	/**
	 * If DPU execution is stopped or not.
	 */
	private boolean isExecutionCanceled = false;

	/**
	 * If is thrown RDFException and need reconnect singleton connection
	 * instance.
	 */
	private boolean hasBrokenConnection = false;

	/**
	 *
	 * @return default size of statements for chunk to load to SPARQL endpoint.
	 */
	public static long getDefaultChunkSize() {
		return DEFAULT_CHUNK_SIZE;
	}

	@Override
	public void addFromFile(File file) throws RDFException {
		extractFromFile(file, RDFFormat.RDFXML, "", false);
	}

	@Override
	public void addFromFile(File file, RDFFormat format) throws RDFException {
		extractFromFile(file, format, "", false);
	}

	@Override
	public void addFromFile(File file, RDFFormat format,
			boolean useStatisticalHandler) throws RDFException {

		extractFromFile(file, format, "", useStatisticalHandler);
	}

	@Override
	public void extractFromFile(File file, RDFFormat format, String baseURI)
			throws RDFException {

		extractFromFile(file, format, baseURI, false);
	}

	@Override
	public void extractFromFile(File file, RDFFormat format, String baseURI,
			boolean useStatisticalHandler) throws RDFException {

		if (file == null) {
			throw new RDFException("Given file for extraction is null");
		}
		extractFromFile(format, FileExtractType.PATH_TO_FILE, file
				.getAbsolutePath(), "",
				baseURI, false, useStatisticalHandler);
	}

	/**
	 * Extract RDF triples from TURTLE file to repository.
	 *
	 * @param file File which contains RDF data to extract.
	 *
	 * @throws RDFException when extraction fail.
	 */
	@Override
	public void addFromTurtleFile(File file) throws RDFException {
		extractFromFile(file, RDFFormat.TURTLE, "", false);
	}

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file File which contains RDF data to extract.
	 *
	 * @throws RDFException when extraction fail.
	 */
	@Override
	public void addFromRDFXMLFile(File file) throws RDFException {
		extractFromFile(file, RDFFormat.RDFXML, "", false);
	}

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param extractType         One of defined enum type for extraction data
	 *                            from file.
	 * @param format              One of RDFFormat value for parsing triples, if
	 *                            value is null RDFFormat is selected by
	 *                            filename.
	 * @param path                String path to file/directory
	 * @param suffix              String suffix of fileName (example: ".ttl",
	 *                            ".xml", etc)
	 * @param baseURI             String name of defined used URI
	 * @param useSuffix           boolean value, if extract files only with
	 *                            defined suffix or not.
	 * @param useStatisticHandler boolean value if detailed log and statistic
	 *                            are awailable or not.
	 * @throws RDFException when extraction fail.
	 */
	@Override
	public void extractFromFile(FileExtractType extractType,
			RDFFormat format,
			String path, String suffix,
			String baseURI, boolean useSuffix, boolean useStatisticHandler)
			throws RDFException {


		extractFromFile(format, extractType, path, suffix, baseURI,
				useSuffix, useStatisticHandler);
	}

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param format              Specifies {@link RDFFormatRDF} (e.g., RDFXML,
	 *                            Turtle, ..)
	 * @param extractType         One of defined enum type for extraction data
	 *                            from file.
	 * @param path                String path to file/directory
	 * @param suffix              String suffix of fileName (example: ".ttl",
	 *                            ".xml", etc)
	 * @param baseURI             String name of defined used URI
	 * @param useSuffix           boolean value, if extract files only with
	 *                            defined suffix or not.
	 * @param useStatisticHandler boolean value if detailed log and statistic
	 *                            are available or not.
	 * @throws RDFException when extraction fail.
	 */
	@Override
	public void extractFromFile(RDFFormat format, FileExtractType extractType,
			String path, String suffix,
			String baseURI, boolean useSuffix, boolean useStatisticHandler)
			throws RDFException {

		if (path == null) {
			final String message = "Mandatory target path in extractor is null.";

			logger.debug(message);
			throw new RDFException(message);

		} else if (path.isEmpty()) {

			final String message = "Mandatory target path in extractor have to be not empty.";

			logger.debug(message);
			throw new RDFException(message);

		}

		File dirFile = new File(path);

		switch (extractType) {
			case HTTP_URL:
				extractDataFileFromHTTPSource(path, baseURI, useStatisticHandler);
				break;
			case PATH_TO_DIRECTORY:
			case PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES:
				if (dirFile.isDirectory()) {
					File[] files = getFilesBySuffix(dirFile, suffix, useSuffix);

					boolean skipFiles = false;

					if (extractType == FileExtractType.PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES) {
						skipFiles = true;
					}

					addFilesInDirectoryToRepository(format, files, baseURI,
							useStatisticHandler, skipFiles,
							graph);
				} else {
					throw new RDFException(
							"Path to directory \"" + path + "\" doesnt exist");
				}
				break;
			case PATH_TO_FILE:
			case UPLOAD_FILE:
				if (dirFile.isFile()) {
					addFileToRepository(format, dirFile, baseURI,
							useStatisticHandler,
							graph);
				} else {
					throw new RDFException(
							"Path to file \"" + path + "\"doesnt exist");
				}
				break;
		}

	}

	@Override
	public void storeToFile(File file, RDFFormatType formatType) throws RDFException {

		if (file == null) {

			final String message = "Given file for loading is null.";

			logger.debug(message);
			throw new RDFException(message);


		} else if (file.getName().isEmpty()) {

			final String message = "File name is empty.";


			logger.debug(message);
			throw new RDFException(message);
		}

		if (!file.exists()) {
			createNewFile(file);

		}

		writeDataIntoFile(file, formatType);

	}

	/**
	 * Load all triples in repository to defined file in defined RDF format.
	 *
	 * @param filePath   Path to file, where RDF data will be saved.
	 * @param formatType Type of RDF format for saving data (example: TURTLE,
	 *                   RDF/XML,etc.)
	 * @throws CannotOverwriteFileException when file is protected for
	 *                                      overwritting.
	 * @throws RDFException                 when loading data fault.
	 */
	@Override
	public void loadToFile(String filePath,
			RDFFormatType formatType) throws CannotOverwriteFileException, RDFException {

		loadToFile(filePath, formatType, false, false);
	}

	/**
	 * Load all triples in repository to defined file in defined RDF format.
	 *
	 * @param filePath         Path to file, where RDF data will be saved.
	 * @param formatType       Type of RDF format for saving data (example:
	 *                         TURTLE, RDF/XML,etc.)
	 * @param canFileOverWrite boolean value, if existing file can be
	 *                         overwritten.
	 * @param isNameUnique     boolean value, if every pipeline execution has
	 *                         his unique name.
	 * @throws CannotOverwriteFileException when file is protected for
	 *                                      overwritting.
	 * @throws RDFException                 when loading data fault.
	 */
	@Override
	public void loadToFile(String filePath, RDFFormatType formatType,
			boolean canFileOverWrite, boolean isNameUnique) throws CannotOverwriteFileException, RDFException {

		if (filePath == null) {

			final String message = "Mandatory file path in File_loader is null.";

			logger.debug(message);
			throw new RDFException(message);


		} else if (filePath.isEmpty()) {

			final String message = "Mandatory file path in File_loader is empty.";
			logger.debug(message);
			throw new RDFException(message);
		}

		File dataFile = new File(filePath);
		File directory = new File(dataFile.getParent());

		if (!directory.exists()) {
			directory.mkdirs();
		}

		if (!dataFile.exists()) {
			createNewFile(dataFile);

		} else {
			if (isNameUnique) {

				String uniqueFileName = UniqueNameGenerator
						.getNextName(dataFile.getName());

				dataFile = new File(directory, uniqueFileName);
				createNewFile(dataFile);

			} else if (canFileOverWrite) {
				createNewFile(dataFile);
			} else {
				logger.debug("File existed and cannot be overwritten");
				throw new CannotOverwriteFileException();
			}

		}

		writeDataIntoFile(dataFile, formatType);

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
	 *                        STOP_WHEN_BAD_PART)
	 * @throws RDFException when loading data fault.
	 */
	@Override
	public void loadToSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType, InsertType insertType) throws RDFException {
		List<String> endpointGraphsURI = new ArrayList<>();
		endpointGraphsURI.add(defaultGraphURI);

		loadToSPARQLEndpoint(endpointURL, endpointGraphsURI, "", "",
				graphType, insertType, DEFAULT_CHUNK_SIZE);
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
	@Override
	public void loadToSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
			String name, String password, WriteGraphType graphType,
			InsertType insertType) throws RDFException {
		List<String> endpointGraphsURI = new ArrayList<>();
		endpointGraphsURI.add(defaultGraphURI);

		loadToSPARQLEndpoint(endpointURL, endpointGraphsURI, name, password,
				graphType, insertType, DEFAULT_CHUNK_SIZE);
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
	@Override
	public void loadToSPARQLEndpoint(URL endpointURL,
			List<String> endpointGraphsURI, WriteGraphType graphType,
			InsertType insertType) throws RDFException {

		loadToSPARQLEndpoint(endpointURL, endpointGraphsURI, "", "",
				graphType, insertType, DEFAULT_CHUNK_SIZE);
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
	@Override
	public void loadToSPARQLEndpoint(URL endpointURL,
			List<String> namedGraph, String userName,
			String password, WriteGraphType graphType, InsertType insertType,
			long chunkSize)
			throws RDFException {

		//check that SPARQL endpoint URL is correct
		if (endpointURL == null) {
			final String message = "SPARQL Endpoint URL must be specified";

			logger.debug(message);
			throw new RDFException(message);

		} else {

			final String endpointName = endpointURL.toString().toLowerCase();

			String message = null;

			if (!endpointName.startsWith("http://")) {
				message = "Endpoint url name have to started with prefix \"http://\".";
			} else if (endpointName.contains(" ")) {
				message = "Endpoint url constains white spaces";
			}
			if (message != null) {
				logger.debug(message);
				throw new RDFException(message);
			}

		}

		if (namedGraph == null) {
			final String message = "Named graph must be specifed";

			logger.debug(message);
			throw new RDFException(message);

		} else if (namedGraph.isEmpty()) {
			final String message = "Named graph must be specifed";

			logger.debug(message);
			throw new RDFException(message);
		}

		if (chunkSize <= 0) {
			final String message = "Chunk size must be number greater than 0";
			logger.debug(message);
			throw new RDFException(message);
		}


		authenticate(userName, password);

		RepositoryConnection connection = null;

		try {

			connection = getConnection();

			for (int i = 0; i < namedGraph.size(); i++) {

				final String endpointGraph = namedGraph.get(i);

				try {
					switch (graphType) {
						case MERGE:
							break;
						case OVERRIDE: {
							//TODO check use of clear/drop graph
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

				//starting to load data to target SPARQL endpoint

				final String tempGraph = endpointGraph + "/temp";

				switch (insertType) {
					case STOP_WHEN_BAD_PART:
						try {
							loadDataParts(endpointURL, tempGraph, insertType,
									chunkSize);
							if (isExecutionCanceled) {
								setExecutionCanceled(false);
							} else {
								moveDataToTarget(endpointURL, tempGraph,
										endpointGraph);
							}

						} catch (InsertPartException e) {
							throw new RDFException(e.getMessage(), e);
						} finally {
							clearEndpointGraph(endpointURL, tempGraph);
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
								moveDataToTarget(endpointURL, tempGraph,
										endpointGraph);
								break; //loaded sucessfull - leave infinite loop

							} catch (InsertPartException e) {
								//log message with destription of insert part problem.
								logger.debug(e.getMessage());
							} finally {
								clearEndpointGraph(endpointURL, tempGraph);
							}
						}
						break;
				}
			}

		} catch (RepositoryException ex) {
			hasBrokenConnection = true;
			throw new RDFException("Repository connection failed. " + ex
					.getMessage(), ex);
		} /*finally {
		 if (connection != null) {
		 try {
		 connection.close();
		 } catch (RepositoryException ex) {
		 logger.warn("Failed to close connection to RDF repository. "
		 + ex.getMessage(), ex);
		 }
		 }

		 }*/
	}

	private long getPartsCount(long chunkSize) {

		long triples = getTripleCount();
		long partsCount = triples / chunkSize;

		if (partsCount * chunkSize != triples) {
			partsCount++;
		}

		return partsCount;
	}

	private RepositoryResult<Statement> getRepoResult() {

		RepositoryResult<Statement> repoResult = null;

		try {
			RepositoryConnection connection = getConnection();

			repoResult = connection.getStatements(null, null, null, true,
					graph);

		} catch (RepositoryException ex) {
			hasBrokenConnection = true;
			logger.debug(ex.getMessage(), ex);
			repoResult = new RepositoryResult<>(
					new EmptyIteration<Statement, RepositoryException>());

		} finally {
			return repoResult;

		}
	}

	private void loadDataParts(URL endpointURL, String endpointGraph,
			InsertType insertType, long chunkSize)
			throws RDFException {

		RepositoryResult<Statement> lazy = getRepoResult();

		String part = getInsertQueryPart(chunkSize, lazy);

		long counter = 0;
		long partsCount = getPartsCount(chunkSize);

		while (part != null) {
			counter++;

			final String query = part;
			part = getInsertQueryPart(chunkSize, lazy);

			final String processing = String.valueOf(counter) + "/" + String
					.valueOf(partsCount);

			try {
				InputStreamReader inputStreamReader = getEndpointStreamReader(
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

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph without authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are stored.
	 * @throws RDFException when extraction data from SPARQL endpoint fail.
	 */
	@Override
	public void addFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphURI) throws RDFException {

		addFromSPARQLEndpoint(endpointURL, defaultGraphURI,
				DEFAULT_CONSTRUCT_QUERY);
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
	@Override
	public void addFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphUri, String query) throws RDFException {

		List<String> endpointGraphsURI = new ArrayList<>();
		endpointGraphsURI.add(defaultGraphUri);

		extractFromSPARQLEndpoint(endpointURL, endpointGraphsURI, query, "",
				"",
				RDFFormat.N3, false, false);
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
	@Override
	public void addFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphURI, String hostName, String password) throws RDFException {

		addFromSPARQLEndpoint(endpointURL, defaultGraphURI,
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
	@Override
	public void addFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphURI, String query, String hostName,
			String password) throws RDFException {

		extractFromSPARQLEndpoint(endpointURL, defaultGraphURI, query,
				hostName,
				password, RDFFormat.N3);
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
	@Override
	public void extractFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphUri, String query, String hostName,
			String password, RDFFormat format) throws RDFException {
		List<String> endpointGraphsURI = new ArrayList<>();
		endpointGraphsURI.add(defaultGraphUri);

		extractFromSPARQLEndpoint(endpointURL, endpointGraphsURI, query,
				hostName, password, format, false, false);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * collection of URI graphs using authentication (name,password).
	 *
	 * @param endpointURL         Remote URL connection to SPARQL endpoint
	 *                            contains RDF data.
	 * @param defaultGraphsUri    List with names of graph where RDF data are
	 *                            loading.
	 * @param query               String SPARQL query.
	 * @param hostName            String name needed for authentication.
	 * @param password            String password needed for authentication.
	 * @param format              Type of RDF format for saving data (example:
	 *                            TURTLE, RDF/XML,etc.)
	 * @param useStatisticHandler boolean value if detailed log and statistic
	 *                            are awailable or not.
	 * @param extractFail         boolean value, if true stop pipeline(cause
	 *                            exception) when no triples were extracted. if
	 *                            false step triple count extraction criterium.
	 * @throws RDFException when extraction data fault.
	 */
	@Override
	public void extractFromSPARQLEndpoint(
			URL endpointURL,
			List<String> endpointGraphsURI,
			String query,
			String hostName,
			String password,
			RDFFormat format,
			boolean useStatisticHandler, boolean extractFail) throws RDFException {

		if (endpointURL == null) {
			final String message = "Mandatory URL path in extractor from SPARQL is null.";

			logger.debug(message);
			throw new RDFException(message);

		} else if (!endpointURL.toString().toLowerCase().startsWith("http")) {

			final String message = "Mandatory URL path in extractor from SPARQL "
					+ "have to started with http.";

			logger.debug(message);
			throw new RDFException(message);

		}

		if (endpointGraphsURI == null) {
			final String message = "Mandatory graph's name(s) in extractor from SPARQL is null.";

			logger.debug(message);
			throw new RDFException(message);

		} else if (endpointGraphsURI.isEmpty()) {
			final String message = "Mandatory graph's name(s) in extractor from SPARQL is empty.";

			logger.debug(message);
			throw new RDFException(message);
		}

		if (query == null) {
			final String message = "Mandatory construct query is null.";
			logger.debug(message);
			throw new RDFException(message);
		} else if (query.isEmpty()) {
			final String message = "Construct query is empty";

			logger.debug(message);
			throw new RDFException(message);
		}

		final int graphSize = endpointGraphsURI.size();

		RepositoryConnection connection = null;

		try {
			connection = getConnection();
			authenticate(hostName, password);

			for (int i = 0; i < graphSize; i++) {

				final String endpointGraph = endpointGraphsURI.get(i);


				InputStreamReader inputStreamReader = getEndpointStreamReader(
						endpointURL, endpointGraph, query, format);

				if (!useStatisticHandler) {

					if (extractFail) {
						TripleCountHandler handler = new TripleCountHandler(
								connection);

						if (graph != null) {
							handler.enforceContext(graph);
						}
						RDFParser parser = getRDFParser(format, handler);
						try {
							parser.parse(inputStreamReader, endpointGraph);
							caseNoTriples(handler);
						} catch (RDFHandlerException e) {
							logger.debug(e.getMessage(), e);
						}
					} else {
						if (graph != null) {
							connection.add(inputStreamReader, endpointGraph,
									format, graph);
						} else {
							connection.add(inputStreamReader, endpointGraph,
									format);
						}

					}

				} else {
					StatisticalHandler handler = new StatisticalHandler();

					RDFParser parser = getRDFParser(format, handler);

					try {
						parser.parse(inputStreamReader, endpointGraph);

						if (extractFail) {
							caseNoTriples(handler);
						}

						if (graph != null) {
							connection.add(handler.getStatements(), graph);
						} else {
							connection.add(handler.getStatements());
						}
					} catch (IOException ex) {
						logger.error(ex.getMessage(), ex);
					} catch (RepositoryException ex) {
						hasBrokenConnection = true;
						logger.error(ex.getMessage(), ex);
					} catch (RDFHandlerException | RDFParseException ex) {
						logger.error(ex.getMessage(), ex);
						throw new RDFException(ex.getMessage(), ex);
					}


				}
			}
		} catch (IOException e) {

			final String message = "Http connection can can not open stream. ";
			logger.debug(message);

			throw new RDFException(message + e.getMessage(), e);

		} catch (RDFParseException e) {
			logger.debug(e.getMessage());

			throw new RDFException(e.getMessage(), e);

		} catch (RepositoryException e) {
			hasBrokenConnection = true;
			final String message = "Repository connection failed: " + e
					.getMessage();

			logger.debug(message);

			throw new RDFException(message, e);

		} /*finally {
		 if (connection != null) {
		 try {
		 connection.close();
		 } catch (RepositoryException ex) {
		 logger.warn(
		 "Failed to close connection to RDF repository while extracting from SPQRQL endpoint.",
		 ex);
		 }
		 }
		 }*/

	}

	private RDFParser getRDFParser(RDFFormat format, RDFHandler handler) {
		RDFParser parser = Rio.createParser(format);
		parser.setRDFHandler(handler);

		return parser;
	}

	private void caseNoTriples(TripleCounter handler) throws RDFException {

		if (handler.isEmpty()) {
			throw new RDFException("No extracted triples from SPARQL endpoint");
		}
	}

	/**
	 * Transform RDF in repository by SPARQL updateQuery.
	 *
	 * @param updateQuery String value of update SPARQL query.
	 * @throws RDFException when transformation fault.
	 */
	@Override
	public void executeSPARQLUpdateQuery(String updateQuery) throws RDFException {

		RepositoryConnection connection = null;
		try {
			connection = getConnection();


			String newUpdateQuery = AddGraphToUpdateQuery(updateQuery);
			Update myupdate = connection.prepareUpdate(QueryLanguage.SPARQL,
					newUpdateQuery);


			logger.debug(
					"This SPARQL query for transform is valid and prepared for execution:");
			logger.debug(newUpdateQuery);

			myupdate.execute();
			connection.commit();

			logger.debug("SPARQL query for transform was executed succesfully");

		} catch (MalformedQueryException e) {

			logger.debug(e.getMessage());
			throw new RDFException(e.getMessage(), e);

		} catch (UpdateExecutionException ex) {

			final String message = "SPARQL query was not executed !!!";
			logger.debug(message);
			logger.debug(ex.getMessage());

			throw new RDFException(message + ex.getMessage(), ex);


		} catch (RepositoryException ex) {
			hasBrokenConnection = true;
			throw new RDFException(
					"Connection to repository is not available. "
					+ ex.getMessage(), ex);
		} /*finally {
		 if (connection != null) {
		 try {
		 connection.close();
		 } catch (RepositoryException ex) {
		 logger.warn(
		 "Failed to close connection to RDF repository while executing SPARQL transform. "
		 + ex.getMessage(), ex);
		 }
		 }
		 }
		 */

	}

	/**
	 * Return count of triples stored in repository.
	 *
	 * @return size of triples in repository.
	 */
	@Override
	public long getTripleCount() {
		long size = 0;

		RepositoryConnection connection = null;

		try {
			connection = getConnection();

			if (graph != null) {
				size = connection.size(graph);
			} else {
				size = connection.size();
			}

		} catch (RepositoryException ex) {
			hasBrokenConnection = true;
			logger.debug(ex.getMessage());
		} /*finally {
		 if (connection != null) {
		 try {
		 connection.close();
		 } catch (RepositoryException ex) {
		 logger.warn(
		 "Failed to close connection to RDF repository while counting triples. "
		 + ex.getMessage(), ex);
		 }
		 }
		 }*/


		return size;
	}

	/**
	 * Return if RDF triple is in repository.
	 *
	 * @param subject   URI or blank node for subject
	 * @param predicate URI for predicate
	 * @param object    URI, blank node or literal for object
	 * @return true if such statement is in repository, false otherwise.
	 */
	@Override
	public boolean isTripleInRepository(Resource subject,
			URI predicate, Value object) {
		boolean hasTriple = false;


		RepositoryConnection connection = null;
		Statement statement = new StatementImpl(subject, predicate, object);

		try {
			connection = getConnection();

			if (graph != null) {
				hasTriple = connection.hasStatement(statement, true, graph);
			} else {
				hasTriple = connection.hasStatement(statement, true);
			}

		} catch (RepositoryException ex) {
			hasBrokenConnection = true;
			logger.debug(ex.getMessage());
		} /*finally {
		 if (connection != null) {
		 try {
		 connection.close();
		 } catch (RepositoryException ex) {
		 logger.warn(
		 "Failed to close connection to RDF repository while looking for triple. "
		 + ex.getMessage(), ex);
		 }
		 }
		 }*/

		return hasTriple;
	}

	/**
	 * Removes all RDF data from repository.
	 */
	@Override
	public void cleanAllData() {

		RepositoryConnection connection = null;
		try {
			connection = getConnection();

			if (graph != null) {
				connection.clear(graph);
			} else {
				connection.clear();
			}

			connection.commit();

		} catch (RepositoryException ex) {
			hasBrokenConnection = true;
			logger.debug(ex.getMessage());
		} /*finally {
		 if (connection != null) {
		 try {
		 connection.close();
		 } catch (RepositoryException ex) {
		 logger.warn(
		 "Failed to close connection to RDF repository while cleaning up. "
		 + ex.getMessage(), ex);
		 }
		 }
		 }*/

	}

	/**
	 * Return all triples(statements) in reposiotory as list.
	 *
	 * @return List<code>&lt;Statement&gt;</code> list of all triples in
	 *         repository/
	 */
	@Override
	public List<Statement> getTriples() {

		List<Statement> statemens = new ArrayList<>();

		if (repository != null) {

			try {
				RepositoryResult<Statement> lazy = getRepoResult();

				while (lazy.hasNext()) {
					Statement next = lazy.next();
					statemens.add(next);
				}

			} catch (RepositoryException ex) {
				hasBrokenConnection = true;
				logger.debug(ex.getMessage(), ex);
			}

		}
		return statemens;
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
			hasBrokenConnection = true;
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

		RepositoryConnection connection = null;

		try {
			connection = getConnection();

			GraphQuery graphQuery = connection.prepareGraphQuery(
					QueryLanguage.SPARQL,
					constructQuery);

			graphQuery.setDataset(getDataSetForGraph());

			logger.debug("Query " + constructQuery + " is valid.");

			try {

				File file = new File(filePath);
				createNewFile(file);

				FileOutputStream os = new FileOutputStream(file);

				MyRDFHandler goal = new MyRDFHandler(os, formatType);

				graphQuery.evaluate(goal);

				logger.debug(
						"Query " + constructQuery + " has not null result.");

				return file;

			} catch (QueryEvaluationException ex) {
				throw new InvalidQueryException(
						"This query is probably not valid. " + ex
						.getMessage(),
						ex);
			} catch (IOException ex) {
				logger.error("Stream were not closed. " + ex.getMessage(),
						ex);
			}

		} catch (MalformedQueryException ex) {
			throw new InvalidQueryException(
					"This query is probably not valid. "
					+ ex.getMessage(), ex);
		} catch (RepositoryException ex) {
			hasBrokenConnection = true;
			logger.error("Connection to RDF repository failed. "
					+ ex.getMessage(), ex);
		} catch (RDFHandlerException ex) {
			logger.error("RDF handler failed. " + ex.getMessage(), ex);
		} /*finally {
		 if (connection != null) {
		 try {
		 connection.close();
		 } catch (RepositoryException ex) {
		 logger.warn(
		 "Failed to close connection to RDF repository while querying. "
		 + ex.getMessage(), ex);
		 }
		 }
		 }*/

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
			Iterator<Statement> it = graphInstance.iterator();

			while (it.hasNext()) {
				Statement statement = it.next();
				addStatement(statement, graph);
			}
		}
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

		RepositoryConnection connection = null;

		try {
			connection = getConnection();

			GraphQuery graphQuery = connection.prepareGraphQuery(
					QueryLanguage.SPARQL,
					constructQuery);

			graphQuery.setDataset(getDataSetForGraph());

			logger.debug("Query " + constructQuery + " is valid.");

			try {

				GraphQueryResult result = graphQuery.evaluate();

				logger.debug(
						"Query " + constructQuery + " has not null result.");
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
			hasBrokenConnection = true;
			logger.error("Connection to RDF repository failed. "
					+ ex.getMessage(), ex);
		} /*finally {
		 if (connection != null) {
		 try {
		 connection.close();
		 } catch (RepositoryException ex) {
		 logger.warn(
		 "Failed to close connection to RDF repository while querying. "
		 + ex.getMessage(), ex);
		 }
		 }
		 }*/

		throw new InvalidQueryException(
				"Getting GraphQueryResult using SPARQL construct query failed.");
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

		RepositoryConnection connection = null;

		try {
			connection = getConnection();

			TupleQuery tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, selectQuery);

			tupleQuery.setDataset(getDataSetForGraph());

			logger.debug("Query " + selectQuery + " is valid.");

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
			logger.error("Writing result to file fail. " + ex.getMessage(),
					ex);

		} catch (RepositoryException ex) {
			hasBrokenConnection = true;
			logger.error("Connection to RDF repository failed. "
					+ ex.getMessage(), ex);
		} catch (IOException ex) {
			logger.error("Stream were not closed. " + ex.getMessage(), ex);
		} finally {
			/*if (connection != null) {
			 try {
			 connection.close();
			 } catch (RepositoryException ex) {
			 logger.warn(
			 "Failed to close connection to RDF repository while querying."
			 + ex.getMessage(), ex);
			 }
			 }*/
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

		Map<String, List<String>> map = new HashMap<>();

		List<BindingSet> listBindings = new ArrayList<>();
		MyTupleQueryResult result = null;
		try {
			result = executeSelectQueryAsTuples(selectQuery);

			List<String> names = result.getBindingNames();

			for (String name : names) {
				map.put(name, new LinkedList<String>());
			}

			listBindings = result.asList();
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
					logger.warn("Failed to close RDF tuple result. "
							+ ex.getMessage(), ex);
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

	private long getSizeForConstruct(String constructQuery) throws InvalidQueryException {
		long size = 0;

		RepositoryConnection connection = null;

		try {
			connection = getConnection();

			GraphQuery graphQuery = connection.prepareGraphQuery(
					QueryLanguage.SPARQL,
					constructQuery);

			graphQuery.setDataset(getDataSetForGraph());
			try {
				GraphQueryResult result = graphQuery.evaluate();

				while (result.hasNext()) {
					result.next();
					size++;
				}

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
			hasBrokenConnection = true;
			logger.error("Connection to RDF repository failed. "
					+ ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn(
							"Failed to close connection to RDF repository while querying. "
							+ ex.getMessage(), ex);
				}
			}
		}

		return size;
	}

	private long getSizeForSelect(QueryPart queryPart) throws InvalidQueryException {

		final String sizeVar = "selectSize";
		final String sizeQuery = String.format(
				"%s select count(*) as ?%s where {%s}", queryPart
				.getQueryPrefixes(),
				sizeVar, queryPart.getQueryWithoutPrefixes());
		try {
			RepositoryConnection connection = getConnection();

			TupleQuery tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, sizeQuery);

			tupleQuery.setDataset(getDataSetForGraph());
			try {
				TupleQueryResult tupleResult = tupleQuery.evaluate();
				if (tupleResult.hasNext()) {
					String selectSize = tupleResult.next()
							.getValue(sizeVar).stringValue();
					long resultSize = Long.parseLong(selectSize);
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
			hasBrokenConnection = true;
			logger.error("Connection to RDF repository failed. "
					+ ex.getMessage(), ex);
		}

		return 0;
	}

	/**
	 * For given valid SELECT of CONSTRUCT query return its size {count of rows
	 * returns for given query).
	 *
	 * @param query Valid SELECT/CONTRUCT query for asking.
	 * @return
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
				size = getSizeForConstruct(query);
				break;
			case UNKNOWN:
				throw new InvalidQueryException(
						"Given query: " + query + "have to be SELECT or CONSTRUCT type.");
		}

		return size;


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
	public MyTupleQueryResult executeSelectQueryAsTuples(
			String selectQuery)
			throws InvalidQueryException {

		try {
			RepositoryConnection connection = getConnection();

			TupleQuery tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, selectQuery);

			tupleQuery.setDataset(getDataSetForGraph());

			logger.debug("Query " + selectQuery + " is valid.");

			try {
				TupleQueryResult tupleResult = tupleQuery.evaluate();
				logger.debug(
						"Query " + selectQuery + " has not null result.");

				MyTupleQueryResult result = new MyTupleQueryResult(
						connection,
						tupleResult);
				return result;

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
			hasBrokenConnection = true;
			logger.error("Connection to RDF repository failed. "
					+ ex.getMessage(), ex);
		}
		throw new InvalidQueryException(
				"Getting TupleQueryResult using SPARQL select query failed.");
	}

	@Override
	public void merge(DataUnit unit) throws IllegalArgumentException {

		if (unit != null) {
			if (unit instanceof RDFDataUnit) {
				RDFDataUnit rdfRepository = (RDFDataUnit) unit;
				mergeRepositoryData(rdfRepository);

			} else {
				throw new IllegalArgumentException(
						"DataUnit is not instance of RDFDataRepository.");
			}
		}
	}

	private Dataset getDataSetForGraph() {
		DatasetImpl dataSet = new DatasetImpl();
		dataSet.addDefaultGraph(graph);

		return dataSet;
	}

	private String AddGraphToUpdateQuery(String updateQuery) {

		if (repository instanceof SailRepository) {
			return updateQuery;
		}

		String regex = "(insert|delete)\\s\\{";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(updateQuery.toLowerCase());

		boolean hasResult = matcher.find();

		if (hasResult) {

			int index = matcher.start();

			String first = updateQuery.substring(0, index);
			String second = updateQuery.substring(index, updateQuery.length());

			String graphName = " WITH <" + graph.stringValue() + "> ";

			String newQuery = first + graphName + second;
			return newQuery;


		} else {

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
						"in graph <" + graph.stringValue() + "> {");

				String newQuery = first + graphName + second;

				return newQuery;

			}
		}
		return updateQuery;


	}

	long getSPARQLEnpointGraphSize(URL endpointURL, String endpointGraph) throws RDFException {
		String countQuery = "select count(*) as ?count where {?x ?y ?z}";

		InputStreamReader inputStreamReader = getEndpointStreamReader(
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

	/**
	 * Removes all RDF data in defined graph using connecion to SPARQL endpoint
	 * address. For data deleting is necessarry to have endpoint with update
	 * rights.
	 *
	 * @param endpointURL   URL address of endpoint connect to.
	 * @param endpointGraph Graph name in URI format.
	 * @throws RDFException When you dont have update right for this action, or
	 *                      connection is lost before succesfully ending.
	 */
	@Override
	public void clearEndpointGraph(URL endpointURL, String endpointGraph)
			throws RDFException {

		String deleteQuery = "delete {?x ?y ?z} where {?x ?y ?z}";
		InputStreamReader inputStreamReader = getEndpointStreamReader(
				endpointURL,
				endpointGraph, deleteQuery, RDFFormat.RDFXML);

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
			InputStreamReader result = getEndpointStreamReader(endpointURL,
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

	private InputStreamReader getEndpointStreamReader(URL endpointURL,
			String endpointGraphURI, String query,
			RDFFormat format) throws RDFException {

		final String endpointGraph = getEncodedString(endpointGraphURI);
		final String myquery = getEncodedString(query);

		final String encoder = getEncoder(format);

		String parameters = "default-graph-uri=" + endpointGraph + "&query=" + myquery + "&format=" + encoder;

		URL call = null;
		try {
			call = new URL(endpointURL.toString());
		} catch (MalformedURLException e) {
			final String message = "Malfolmed URL exception by construct extract URL. ";
			logger.debug(message);
			throw new RDFException(message + e.getMessage(), e);
		}

		HttpURLConnection httpConnection = null;
		try {
			httpConnection = getHttpURLConnection(call);
			httpConnection.setRequestMethod("POST");
			httpConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			httpConnection.setRequestProperty("Content-Length", ""
					+ Integer.toString(parameters.getBytes().length));

			httpConnection.setUseCaches(false);
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);

			try (OutputStream os = httpConnection.getOutputStream()) {
				os.write(parameters.getBytes());
				os.flush();
			}

			int httpResponseCode = httpConnection.getResponseCode();

			if (httpResponseCode != HTTP_OK_RESPONSE) {

				StringBuilder message = new StringBuilder(
						httpConnection.getHeaderField(0));


				if (httpResponseCode == HTTP_UNAUTORIZED_RESPONSE) {
					message.append(
							". Your USERNAME and PASSWORD for connection is wrong.");
				} else if (httpResponseCode == HTTP_BAD_RESPONSE) {

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

							throw new InsertPartException(message.toString());
						}
					}


				} else {
					message.append(
							". You probably dont have enought PERMISSION for this action.");
				}

				throw new RDFException(message.toString());
			}

		} catch (UnknownHostException e) {
			final String message = "Unknown host: ";
			throw new RDFException(message + e.getMessage(), e);
		} catch (IOException e) {
			final String message = "Endpoint URL stream can not open. ";
			logger.debug(message);
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			throw new RDFException(message + e.getMessage(), e);
		}

		try {
			InputStreamReader inputStreamReader = new InputStreamReader(
					httpConnection.getInputStream(), Charset.forName(
					encode));

			return inputStreamReader;

		} catch (IOException e) {

			final String message = "Http connection can can not open stream. ";
			logger.debug(message);

			throw new RDFException(message + e.getMessage(), e);

		}
	}

	private HttpURLConnection getHttpURLConnection(URL call) throws IOException {

		int retryCount = 0;

		HttpURLConnection httpConnection;

		while (true) {
			try {
				httpConnection = (HttpURLConnection) call.openConnection();
				return httpConnection;
			} catch (IOException e) {
				retryCount++;

				final String message = String.format(
						"%s/%s attempt to reconnect %s FAILED", retryCount,
						RETRY_CONNECTION_SIZE, call.toString());

				logger.debug(message);

				if (retryCount >= RETRY_CONNECTION_SIZE) {
					throw new IOException(e.getMessage(), e);
				} else {
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

	private String getEncoder(RDFFormat format) throws RDFException {
		String encoder = getEncodedString(format.getDefaultMIMEType());
		return encoder;
	}

	private void authenticate(String hostName, String password) {

		boolean usePassword = !(hostName.isEmpty() && password.isEmpty());

		if (usePassword) {

			final String myName = hostName;
			final String myPassword = password;

			Authenticator autentisator = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(myName, myPassword
							.toCharArray());
				}
			};

			Authenticator.setDefault(autentisator);

		}
	}

	private void writeDataIntoFile(File dataFile, RDFFormatType formatType)
			throws RDFException {

		RepositoryConnection connection = null;

		try (OutputStreamWriter os = new OutputStreamWriter(
				new FileOutputStream(
				dataFile.getAbsoluteFile()), Charset
				.forName(encode))) {

			if (formatType == RDFFormatType.AUTO) {
				String fileName = dataFile.getName();
				RDFFormat newFormat = RDFFormat.forFileName(fileName,
						RDFFormat.RDFXML);
				formatType = RDFFormatType.getTypeByRDFFormat(newFormat);
			}
			MyRDFHandler handler = new MyRDFHandler(os, formatType);

			connection = getConnection();

			if (graph != null) {
				connection.export(handler, graph);
			} else {
				connection.export(handler);
			}

			connection.commit();

		} catch (IOException ex) {
			throw new RDFException("Problems with file stream:" + ex
					.getMessage(), ex);
		} catch (RDFHandlerException ex) {
			throw new RDFException(ex.getMessage(), ex);
		} catch (RepositoryException ex) {
			hasBrokenConnection = true;
			throw new RDFException(
					"Repository connection failed while trying to load into XML file."
					+ ex.getMessage(), ex);
		} /*finally {
		 if (connection != null) {
		 try {
		 connection.close();
		 } catch (RepositoryException ex) {
		 logger.warn(
		 "Failed to close connection to RDF repository while trying to load into XML file."
		 + ex.getMessage(), ex);
		 }
		 }
		 }*/
	}

	private void extractDataFileFromHTTPSource(String path, String baseURI,
			boolean useStatisticHandler) throws RDFException {
		URL urlPath;
		try {
			urlPath = new URL(path);
		} catch (MalformedURLException ex) {
			throw new RDFException(ex.getMessage(), ex);
		}

		try (InputStreamReader inputStreamReader = new InputStreamReader(
				urlPath.openStream(), Charset.forName(encode))) {

			RDFFormat format = RDFFormat.forFileName(path, RDFFormat.RDFXML);
			RepositoryConnection connection = getConnection();

			if (!useStatisticHandler) {

				addInputStreamToRepository(connection, inputStreamReader,
						baseURI, format, graph);
			} else {
				StatisticalHandler handler = parseFileUsingStatisticalHandler(
						format, inputStreamReader, baseURI);

				if (graph != null) {
					connection.add(handler.getStatements(), graph);
				} else {
					connection.add(handler.getStatements());
				}

				inputStreamReader.close();

			}


		} catch (IOException | RDFParseException ex) {
			throw new RDFException(ex.getMessage(), ex);
		} catch (RepositoryException ex) {
			hasBrokenConnection = true;
			throw new RDFException(ex.getMessage(), ex);
		}
	}

	private void addFilesInDirectoryToRepository(RDFFormat format, File[] files,
			String baseURI,
			boolean useStatisticHandler, boolean skipFiles, Resource... graphs)
			throws RDFException {

		if (files == null) {
			return; // nothing to add
		}

		for (int i = 0; i < files.length; i++) {
			File nextFile = files[i];

			try {
				addFileToRepository(format, nextFile, baseURI,
						useStatisticHandler,
						graphs);
			} catch (RDFException e) {

				if (skipFiles) {
					final String message = String.format(
							"RDF data from file <%s> was skiped", nextFile
							.getAbsolutePath());
					logger.error(message);

				} else {
					throw new RDFException(e.getMessage(), e);
				}

			}
		}
	}

	private File[] getFilesBySuffix(File dirFile, String suffix,
			boolean useAceptedSuffix) {

		if (useAceptedSuffix) {
			final String aceptedSuffix = suffix.toUpperCase();

			FilenameFilter acceptedFileFilter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.toUpperCase().endsWith(aceptedSuffix)) {
						return true;
					} else {
						return false;
					}
				}
			};

			return dirFile.listFiles(acceptedFileFilter);

		} else {
			return dirFile.listFiles();
		}

	}

	private void addInputStreamToRepository(RepositoryConnection connection,
			InputStreamReader inputStreamReader, String baseURI,
			RDFFormat format,
			Resource... graphs) throws IOException, RDFParseException, RepositoryException {

		if (graphs != null) {
			connection.add(inputStreamReader, baseURI, format, graphs);
		} else {
			connection.add(inputStreamReader, baseURI, format);
		}

	}

	private void addFileToRepository(RDFFormat fileFormat, File dataFile,
			String baseURI,
			boolean useStatisticHandler, Resource... graphs) throws RDFException {

		//in case that RDF format is AUTO or not fixed.
		if (fileFormat == null) {
			fileFormat = RDFFormat.forFileName(dataFile.getAbsolutePath(),
					RDFFormat.RDFXML);
		}

		RepositoryConnection connection = null;


		try (InputStreamReader is = new InputStreamReader(new FileInputStream(
				dataFile), Charset.forName(encode))) {

			connection = getConnection();

			if (!useStatisticHandler) {

				addInputStreamToRepository(connection, is, baseURI, fileFormat,
						graphs);

			} else {

				StatisticalHandler handler = parseFileUsingStatisticalHandler(
						fileFormat, is, baseURI);

				if (graphs != null) {
					connection.add(handler.getStatements(), graphs);
				} else {
					connection.add(handler.getStatements());
				}

			}

			connection.commit();

		} catch (IOException ex) {
			logger.debug(ex.getMessage(), ex);
			throw new RDFException("IO Exception: " + ex.getMessage(), ex);
		} catch (RDFParseException ex) {
			logger.debug(ex.getMessage(), ex);
			throw new RDFException("Problem with parsing triples: " + ex
					.getMessage(), ex);
		} catch (RepositoryException ex) {
			hasBrokenConnection = true;
			logger.debug(ex.getMessage(), ex);
			throw new RDFException(
					"Error by adding file to repository " + ex.getMessage(), ex);
		} /*finally {
		 if (connection != null) {
		 try {
		 connection.close();
		 } catch (RepositoryException ex) {
		 logger.warn("Failed to close connection to RDF repository.",
		 ex);
		 }
		 }
		 }*/
	}

	private StatisticalHandler parseFileUsingStatisticalHandler(
			RDFFormat fileFormat,
			InputStreamReader is, String baseURI) throws RDFException {

		StatisticalHandler handler = new StatisticalHandler();

		RDFParser parser = Rio.createParser(fileFormat);
		parser.setRDFHandler(handler);

		parser.setStopAtFirstError(false);
		parser.setParseErrorListener(new ParseErrorListener() {
			@Override
			public void warning(String msg, int lineNo, int colNo) {
				logger.warn(msg + "line:" + lineNo + "column:" + colNo);
			}

			@Override
			public void error(String msg, int lineNo, int colNo) {
				logger.error(msg + "line:" + lineNo + "column:" + colNo);
			}

			@Override
			public void fatalError(String msg, int lineNo, int colNo) {
				logger.error(msg + "line:" + lineNo + "column:" + colNo);
			}
		});
		try {
			parser.parse(is, baseURI);
		} catch (IOException | RDFParseException | RDFHandlerException ex) {
			throw new RDFException(ex.getMessage(), ex);
		} finally {
			return handler;
		}
	}

	protected void addRDFStringToRepository(String rdfString, RDFFormat format,
			Resource... graphs) throws RDFException {
		RepositoryConnection connection = null;

		try {

			connection = getConnection();
			StringReader reader = new StringReader(rdfString);

			if (graphs != null) {

				connection.add(reader, "", format, graphs);
			} else {
				connection.add(reader, "", format);
			}

			connection.commit();

		} catch (RepositoryException e) {
			hasBrokenConnection = true;
			logger.debug(e.getMessage());

		} catch (IOException | RDFParseException ex) {
			throw new RDFException(ex.getMessage(), ex);
		} /*finally {
		 if (connection != null) {
		 try {
		 connection.close();
		 } catch (RepositoryException ex) {
		 throw new RuntimeException(ex);
		 }
		 }
		 }*/
	}

	protected void addStatement(Statement statement, Resource... graphs) {

		RepositoryConnection connection = null;

		try {

			connection = getConnection();
			if (graphs != null) {

				connection.add(statement, graphs);
			} else {
				connection.add(statement);
			}

			connection.commit();

		} catch (RepositoryException e) {
			hasBrokenConnection = true;
			logger.debug(e.getMessage());


		} /*finally {
		 if (connection != null) {
		 try {
		 connection.close();
		 } catch (RepositoryException ex) {
		 throw new RuntimeException(ex);
		 }
		 }
		 }*/
	}

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

	protected URI createNewGraph(String graphURI) {
		if (graphURI.toLowerCase().startsWith("http://")) {

			URI newGraph = new URIImpl(graphURI);
			return newGraph;

		} else {

			String newGraphUri = "http://" + graphURI;
			return createNewGraph(newGraphUri);

		}
	}

	/**
	 * Definitely destroy repository - use after all working in repository.
	 * Another repository using cause exception. For other using you have to
	 * create new instance.
	 */
	@Override
	public void shutDown() {

		Thread destroyThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					repository.shutDown();
					logger.debug("Repository with data graph <" + getDataGraph()
							.stringValue() + "> destroyed SUCCESSFULL.");
				} catch (RepositoryException ex) {
					hasBrokenConnection = true;
					logger.debug(
							"Repository was not destroyed - potencial problems with locks .");
					logger.debug(ex.getMessage());
				}
			}
		});

		destroyThread.setDaemon(true);
		destroyThread.start();
	}

	public List<RDFTriple> getRDFTriplesInRepository() {

		List<RDFTriple> triples = new ArrayList<>();
		List<Statement> statements = getTriples();

		int count = 0;

		for (Statement next : statements) {
			String subject = next.getSubject().stringValue();
			String predicate = next.getPredicate().stringValue();
			String object = next.getObject().stringValue();

			count++;

			RDFTriple triple = new RDFTriple(count, subject, predicate, object);
			triples.add(triple);
		}

		return triples;
	}

	@Override
	public void close() throws IOException {
		shutDown();
	}

	@Override
	public String getDataUnitName() {
		return dataUnitName;
	}

	/**
	 * Return openRDF repository needed for almost every operation using RDF.
	 *
	 * @return openRDF repository.
	 */
	@Override
	public Repository getDataRepository() {
		return repository;
	}

	/**
	 * Return URI representation of graph where RDF data are stored.
	 *
	 * @return graph with stored data as URI.
	 */
	@Override
	public URI getDataGraph() {
		return graph;
	}

	/**
	 * Set data graph storage for given data in RDF format.
	 *
	 * @param newDataGraph new graph represented as URI.
	 */
	@Override
	public void setDataGraph(URI newDataGraph) {
		graph = newDataGraph;
		if (!isGraphDefault()) {
			logger.info("Set new data graph - " + graph.stringValue());
		}
	}

	private boolean isGraphDefault() {
		if (graph != null) {
			return graph.stringValue().equals(DEFAULT_GRAPH_NAME);
		} else {
			return false;
		}
	}

	@Override
	public boolean isReadOnly() {
		return isReadOnly;
	}

	@Override
	public void madeReadOnly() {
		setReadOnly(true);
	}

	protected void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	/**
	 * Add one RDF triple (statement) to the repository.
	 *
	 * @param subject   One of type for subject - URI,BlankNode.
	 * @param predicate URI for subject.
	 * @param object    One of type for object - URI,BlankNode or Literal.
	 */
	@Override
	public void addTriple(Resource subject, URI predicate, Value object) {
		Statement statement = new StatementImpl(subject, predicate, object);
		addStatement(statement, graph);

	}

	/**
	 * Create new blank node with defined id.
	 *
	 * @param id String value of ID.
	 * @return created blank node.
	 */
	@Override
	public BNode createBlankNode(String id) {
		ValueFactory factory = repository.getValueFactory();
		return factory.createBNode(id);
	}

	/**
	 * Create new URI from String.
	 *
	 * @param uri String value for URI.
	 * @return created URI.
	 */
	@Override
	public URI createURI(String uri) {
		ValueFactory factory = repository.getValueFactory();
		return factory.createURI(uri);

	}

	/**
	 * Create new typed literal.
	 *
	 * @param literalLabel String value for literal.
	 * @param dataType     URI of data type for {@literal}.
	 * @return created typed literal.
	 */
	@Override
	public Literal createLiteral(String literalLabel, URI dataType) {
		ValueFactory factory = repository.getValueFactory();
		return factory.createLiteral(literalLabel, dataType);
	}

	/**
	 * Create new language literal.
	 *
	 * @param literalLabel String value for literal.
	 * @param language     String value for language.
	 * @return created language literal.
	 */
	@Override
	public Literal createLiteral(String literalLabel, String language) {
		ValueFactory factory = repository.getValueFactory();
		return factory.createLiteral(literalLabel, language);
	}

	/**
	 * Create new label literal.
	 *
	 * @param literalLabel String value for literal.
	 * @return created language literal.
	 */
	@Override
	public Literal createLiteral(String literalLabel) {
		ValueFactory factory = repository.getValueFactory();
		return factory.createLiteral(literalLabel);
	}

	@Override
	public void clean() {
		// to clean documentaion in MergableDataUnit
		cleanAllData();
	}

	protected RepositoryConnection getConnection() throws RepositoryException {

		if (!hasBrokenConnection) {
			if (repoConnection != null && repoConnection.isOpen()) {
				return repoConnection;
			} else {
				repoConnection = repository.getConnection();
				return repoConnection;
			}
		} else {
			repoConnection = repository.getConnection();
			hasBrokenConnection = false;
			return repoConnection;
		}

	}

	protected void closeConnection() throws RepositoryException {
		if (!hasBrokenConnection && repoConnection != null
				&& repoConnection.isOpen()) {

			repoConnection.close();
		}
	}

	/**
	 * Stop execution of data unit. And clean obtained data, if is possible. If
	 * is called during execution atomic methods (as extraction from file), data
	 * unit will be stoped after execution this called atomic method.
	 */
	@Override
	public void interruptExecution() {
		setExecutionCanceled(true);
	}

	private void setExecutionCanceled(boolean isCanceled) {
		isExecutionCanceled = isCanceled;
	}
}
