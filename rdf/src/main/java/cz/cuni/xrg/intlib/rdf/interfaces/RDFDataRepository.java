package cz.cuni.xrg.intlib.rdf.interfaces;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import cz.cuni.xrg.intlib.rdf.enums.FileExtractType;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;

import cz.cuni.xrg.intlib.rdf.enums.WriteGraphType;
import cz.cuni.xrg.intlib.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.rdf.exceptions.InvalidQueryException;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;

/**
 * Enable work with RDF data repository.
 *
 * @author Jiri Tomes
 * @author Petyr
 *
 */
public interface RDFDataRepository extends DataUnit {

	/**
	 * Add one tripple RDF (statement) to the repository.
	 *
	 * @param namespace     String name of defined namespace
	 * @param subjectName   String name of subject
	 * @param predicateName String name of predicate
	 * @param objectName    String name of object
	 */
	public void addTripleToRepository(String namespace, String subjectName,
			String predicateName,
			String objectName);

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param extractType         One of defined enum type for extraction data
	 *                            from file.
	 * @param path                String path to file/directory
	 * @param suffix              String suffix of fileName (example: ".ttl",
	 *                            ".xml", etc)
	 * @param baseURI             String name of defined used URI
	 * @param useSuffix           boolean value, if extract files only with
	 *                            defined suffix or not.
	 * @param useStatisticHandler boolean value, if during extraction needed
	 *                            detail statistic about RDF triples and
	 *                            detailed log or not.
	 * @throws ExtractException when extraction fail.
	 */
	public void extractRDFfromFileToRepository(FileExtractType extractType,
			String path, String suffix,
			String baseURI,
			boolean useSuffix, boolean useStatisticHandler) throws ExtractException;

	/**
	 * Load all triples in repository to defined file in defined RDF format.
	 *
	 * @param directoryPath Path to directory, where file with RDF data will be
	 *                      saved.
	 * @param fileName      Name of file for saving RDF data.
	 * @param format        Type of RDF format for saving data (example: TURTLE,
	 *                      RDF/XML,etc.)
	 * @throws CannotOverwriteFileException when file is protected for
	 *                                      overwritting.
	 * @throws LoadException                when loading data fault.
	 */
	public void loadRDFfromRepositoryToFile(String directoryPath,
			String fileName,
			org.openrdf.rio.RDFFormat format) throws CannotOverwriteFileException, LoadException;

	/**
	 * Load all triples in repository to defined file in defined RDF format.
	 *
	 * @param directoryPath    Path to directory, where file with RDF data will
	 *                         be saved.
	 * @param fileName         Name of file for saving RDF data.
	 * @param format           Type of RDF format for saving data (example:
	 *                         TURTLE, RDF/XML,etc.)
	 * @param canFileOverWrite boolean value, if existing file can be
	 *                         overwritten.
	 * @param isNameUnique     boolean value, if every pipeline execution has
	 *                         his unique name.
	 * @throws CannotOverwriteFileException when file is protected for
	 *                                      overwritting.
	 * @throws LoadException                when loading data fault.
	 */
	public void loadRDFfromRepositoryToFile(String directoryPath,
			String fileName, org.openrdf.rio.RDFFormat format,
			boolean canFileOverWrite, boolean isNameUnique) throws CannotOverwriteFileException, LoadException;

