package cz.cuni.xrg.intlib.rdf.interfaces;

import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;
import cz.cuni.xrg.intlib.rdf.enums.WriteGraphType;
import cz.cuni.xrg.intlib.rdf.exceptions.InvalidQueryException;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFException;
import cz.cuni.xrg.intlib.rdf.impl.MyTupleQueryResult;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;

/**
 * Interface provides methods important for DPU developers.
 *
 * @author Jiri Tomes
 */
public interface RDFDataUnitHelper {

	/**
	 * Add one tripple RDF (statement) to the repository (default empty
	 * namespace).
	 *
	 * @param subjectName   String name of subject
	 * @param predicateName String name of predicate
	 * @param objectName    String name of object
	 */
	public void addTriple(String subjectName,
			String predicateName, String objectName);

	/**
	 * Add all RDF data from string to repository.
	 *
	 * @param rdfString string constains RDF data.
	 * @param format    RDF format of given string - used to select parser.
	 *
	 * @throws RDFException when adding RDF data failt.
	 */
	public void addRDFString(String rdfString, RDFFormat format) throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file File contains RDF data to extract.
	 *
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromFile(File file) throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file   File contains RDF data to extract.
	 * @param format Specifies concrete {@link RDFFormat} (e.g., RDFXML, Turtle,
	 *               ..) if RDF format can not be detected from file suffix.
	 *
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromFile(File file, RDFFormat format) throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file                  File contains RDF data to extract.
	 * @param format                Specifies concrete {@link RDFFormat} (e.g.,
	 *                              RDFXML, Turtle, ..) if RDF format can not be
	 *                              detected from file suffix.
	 * @param useStatisticalHandler boolean value, if during extraction needed
	 *                              detail statistic about RDF triples and
	 *                              detailed log or not.
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromFile(File file, RDFFormat format,
			boolean useStatisticalHandler) throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param path String path to file
	 *
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromLocalTurtleFile(String path) throws RDFException;

	/**
	 * Load all triples in repository to defined file in defined RDF format.
	 *
	 * @param file       File where data be saved.
	 * @param formatType Type of RDF format for saving data (example: TURTLE,
	 *                   RDF/XML,etc.)
	 * @throws RDFException when loading data to file fail.
	 */
	public void loadToFile(File file, RDFFormatType formatType) throws RDFException;

	/**
	 * Load RDF data from repository to SPARQL endpointURL to the one URI graph
	 * without endpoint authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.
	 * @param graphType       One of way, how to solve loading RDF data to graph
	 *                        when is it is not empty (MERGE, OVERRIDE, FAIL).
	 * @throws RDFException when loading data fail.
	 */
	public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType) throws RDFException;

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
	 * @throws RDFException when loading data to SPARQL endpoint fail.
	 */
	public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
			String name,
			String password, WriteGraphType graphType) throws RDFException;

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
	 * @throws RDFException when loading data to SPARQL endpoint fail.
	 */
	public void loadtoSPARQLEndpoint(URL endpointURL,
			List<String> endpointGraphsURI, WriteGraphType graphType) throws RDFException;

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
	 * @throws RDFException when loading data fail.
	 */
	public void loadtoSPARQLEndpoint(URL endpointURL,
			List<String> endpointGraphsURI, String userName,
			String password, WriteGraphType graphType) throws RDFException;

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph without authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are stored.
	 * @param query           String SPARQL query.
	 * @throws RDFException when extraction data from SPARQL endpoint fail.
	 */
	public void extractfromSPARQLEndpoint(URL endpointURL,
			String defaultGraphUri, String query) throws RDFException;

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
	 *
	 * @throws RDFException when extraction data from SPARQL endpoint fail.
	 */
	public void extractFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphURI, String query, String hostName,
			String password) throws RDFException;

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
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * collection of URI graphs using authentication (name,password).
	 *
	 * @param endpointURL         Remote URL connection to SPARQL endpoint
	 *                            contains RDF data.
	 * @param defaultGraphsUri    List with names of graph where RDF data are
	 *                            stored.
	 * @param query               String SPARQL query.
	 * @param hostName            String name needed for authentication.
	 * @param password            String password needed for authentication.
	 * @param format              Type of RDF format for saving data (example:
	 *                            TURTLE, RDF/XML,etc.)
	 * @param useStatisticHandler boolean value if detailed log and statistic
	 *                            are awailable or not.
	 * @throws RDFException when extraction data from SPARQL endpoint fail.
	 */
	public void extractFromSPARQLEndpoint(URL endpointURL,
			List<String> endpointGraphsURI,
			String query, String hostName, String password, RDFFormat format,
			boolean useStatisticHandler) throws RDFException;

	/**
	 * Transform RDF in repository by SPARQL updateQuery.
	 *
	 * @param updateQuery String value of update SPARQL query.
	 * @throws RDFException when transformation fail.
	 */
	public void transformUsingSPARQL(String updateQuery) throws RDFException;

	/**
	 * Make select query over repository data and return file as SPARQL XML
	 * result.
	 *
	 * @param selectQuery String representation of SPARQL query
	 * @param filePath    String path to file for saving result of query in
	 *                    SPARQL XML syntax.
	 * @return File contains result of given SPARQL select query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	public File makeSelectQueryOverRepository(String selectQuery,
			String filePath)
			throws InvalidQueryException;

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
	 * Make select query over repository data and return MyTupleQueryResult
	 * class as result.
	 *
	 * @param selectQuery String representation of SPARQL select query.
	 * @return MyTupleQueryResult representation of SPARQL select query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	public MyTupleQueryResult makeSelectQueryOverRepositoryAsResult(
			String selectQuery) throws InvalidQueryException;

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
	public File makeConstructQueryOverRepository(String constructQuery,
			RDFFormatType formatType, String filePath) throws InvalidQueryException;

	/**
	 * Make construct query over repository data and return interface Graph as
	 * result contains iterator for statements (triples).
	 *
	 * @param constructQuery String representation of SPARQL query.
	 * @return Interface Graph as result of construct SPARQL query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	public Graph makeConstructQueryOverRepository(
			String constructQuery) throws InvalidQueryException;

	/**
	 * Return count of triples stored in repository.
	 *
	 * @return size of triples in repository.
	 */
	public long getTripleCount();

	/**
	 * Return all triples(statements) in reposiotory as list.
	 *
	 * @return List<code>&lt;Statement&gt;</code> list of all triples in
	 *         repository/
	 */
	public List<Statement> getRepositoryStatements();

	/**
	 * Copy all data from repository to targetRepository.
	 *
	 * @param targetRepository goal repository where RDF data are added.
	 */
	public void copyAllDataToTargetRepository(RDFDataUnit targetRepo);
}
