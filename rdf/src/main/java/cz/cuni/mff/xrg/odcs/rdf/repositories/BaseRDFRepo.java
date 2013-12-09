package cz.cuni.mff.xrg.odcs.rdf.repositories;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.rdf.GraphUrl;
import cz.cuni.mff.xrg.odcs.rdf.enums.*;

import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InsertPartException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.help.ParamController;
import cz.cuni.mff.xrg.odcs.rdf.impl.MyGraphQueryResult;
import cz.cuni.mff.xrg.odcs.rdf.handlers.MyRDFHandler;
import cz.cuni.mff.xrg.odcs.rdf.impl.MyTupleQueryResult;
import cz.cuni.mff.xrg.odcs.rdf.query.utils.QueryPart;
import cz.cuni.mff.xrg.odcs.rdf.help.RDFTriple;
import cz.cuni.mff.xrg.odcs.rdf.handlers.StatisticalHandler;
import cz.cuni.mff.xrg.odcs.rdf.handlers.TripleCountHandler;
import cz.cuni.mff.xrg.odcs.rdf.help.LazyTriples;
import cz.cuni.mff.xrg.odcs.rdf.help.UniqueNameGenerator;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
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
import org.openrdf.rio.helpers.BasicParserSettings;
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
	protected static int RETRY_CONNECTION_SIZE = -1;

	/**
	 * Time in miliseconds how long to wait before trying to reconnect.
	 */
	protected static long RETRY_CONNECTION_TIME = 1000;

	/**
	 * Represents successful connection using HTTP.
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
	 * Maximum query execution time in miliseconds
	 */
	protected static final String TIME_OUT = "10000000";

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
	protected RepositoryConnection repoConnection;

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

	/**
	 *
	 * Set time in miliseconds how long to wait before trying to reconnect.
	 *
	 * @param retryTimeValue time in milisecond for waiting before trying to
	 *                       reconnect.
	 * @throws IllegalArgumentException if time is 0 or negative long number.
	 */
	@Override
	public void setRetryConnectionTime(long retryTimeValue) throws IllegalArgumentException {
		if (retryTimeValue > 0) {
			RETRY_CONNECTION_TIME = retryTimeValue;
		} else {
			throw new IllegalArgumentException(
					"Retry connection time must be positive number");
		}
	}

	/**
	 * Set Count of attempts to reconnect if the connection fails. For infinite
	 * loop use zero or negative integer
	 *
	 * @param retrySizeValue as interger with count of attemts to reconnect.
	 */
	@Override
	public void setRetryConnectionSize(int retrySizeValue) {
		RETRY_CONNECTION_SIZE = retrySizeValue;
	}

	@Override
	public void addFromFile(File file) throws RDFException {
		extractFromFile(file, RDFFormat.RDFXML, "",
				HandlerExtractType.STANDARD_HANDLER);
	}

	@Override
	public void addFromFile(File file, RDFFormat format) throws RDFException {
		extractFromFile(file, format, "", HandlerExtractType.STANDARD_HANDLER);
	}

	@Override
	public void addFromFile(File file, RDFFormat format,
			HandlerExtractType handlerExtractType) throws RDFException {

		extractFromFile(file, format, "", handlerExtractType);
	}

	@Override
	public void extractFromFile(File file, RDFFormat format, String baseURI)
			throws RDFException {

		extractFromFile(file, format, baseURI,
				HandlerExtractType.STANDARD_HANDLER);
	}

	@Override
	public void extractFromFile(File file, RDFFormat format, String baseURI,
			HandlerExtractType handlerExtractType) throws RDFException {

		if (file == null) {
			throw new RDFException("Given file for extraction is null");
		}

		extractFromFile(FileExtractType.PATH_TO_FILE, format, file
				.getAbsolutePath(), "", baseURI, false, handlerExtractType);
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
		extractFromFile(file, RDFFormat.TURTLE, "",
				HandlerExtractType.STANDARD_HANDLER);
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
		extractFromFile(file, RDFFormat.RDFXML, "",
				HandlerExtractType.STANDARD_HANDLER);
	}

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param extractType        One of defined enum type for extraction data
	 *                           from file.
	 * @param format             One of RDFFormat value for parsing triples, if
	 *                           value is null RDFFormat is selected by
	 *                           filename.
	 * @param path               String path to file/directory
	 * @param suffix             String suffix of fileName (example: ".ttl",
	 *                           ".xml", etc)
	 * @param baseURI            String name of defined used URI
	 * @param useSuffix          boolean value, if extract files only with
	 *                           defined suffix or not.
	 * @param handlerExtractType Possibilies how to choose handler for data
	 *                           extraction and how to solve finded problems
	 *                           with no valid data.
	 * @throws RDFException when extraction fail.
	 */
	@Override
	public void extractFromFile(FileExtractType extractType,
			RDFFormat format,
			String path, String suffix,
			String baseURI, boolean useSuffix,
			HandlerExtractType handlerExtractType)
			throws RDFException {

		ParamController.testNullParameter(path,
				"Mandatory target path in extractor is null.");
		ParamController.testEmptyParameter(path,
				"Mandatory target path in extractor have to be not empty.");

		File dirFile = new File(path);

		switch (extractType) {
			case HTTP_URL:
				extractDataFileFromHTTPSource(path, baseURI, handlerExtractType);
				break;
			case PATH_TO_DIRECTORY:
				extractDataFromDirectorySource(dirFile, suffix, useSuffix,
						format, baseURI, handlerExtractType, false);
				break;

			case PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES:
				extractDataFromDirectorySource(dirFile, suffix, useSuffix,
						format, baseURI, handlerExtractType, true);
				break;
			case PATH_TO_FILE:
			case UPLOAD_FILE:
				extractDataFromFileSource(dirFile, format, baseURI,
						handlerExtractType);
				break;
		}

	}

	private void extractDataFromFileSource(File dirFile, RDFFormat format,
			String baseURI, HandlerExtractType handlerExtractType) throws RDFException {

		if (dirFile.isFile()) {
			addFileToRepository(format, dirFile, baseURI,
					handlerExtractType,
					graph);
		} else {
			throw new RDFException(
					"Path to file \"" + dirFile.getAbsolutePath() + "\"doesnt exist");
		}
	}

	private void extractDataFromDirectorySource(File dirFile, String suffix,
			boolean useSuffix, RDFFormat format, String baseURI,
			HandlerExtractType handlerExtractType, boolean skipFiles)
			throws RDFException {

		if (dirFile.isDirectory()) {
			File[] files = getFilesBySuffix(dirFile, suffix, useSuffix);

			addFilesInDirectoryToRepository(format, files, baseURI,
					handlerExtractType, skipFiles,
					graph);
		} else {
			throw new RDFException(
					"Path to directory \"" + dirFile.getAbsolutePath()
					+ "\" doesnt exist");
		}
	}

	@Override
	public void loadToFile(File file, RDFFormatType formatType) throws RDFException {

		ParamController.testNullParameter(file,
				"Given file for loading is null.");

		ParamController.testEmptyParameter(file, "File name is empty");

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

		ParamController.testNullParameter(filePath,
				"Mandatory file path in File_loader is null.");
		ParamController.testEmptyParameter(filePath,
				"Mandatory file path in File_loader is empty.");

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

	@Override
	public long getPartsCount(long chunkSize) {

		long triples = getTripleCount();
		long partsCount = triples / chunkSize;

		if (partsCount * chunkSize != triples) {
			partsCount++;
		}

		return partsCount;
	}

	@Override
	public RepositoryResult<Statement> getRepositoryResult() {

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

	/**
	 * Create RDF parser for given RDF format and set RDF handler where are data
	 * insert to.
	 *
	 * @param format  RDF format witch is set to RDF parser
	 * @param handler Type of handler where RDF parser used for parsing. If
	 *                handler is {@link StatisticalHandler} type, is set error
	 *                listener for fix errors here.
	 * @return RDFParser for given RDF format and handler.
	 */
	@Override
	public RDFParser getRDFParser(RDFFormat format, TripleCountHandler handler) {
		RDFParser parser = Rio.createParser(format);
		parser.setRDFHandler(handler);

		ParserConfig config = parser.getParserConfig();

		config.addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);

		parser.setParserConfig(config);

		if (handler instanceof StatisticalHandler) {
			setErrorsListenerToParser(parser, (StatisticalHandler) handler);
		}

		return parser;
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
			MyTupleQueryResult tupleResult = executeSelectQueryAsTuples(select);

			String prefix = GraphUrl.getGraphPrefix();

			for (BindingSet set : tupleResult.asList()) {

				for (String name : set.getBindingNames()) {
					String graphName = set.getValue(name).stringValue();

					if (graphName.startsWith(prefix)) {
						result.add(graphName);
					}
				}
			}
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
			logger.info("Graph " + graphName + " was sucessfully deleted");
		} catch (RDFException e) {
			logger.debug(e.getMessage());
		}

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

			//connection.commit();

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
				RepositoryResult<Statement> lazy = getRepositoryResult();

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

	/**
	 *
	 * @return instance with iterator behavior for lazy returning all triples in
	 *         repository, which are split to parts using default split value
	 *         (see {@link LazyTriples#DEFAULT_SPLIT_SIZE}).
	 */
	@Override
	public LazyTriples getTriplesIterator() {

		LazyTriples result = new LazyTriples(getRepositoryResult());

		return result;
	}

	/**
	 *
	 * @param splitSize number of triples returns in each return part using
	 *                  method {@link LazyTriples#getTriples() }.
	 * @return instance with iterator behavior for lazy returning all triples in
	 *         repository, which are split to parts - each has triples at most
	 *         as defined splitSize.
	 */
	@Override
	public LazyTriples getTriplesIterator(long splitSize) {

		LazyTriples result = new LazyTriples(getRepositoryResult(), splitSize);

		return result;
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

		RepositoryConnection connection = null;

		try {
			connection = getConnection();

			GraphQuery graphQuery = connection.prepareGraphQuery(
					QueryLanguage.SPARQL,
					constructQuery);

			graphQuery.setDataset(getDataSet());

			logger.debug("Query " + constructQuery + " is valid.");

			try {

				File file = new File(filePath);
				MyRDFHandler goal = getHandlerForConstructQuery(file, formatType);

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
				logger.error("Problems with file stream : " + ex.getMessage(),
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

		RepositoryConnection connection = null;

		try {
			connection = getConnection();

			GraphQuery graphQuery = connection.prepareGraphQuery(
					QueryLanguage.SPARQL,
					constructQuery);

			graphQuery.setDataset(dataSet);

			logger.debug("Query " + constructQuery + " is valid.");

			try {

				MyGraphQueryResult result = new MyGraphQueryResult(graphQuery
						.evaluate());

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
	 * Make construct query over repository data and return interface Graph as
	 * result contains iterator for statements (triples).
	 *
	 * @param constructQuery String representation of SPARQL query.
	 * @return Interface Graph as result of construct SPARQL query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	@Override
	public Graph executeConstructQuery(String constructQuery) throws InvalidQueryException {
		return executeConstructQuery(constructQuery, getDataSet());
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

			tupleQuery.setDataset(getDataSet());

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

		Map<String, List<String>> map = new LinkedHashMap<>();

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

			graphQuery.setDataset(getDataSet());
			try {
				GraphQueryResult result = graphQuery.evaluate();

				Model model = QueryResults.asModel(result);
				size = model.size();

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
				"%s SELECT (count(*) AS ?%s) WHERE {%s}", queryPart
				.getQueryPrefixes(),
				sizeVar, queryPart.getQueryWithoutPrefixes());
		try {
			RepositoryConnection connection = getConnection();

			TupleQuery tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, sizeQuery);

			tupleQuery.setDataset(getDataSet());
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

			tupleQuery.setDataset(getDataSet());

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

	/**
	 *
	 * @return dataset for graphs set in reposiotory as default.
	 */
	@Override
	public Dataset getDataSet() {
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
		boolean hasWith = updateQuery.toLowerCase().contains("with");

		if (hasResult && !hasWith) {

			int index = matcher.start();

			String first = updateQuery.substring(0, index);
			String second = updateQuery.substring(index, updateQuery.length());

			String graphName = " WITH <" + graph.stringValue() + "> ";

			String newQuery = first + graphName + second;
			return newQuery;


		} else {

			logger.debug("WITH graph clause was not added, "
					+ "because the query was: " + updateQuery);

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
						"{ GRAPH <" + graph.stringValue() + "> {");

				second = second.replaceFirst("}", "} }");
				String newQuery = first + graphName + second;

				return newQuery;

			}
		}
		return updateQuery;


	}

	/**
	 * Removes all RDF data in defined graph using connecion to SPARQL endpoint
	 * address. For data deleting is necessarry to have endpoint with update
	 * rights.
	 *
	 * @param endpointURL   URL address of update endpoint connect to.
	 * @param endpointGraph Graph name in URI format.
	 * @throws RDFException When you dont have update right for this action, or
	 *                      connection is lost before succesfully ending.
	 */
	@Override
	public void clearEndpointGraph(URL endpointURL, String endpointGraph)
			throws RDFException {

		String deleteQuery = String.format("CLEAR GRAPH <%s>", endpointGraph);

		InputStreamReader inputStreamReader = getEndpointStreamReader(
				endpointURL, "", deleteQuery, RDFFormat.RDFXML);

	}

	@Override
	public InputStreamReader getEndpointStreamReader(URL endpointURL,
			String endpointGraphURI, String query,
			RDFFormat format) throws RDFException {

		final String endpointGraph = getEncodedString(endpointGraphURI);
		final String myquery = getEncodedString(query);

		final String encoder = getEncoder(format);
		final String timeOut = getEncodedString(TIME_OUT);

		String parameters = "default-graph-uri=" + endpointGraph
				+ "&query=" + myquery
				+ "&format=" + encoder
				+ "&timeout=" + timeOut;

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
						message.append(
								". Inserted data has wrong format.");
//
//					try (InputStream errorStream = httpConnection
//							.getErrorStream()) {
//
//						try (BufferedReader reader = new BufferedReader(
//								new InputStreamReader(
//								errorStream, Charset.forName(encode)))) {
//
//							StringBuilder inputStringBuilder = new StringBuilder();
//							String line = reader.readLine();
//							while (line != null) {
//								inputStringBuilder.append(line);
//								inputStringBuilder.append('\n');
//								line = reader.readLine();
//							}
//
//							String cause = ". Caused by " + inputStringBuilder
//									.toString();
//
//							message.append(cause);
//
//							throw new InsertPartException(message.toString());
//						}
//					}


					} else {
						//message.append(". You probably dont have enought PERMISSION for this action.");
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

								//throw new InsertPartException(message.toString());
							}
						}

					}

					throw new InsertPartException(
							message.toString() + "\n\n" + "URL endpoint: " + endpointURL
							.toString() + " POST content: " + parameters);
					//throw new RDFException(message.toString());
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

				final String message = String.format(
						"%s/%s attempt to reconnect %s FAILED", retryCount,
						getRetryConnectionSizeAsString(), call
						.toString());

				logger.debug(message);

				if (retryCount > RETRY_CONNECTION_SIZE && !hasInfinityRetryConnection()) {
					final String errorMessage = "Endpoint HTTP connection stream cannot be opened. ";
					logger.debug(errorMessage);
					if (httpConnection != null) {
						httpConnection.disconnect();
					}
					throw new RDFException(errorMessage + e.getMessage(), e);
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

	private boolean hasInfinityRetryConnection() {
		if (RETRY_CONNECTION_SIZE < 0) {
			return true;
		} else {
			return false;
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

			//connection.commit();

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
			HandlerExtractType handlerExtractType) throws RDFException {
		URL urlPath;
		try {
			urlPath = new URL(path);
		} catch (MalformedURLException ex) {
			throw new RDFException(ex.getMessage(), ex);
		}

		try {
			try (InputStreamReader inputStreamReader = new InputStreamReader(
					urlPath.openStream(), Charset.forName(encode))) {

				RDFFormat format = RDFFormat.forFileName(path, RDFFormat.RDFXML);
				RepositoryConnection connection = getConnection();

				switch (handlerExtractType) {
					case STANDARD_HANDLER:
						parseFileUsingStandardHandler(format, inputStreamReader,
								baseURI, connection);
						break;
					case ERROR_HANDLER_CONTINUE_WHEN_MISTAKE:
						parseFileUsingStatisticalHandler(format,
								inputStreamReader,
								baseURI, connection, false);
						break;
					case ERROR_HANDLER_FAIL_WHEN_MISTAKE:
						parseFileUsingStatisticalHandler(format,
								inputStreamReader,
								baseURI, connection, true);
						break;
				}
			}

		} catch (IOException ex) {
			throw new RDFException(ex.getMessage(), ex);
		} catch (RepositoryException ex) {
			hasBrokenConnection = true;
			throw new RDFException(ex.getMessage(), ex);
		}
	}

	private void addFilesInDirectoryToRepository(RDFFormat format, File[] files,
			String baseURI,
			HandlerExtractType handlerExtractType, boolean skipFiles,
			Resource... graphs)
			throws RDFException {

		if (files == null) {
			return; // nothing to add
		}

		for (int i = 0; i < files.length; i++) {
			File nextFile = files[i];

			try {
				addFileToRepository(format, nextFile, baseURI,
						handlerExtractType, graphs);

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

	private void addFileToRepository(RDFFormat fileFormat, File dataFile,
			String baseURI,
			HandlerExtractType handlerExtractType, Resource... graphs) throws RDFException {

		//in case that RDF format is AUTO or not fixed.
		if (fileFormat == null) {
			fileFormat = RDFFormat.forFileName(dataFile.getAbsolutePath(),
					RDFFormat.RDFXML);
		}

		RepositoryConnection connection = null;

		try (InputStreamReader is = new InputStreamReader(new FileInputStream(
				dataFile), Charset.forName(encode))) {

			connection = getConnection();

			switch (handlerExtractType) {
				case STANDARD_HANDLER:
					parseFileUsingStandardHandler(fileFormat, is, baseURI,
							connection);
					break;
				case ERROR_HANDLER_CONTINUE_WHEN_MISTAKE:
					parseFileUsingStatisticalHandler(fileFormat, is, baseURI,
							connection, false);
					break;
				case ERROR_HANDLER_FAIL_WHEN_MISTAKE:
					parseFileUsingStatisticalHandler(fileFormat, is, baseURI,
							connection, true);
					break;
			}

			//connection.commit();

		} catch (IOException ex) {
			logger.debug(ex.getMessage(), ex);
			throw new RDFException("IO Exception: " + ex.getMessage(), ex);
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

	private void parseFileUsingStatisticalHandler(RDFFormat fileFormat,
			InputStreamReader is, String baseURI,
			RepositoryConnection connection, boolean failWhenErrors) throws RDFException {

		StatisticalHandler handler = new StatisticalHandler(connection);
		parseFileUsingHandler(handler, fileFormat, is, baseURI);

		if (handler.hasFindedProblems()) {
			String problems = handler.getFindedProblemsAsString();

			logger.error(problems);
			if (failWhenErrors) {
				throw new RDFException(problems);
			}
		}
	}

	private void parseFileUsingStandardHandler(RDFFormat fileFormat,
			InputStreamReader is, String baseURI,
			RepositoryConnection connection) throws RDFException {

		TripleCountHandler handler = new TripleCountHandler(connection);
		parseFileUsingHandler(handler, fileFormat, is, baseURI);
	}

	private void parseFileUsingHandler(TripleCountHandler handler,
			RDFFormat fileFormat,
			InputStreamReader is, String baseURI) throws RDFException {

		handler.setGraphContext(graph);
		RDFParser parser = getRDFParser(fileFormat, handler);

		try {
			parser.parse(is, baseURI);
		} catch (IOException | RDFParseException | RDFHandlerException ex) {
			throw new RDFException(ex.getMessage(), ex);
		}
	}

	private void setErrorsListenerToParser(RDFParser parser,
			final StatisticalHandler handler) {

		if (parser == null || handler == null) {
			return;

		}

		parser.setParseErrorListener(new ParseErrorListener() {
			@Override
			public void warning(String msg, int lineNo, int colNo) {
				handler.addWarning(msg, lineNo, colNo);
			}

			@Override
			public void error(String msg, int lineNo, int colNo) {
				handler.addError(msg, lineNo, colNo);
			}

			@Override
			public void fatalError(String msg, int lineNo, int colNo) {
				handler.addError(msg, lineNo, colNo);
			}
		});
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

			//connection.commit();


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

			//connection.commit();

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
					closeConnection();
					repository.shutDown();
					logger.debug("Repository with data graph <" + getDataGraph()
							.stringValue() + "> destroyed SUCCESSFUL.");
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
	 * Remove one RDF triple (statement) if contains in repository.
	 *
	 * @param subject   One of type for subject - URI,BlankNode.
	 * @param predicate URI for subject.
	 * @param object    One of type for object - URI,BlankNode or Literal.
	 */
	@Override
	public void removeTriple(Resource subject, URI predicate, Value object) {
		if (isTripleInRepository(subject, predicate, object)) {

			try {
				RepositoryConnection connection = getConnection();
				if (graph != null) {
					connection.remove(subject, predicate, object, graph);
				} else {
					connection.remove(subject, predicate, object);
				}
			} catch (RepositoryException ex) {
				hasBrokenConnection = true;
				logger.debug(ex.getMessage());
			}
		}


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

	@Override
	public RepositoryConnection getConnection() throws RepositoryException {

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
}
