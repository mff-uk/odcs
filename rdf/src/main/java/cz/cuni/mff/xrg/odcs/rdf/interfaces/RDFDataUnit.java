package cz.cuni.mff.xrg.odcs.rdf.interfaces;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;

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
	 * @param file                  File contains RDF data to extract.
	 * @param format                Specifies concrete {@link RDFFormat} (e.g.,
	 *                              RDFXML, Turtle, ..) if RDF format can not be
	 *                              detected from file suffix.
	 * @param baseURI               String name of defined used URI prefix
	 *                              namespace used by all triples.
	 * @param useStatisticalHandler boolean value, if during extraction needed
	 *                              detail statistic about RDF triples and
	 *                              detailed log or not.
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromFile(File file, RDFFormat format, String baseURI,
			boolean useStatisticalHandler) throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param extractType         One of defined enum type for extraction data
	 *                            from file.
	 * @param path                String path to file/directory
	 * @param suffix              String suffix of fileName (example: ".ttl",
	 *                            ".xml", etc)
	 * @param baseURI             String name of defined used URI prefix
	 *                            namespace used by all triples.
	 * @param useSuffix           boolean value, if extract files only with
	 *                            defined suffix or not.
	 * @param useStatisticHandler boolean value, if during extraction needed
	 *                            detail statistic about RDF triples and
	 *                            detailed log or not.
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromFile(FileExtractType extractType,
			String path, String suffix,
			String baseURI,
			boolean useSuffix, boolean useStatisticHandler) throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param format              Specifies concrete {@link RDFFormat} (e.g.,
	 *                            RDFXML, Turtle, ..) if RDF format can not be
	 *                            detected from file suffix.
	 * @param extractType         One of defined enum type for extraction data
	 *                            from file.
	 * @param path                String path to file/directory
	 * @param suffix              String suffix of fileName (example: ".ttl",
	 *                            ".xml", etc)
	 * @param baseURI             String name of defined used URI prefix
	 *                            namespace used by all triples.
	 * @param useSuffix           boolean value, if extract files only with
	 *                            defined suffix or not.
	 * @param useStatisticHandler boolean value, if during extraction needed
	 *                            detail statistic about RDF triples and
	 *                            detailed log or not.
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromFile(RDFFormat format, FileExtractType extractType,
			String path, String suffix,
			String baseURI,
			boolean useSuffix, boolean useStatisticHandler) throws RDFException;

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
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph using authentication (name,password).
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are stored.
	 * @param query           String SPARQL query.
	 * @param hostName        String name needed for authentication.
	 * @param password        String password needed for authentication.
	 * @param format          Type of RDF format for saving data (example:
	 *                        TURTLE, RDF/XML,etc.)
	 * @throws RDFException when extraction data from SPARQL endpoint fail.
	 */
	public void extractFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphUri, String query,
			String hostName, String password, RDFFormat format) throws RDFException;

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
			WriteGraphType graphType, InsertType insertType) throws RDFException;

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
	 * @throws RDFException when loading data to SPARQL endpoint fail.
	 */
	public void loadToSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
			String name,
			String password, WriteGraphType graphType, InsertType insertType)
			throws RDFException;

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
			InsertType insertType) throws RDFException;

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
	 * Removes all RDF data in defined graph using connecion to SPARQL endpoint
	 * address. For data deleting is necessarry to have endpoint with update
	 * rights.
	 *
	 * @param endpointURL   URL address of endpoint connect to.
	 * @param endpointGraph Graph name in URI format.
	 * @throws RDFException When you dont have update right for this action, or
	 *                      connection is lost before succesfully ending.
	 */
	public void clearEndpointGraph(URL endpointURL, String endpointGraph)
			throws RDFException;

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
}
