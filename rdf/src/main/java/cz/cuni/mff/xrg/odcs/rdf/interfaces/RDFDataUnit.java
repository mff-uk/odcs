package cz.cuni.mff.xrg.odcs.rdf.interfaces;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.handlers.TripleCountHandler;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import org.openrdf.model.URI;
import org.openrdf.query.Dataset;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;

/**
 * Provides method for working with RDF data repository.
 *
 * @author Jiri Tomes
 * @author Petyr
 *
 */
public interface RDFDataUnit extends DataUnit, ManagableDataUnit, RDFDataUnitHelper {

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file    File contains RDF data to extract.
	 * @param format  Specifies concrete {@link RDFFormat} (e.g., RDFXML,
	 *                Turtle, ..) if RDF format can not be detected from file
	 *                suffix.
	 * @param baseURI String name of defined used URI prefix namespace used by
	 *                all triples.
	 *
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromFile(File file, RDFFormat format, String baseURI)
			throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file               File contains RDF data to extract.
	 * @param format             Specifies concrete {@link RDFFormat} (e.g.,
	 *                           RDFXML, Turtle, ..) if RDF format can not be
	 *                           detected from file suffix.
	 * @param baseURI            String name of defined used URI prefix
	 *                           namespace used by all triples.
	 *                           HandlerExtractType handlerExtractType
	 * @param handlerExtractType Possibilies how to choose handler for data
	 *                           extraction and how to solve finded problems
	 *                           with no valid data.
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromFile(File file, RDFFormat format, String baseURI,
			HandlerExtractType handlerExtractType) throws RDFException;

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
	 * @param baseURI            String name of defined used URI prefix
	 *                           namespace used by all triples.
	 * @param useSuffix          boolean value, if extract files only with
	 *                           defined suffix or not.
	 * @param handlerExtractType Possibilies how to choose handler for data
	 *                           extraction and how to solve finded problems
	 *                           with no valid data.
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromFile(FileExtractType extractType,
			RDFFormat format,
			String path, String suffix,
			String baseURI,
			boolean useSuffix, HandlerExtractType handlerExtractType) throws RDFException;

	/**
	 * Load all triples in repository to defined file in defined RDF format.
	 *
	 * @param filePath   Path to file, where RDF data will be saved.
	 * @param formatType Type of RDF format for saving data (example: TURTLE,
	 *                   RDF/XML,etc.)
	 * @throws CannotOverwriteFileException when file is protected for
	 *                                      overwritting.
	 * @throws RDFException                 when loading data to file fail.
	 */
	public void loadToFile(String filePath,
			RDFFormatType formatType) throws CannotOverwriteFileException, RDFException;

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
	 * @throws RDFException                 when loading data to file fail.
	 */
	public void loadToFile(String filePath, RDFFormatType formatType,
			boolean canFileOverWrite, boolean isNameUnique) throws CannotOverwriteFileException, RDFException;

	/**
	 * Make select query over repository data and return tables as result.
	 *
	 * @param selectQuery String representation of SPARQL query.
	 * @return <code>Map&lt;String,List&lt;String&gt;&gt;</code> as table, where
	 *         map key is column name and <code>List&lt;String&gt;</code> are
	 *         string values in this column. When query is invalid, return *
	 *         empty <code>Map</code>.
	 * @throws InvalidQueryException when query is not valid.
	 */
	public Map<String, List<String>> executeSelectQuery(
			String selectQuery)
			throws InvalidQueryException;

	/**
	 * Removes all RDF data from repository.
	 */
	public void cleanAllData();

	/**
	 * Make RDF data merge over repository - data in repository merge with data
	 * in second defined repository.
	 *
	 *
	 * @param second Type of repository contains RDF data as implementation of
	 *               RDFDataUnit interface.
	 * @throws IllegalArgumentException if second repository as param is null.
	 */
	public void mergeRepositoryData(RDFDataUnit second) throws IllegalArgumentException;

	/**
	 * Return openRDF repository needed for almost every operation using RDF.
	 *
	 * @return openRDF repository.
	 */
	public Repository getDataRepository();

	/**
	 * Return URI representation of graph where RDF data are stored.
	 *
	 * @return graph with stored data as URI.
	 */
	public URI getDataGraph();