	/**
	 * Load RDF data from repository to SPARQL endpointURL to the one URI graph
	 * without endpoint authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.
	 * @throws LoadException when loading data fault.
	 */
	public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType) throws LoadException;

	/**
	 * Load RDF data from repository to SPARQL endpointURL to the one URI graph
	 * with endpoint authentication (name,password).
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.
	 * @param name            String name needed for authentication.
	 * @param password        String password needed for authentication.
	 * @throws LoadException when loading data fault.
	 */
	public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
			String name,
			String password, WriteGraphType graphType) throws LoadException;

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
	 * @throws LoadException when loading data fault.
	 */
	public void loadtoSPARQLEndpoint(URL endpointURL,
			List<String> endpointGraphsURI, String userName,
			String password, WriteGraphType graphType) throws LoadException;

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph without authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are loading.
	 * @param query           String SPARQL query.
	 * @throws ExtractException when extraction data fault.
	 */
	public void extractfromSPARQLEndpoint(URL endpointURL,
			String defaultGraphUri, String query) throws ExtractException;

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
	 * @throws ExtractException when extraction data fault.
	 */
	public void extractfromSPARQLEndpoint(URL endpointURL,
			String defaultGraphUri, String query,
			String hostName, String password, RDFFormat format) throws ExtractException;

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * collection of URI graphs using authentication (name,password).
	 *
	 * @param endpointURL      Remote URL connection to SPARQL endpoint contains
	 *                         RDF data.
	 * @param defaultGraphsUri List with names of graph where RDF data are
	 *                         loading.
	 * @param query            String SPARQL query.
	 * @param hostName         String name needed for authentication.
	 * @param password         String password needed for authentication.
	 * @param format           Type of RDF format for saving data (example:
	 *                         TURTLE, RDF/XML,etc.)
	 * @throws ExtractException when extraction data fault.
	 */
	public void extractfromSPARQLEndpoint(URL endpointURL,
			List<String> endpointGraphsURI,
			String query, String hostName, String password,
			boolean useStatisticHandler) throws ExtractException;

	/**
	 * Transform RDF in repository by SPARQL updateQuery.
	 *
	 * @param updateQuery String value of update SPARQL query.
	 * @throws TransformException when transformation fault.
	 */
	public void transformUsingSPARQL(String updateQuery) throws TransformException;

	/**
	 * Return count of triples stored in repository.
	 *
	 * @return size of triples in repository.
	 */
	public long getTripleCountInRepository();

	/**
	 * Return if RDF triple is in repository.
	 *
	 * @param namespace     String name of defined namespace
	 * @param subjectName   String name of subject
	 * @param predicateName String name of predicate
	 * @param objectName    String name of object
	 * @return true if such statement is in repository, false otherwise.
	 */
	public boolean isTripleInRepository(String namespace, String subjectName,
			String predicateName,
			String objectName);

	/**
	 * Removes all RDF data from repository.
	 */
	public void cleanAllRepositoryData();

	/**
	 * Copy all data from repository to targetRepository.
	 *
	 * @param targetRepository goal repository where RDF data are added.
	 */
	public void copyAllDataToTargetRepository(RDFDataRepository targetRepo);

	/**
	 * Make RDF data merge over repository - data in repository merge with data
	 * in second defined repository.
	 *
	 *
	 * @param second Type of repository contains RDF data as implementation of
	 *               RDFDataRepository interface.
	 * @throws IllegalArgumentException if second repository as param is null.
	 */
	public void mergeRepositoryData(RDFDataRepository second) throws IllegalArgumentException;

	/**
	 * Return openRDF repository needed for almost every operation using RDF.
	 *
	 * @return openRDF repository.
	 */
	public Repository getDataRepository();

	/**
	 * Return all triples(statements) in reposiotory as list.
	 *
	 * @return List<code>&lt;Statement&gt;</code> list of all triples in
	 *         repository/
	 */
	public List<Statement> getRepositoryStatements();

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
	public Map<String, List<String>> makeSelectQueryOverRepository(
			String selectQuery)
			throws InvalidQueryException;

	/**
	 * Make construct query over repository data and return file where RDF data
	 * as result are saved.
	 *
	 * @param constructQuery String representation of SPARQL query.
	 * @param formatType     Choosed type of format RDF data in result.
	 * @param fileName       Name of file where result with RDF data is stored.
	 * @return File with RDF data in defined format as result of construct
	 *         query.
	 * @throws InvalidQueryException when query is not valid or creating file
	 *                               fault.
	 */
	public File makeConstructQueryOverRepository(String constructQuery,
			RDFFormatType formatType, String fileName) throws InvalidQueryException;
}
