package cz.cuni.mff.xrg.odcs.rdf.interfaces;

import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.help.OrderTupleQueryResult;
import cz.cuni.mff.xrg.odcs.rdf.impl.MyTupleQueryResult;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Petyr
 */
public interface ManagableRdfDataUnit extends RDFDataUnit, ManagableDataUnit {

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file File contains RDF data to extract.
	 * @param format Specifies concrete {@link RDFFormat} (e.g., RDFXML, Turtle,
	 * ..) if RDF format can not be detected from file suffix.
	 * @param baseURI String name of defined used URI prefix namespace used by
	 * all triples.
	 *
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromFile(File file, RDFFormat format, String baseURI)
			throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file File contains RDF data to extract.
	 * @param format Specifies concrete {@link RDFFormat} (e.g., RDFXML, Turtle,
	 * ..) if RDF format can not be detected from file suffix.
	 * @param baseURI String name of defined used URI prefix namespace used by
	 * all triples. HandlerExtractType handlerExtractType
	 * @param handlerExtractType Possibilies how to choose handler for data
	 * extraction and how to solve finded problems with no valid data.
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromFile(File file, RDFFormat format, String baseURI,
			HandlerExtractType handlerExtractType) throws RDFException;

	/**
	 * Load all triples in repository to defined file in defined RDF format.
	 *
	 * @param filePath Path to file, where RDF data will be saved.
	 * @param formatType Type of RDF format for saving data (example: TURTLE,
	 * RDF/XML,etc.)
	 * @throws CannotOverwriteFileException when file is protected for
	 * overwritting.
	 * @throws RDFException when loading data to file fail.
	 */
	public void loadToFile(String filePath,
			RDFFormatType formatType) throws CannotOverwriteFileException, RDFException;

	/**
	 * Make select query over repository data and return tables as result.
	 *
	 * @param selectQuery String representation of SPARQL query.
	 * @return <code>Map&lt;String,List&lt;String&gt;&gt;</code> as table, where
	 * map key is column name and <code>List&lt;String&gt;</code> are string
	 * values in this column. When query is invalid, return * empty
	 * <code>Map</code>.
	 * @throws InvalidQueryException when query is not valid.
	 */
	public Map<String, List<String>> executeSelectQuery(
			String selectQuery)
			throws InvalidQueryException;

	/**
	 * Removes all RDF data in defined graph using connecion to SPARQL endpoint
	 * address. For data deleting is necessarry to have endpoint with update
	 * rights.
	 *
	 * @param endpointURL URL address of update endpoint connect to.
	 * @param endpointGraph Graph name in URI format.
	 * @param context DPU context for checking manual canceling in case of
	 * infinite loop (no recovery error).
	 * @throws RDFException When you dont have update right for this action, or
	 * connection is lost before succesfully ending.
	 */
	public void clearEndpointGraph(URL endpointURL, String endpointGraph,
			DPUContext context)
			throws RDFException;

	/**
	 * Make RDF data merge over repository - data in repository merge with data
	 * in second defined repository.
	 *
	 *
	 * @param second Type of repository contains RDF data as implementation of
	 * RDFDataUnit interface.
	 * @throws IllegalArgumentException if second repository as param is null.
	 */
	public void mergeRepositoryData(ManagableRdfDataUnit second) throws IllegalArgumentException;

	/**
	 * Return openRDF repository needed for almost every operation using RDF.
	 *
	 * @return openRDF repository.
	 */
	public Repository getDataRepository();

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
	 * prefix http://).
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
	 * valid.
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
	 * @return List of all application graphs keeps in Virtuoso storage in case
	 * of Virtuoso repository. When is used local repository as storage, this
	 * method return an empty list.
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
	 * Make ORDERED SELECT QUERY (select query contains ORDER BY keyword) over
	 * repository data and return {@link OrderTupleQueryResult} class as result.
	 *
	 * This ordered select query don´t have to containt LIMIT nad OFFSET
	 * keywords.
	 *
	 * For no problem behavior check you setting "MaxSortedRows" param in your
	 * virtuoso.ini file before using. For more info
	 *
	 * @see OrderTupleQueryResult class description.
	 *
	 * @param orderSelectQuery String representation of SPARQL select query.
	 * @return {@link OrderTupleQueryResult} representation of ordered select
	 * query.
	 * @throws InvalidQueryException when query is not valid or containst LIMIT
	 * or OFFSET keyword.
	 */
	public OrderTupleQueryResult executeOrderSelectQueryAsTuples(
			String orderSelectQuery) throws InvalidQueryException;

	/**
	 * Make select query over repository data and return MyTupleQueryResult
	 * class as result.
	 *
	 * @param selectQuery String representation of SPARQL select query.
	 * @return MyTupleQueryResult representation of SPARQL select query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	public MyTupleQueryResult executeSelectQueryAsTuples(
			String selectQuery) throws InvalidQueryException;

}