	/**
	 * Set data graph storage for given data in RDF format.
	 *
	 * @param newDataGraph new graph representated as URI.
	 */
	public void setDataGraph(URI newDataGraph);

	/**
	 * Set new data graph as default storage for data in RDF format.
	 *
	 * @param newStringDataGraph String name of graph as URI - starts with
	 *                           prefix http://).
	 */
	public void setDataGraph(String newStringDataGraph);

	/**
	 * Definitely destroy repository - use after all working in repository.
	 * Another repository using cause exception. For other using you have to
	 * create new instance.
	 */
	public void shutDown();

	/**
	 * Add all RDF triples in defined graph to reposiotory.
	 *
	 * @param graphInstance Concrete graph contains RDF triples.
	 */
	public void addTriplesFromGraph(Graph graphInstance);

	/**
	 * For Browsing all data in graph return its size {count of rows}.
	 *
	 * @return count of rows for browsing all data in graph.
	 * @throws InvalidQueryException if query for find out count of rows in not
	 *                               valid.
	 */
	public long getResultSizeForDataCollection() throws InvalidQueryException;

	/**
	 * For given valid SELECT of CONSTRUCT query return its size {count of rows
	 * returns for given query).
	 *
	 * @param query Valid SELECT/CONTRUCT query for asking.
	 * @return size for given valid query as long.
	 * @throws InvalidQueryException if query is not valid.
	 */
	public long getResultSizeForQuery(String query) throws InvalidQueryException;

	/**
	 * Return iterable collection of all statemens in repository. Needed for
	 * adding/merge large collection when is not possible to return all
	 * statements (RDF triples) at once in method as in {@link #getTriples() }.
	 *
	 * @return Iterable collection of Statements need for lazy
	 */
	public RepositoryResult<Statement> getRepositoryResult();

	/**
	 *
	 * @return dataset for graphs set in reposiotory as default.
	 */
	public Dataset getDataSet();

	/**
	 *
	 * @param chunkSize size of triples/statements in one part.
	 * @return Count of parts as split data in reposioty by defined chunkSize .
	 */
	public long getPartsCount(long chunkSize);

	/**
	 * Create RDF parser for given RDF format and set RDF handler where are data
	 * insert to.
	 *
	 * @param format  RDF format witch is set to RDF parser
	 * @param handler Type of handler where RDF parser used for parsing.
	 * @return RDFParser for given RDF format and handler.
	 */
	public RDFParser getRDFParser(RDFFormat format, TripleCountHandler handler);

	/**
	 *
	 * @return Shared connection to repository.
	 * @throws RepositoryException If something went wrong during the creation
	 *                             of the Connection.
	 */
	public RepositoryConnection getConnection() throws RepositoryException;

	/**
	 * Method called after restarting after DB. Calling method
	 * {@link #getConnection()} provides to get new instance of connection.
	 */
	public void restartConnection();

	/**
	 *
	 * @return List of all application graphs keeps in Virtuoso storage in case
	 *         of Virtuoso repository. When is used local repository as storage,
	 *         this method return an empty list.
	 */
	public List<String> getApplicationGraphs();

	/**
	 * Delete all application graphs keeps in Virtuoso storage in case of
	 * Virtuoso repository. When is used local repository as storage, this
	 * method has no effect.
	 *
	 * @return Info string message about removing application graphs.
	 */
	public String deleteApplicationGraphs();

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
	public Graph describeURI(Resource uriResource) throws InvalidQueryException;

	/**
	 *
	 * Set time in miliseconds how long to wait before trying to reconnect.
	 *
	 * @param retryTimeValue time in milisecond for waiting before trying to
	 *                       reconnect.
	 * @throws IllegalArgumentException if time is 0 or negative long number.
	 */
	public void setRetryConnectionTime(long retryTimeValue) throws IllegalArgumentException;

	/**
	 * Set Count of attempts to reconnect if the connection fails. For infinite
	 * loop use zero or negative integer
	 *
	 * @param retrySizeValue as interger with count of attemts to reconnect.
	 */
	public void setRetryConnectionSize(int retrySizeValue);
}
