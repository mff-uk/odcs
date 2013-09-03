package cz.cuni.xrg.intlib.rdf.interfaces;

import cz.cuni.xrg.intlib.rdf.enums.InsertType;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;
import cz.cuni.xrg.intlib.rdf.enums.WriteGraphType;
import cz.cuni.xrg.intlib.rdf.exceptions.InvalidQueryException;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFException;
import cz.cuni.xrg.intlib.rdf.impl.MyTupleQueryResult;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.openrdf.model.*;
import org.openrdf.rio.RDFFormat;

/**
 * Interface provides methods important for DPU developers.
 *
 * @author Jiri Tomes
 */
public interface RDFDataUnitHelper {

	
        /**
	 * Add one RDF triple (statement) to the repository. Subject, predicate, object 
         * may be prepare by the corresponding methods createURI, createBlankNode, or createLiteral
	 *
	 * @param subject   One of type for subject - URI, Blank node.
	 * @param predicate URI for subject.
	 * @param object    One of type for object - URI, Blank node or Literal.
	 */
	public void addTriple(Resource subject, URI predicate, Value object);
        
        
        /**
	 * Create new blank node with the defined id. 
	 *
	 * @param id String value of ID, e.g., b12345
	 * @return created blank node, e.g., _:b12345.
	 */
	public BNode createBlankNode(String id);

	/**
	 * Create new URI resource.
	 *
	 * @param uri String value for URI, e.g., "http://linked.opendata.cz/resource/test".
	 * @return created URI, e.g., &lt;http://linked.opendata.cz/resource/test&gt.
	 */
	public URI createURI(String uri);

	/**
	 * Create new typed literal.
	 *
	 * @param literalLabel String value for literal, e.g., "myValue".
	 * @param dataType     URI of data type for the literal, e.g., "http://www.w3.org/2001/XMLSchema#dateTime". It does not support prefixes.
	 * @return Created typed literal, e.g., """myValue"""^^<http://www.w3.org/2001/XMLSchema#dateTime>.
	 */
	public Literal createLiteral(String literalLabel, URI dataType);

	/**
	 * Create new language literal.
	 *
	 * @param literalLabel String value for literal, e.g., "myValue".
	 * @param language     String value for language tag, e.g. "de" for German literal.
	 * @return Created language literal, e.g., """myValue"""@de.
	 */
	public Literal createLiteral(String literalLabel, String language);

	/**
	 * Create new label literal.
	 *
	 * @param literalLabel String value for literal, e.g., "myValue".
	 * @return created language literal.
	 */
	public Literal createLiteral(String literalLabel) ;
        
        /**
	 * Check if RDF triple is in repository.
	 *
	 * @param subject   URI or blank node for subject
	 * @param predicate URI for predicate
	 * @param object    URI, blank node or literal for object
	 * @return true if such RDF triple is in repository, false otherwise.
	 */
        public boolean isTripleInRepository(Resource subject,
			URI predicate, Value object);
        
        
        
        
	/**
	 * Extract RDF triples from RDF file to data unit. It expects RDF/XML serialization of RDF data
	 *
	 * @param file File contains RDF data to extract.
	 *
	 * @throws RDFException when extraction fail.
	 */
	public void addFromFile(File file) throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file   File contains RDF data to extract.
	 * @param format Specifies concrete {@link RDFFormat} (e.g., RDFXML, Turtle,
	 *               ..) if RDF format can not be detected from file suffix.
	 *
	 * @throws RDFException when extraction fail.
	 */
	public void addFromFile(File file, RDFFormat format) throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file                  File which contains RDF data to extract.
	 * @param format                Specifies concrete {@link RDFFormat} (e.g.,
	 *                              RDFXML, Turtle, ..) if RDF format can not be
	 *                              detected from file suffix.
	 * @param useStatisticalHandler boolean value, if during extraction the 
	 *                              detailed statistic about parsed RDF triples and
	 *                              detailed log is needed or not
	 * @throws RDFException when extraction fail.
	 */
	public void addFromFile(File file, RDFFormat format,
			boolean useStatisticalHandler) throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file  File which contains RDF data to extract.
	 *
	 * @throws RDFException when extraction fail.
	 */
	public void addFromTurtleFile(File file) throws RDFException;
        
        /**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file  File which contains RDF data to extract.
	 *
	 * @throws RDFException when extraction fail.
	 */
	public void addFromRDFXMLFile(File file) throws RDFException;

	/**
	 * Load all triples in repository to defined file in defined RDF format.
	 *
	 * @param file       File where data be saved.
	 * @param formatType Type of RDF format for saving data (example: TURTLE,
	 *                   RDF/XML,etc.)
	 * @throws RDFException when loading data to file fail.
	 */
	public void storeToFile(File file, RDFFormatType formatType) throws RDFException;

//	/**
//	 * Load RDF data from repository to SPARQL endpointURL to the one URI graph
//	 * without endpoint authentication.
//	 *
//	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
//	 *                        RDF data.
//	 * @param defaultGraphURI name of graph where RDF data are loading.
//	 * @param graphType       One of way, how to solve loading RDF data to graph
//	 *                        when is it is not empty (MERGE, OVERRIDE, FAIL).
//	 * @param insertType      One of way, how solve loading RDF data parts to
//	 *                        SPARQL endpoint (SKIP_BAD_TYPES,
//	 *                        STOP_WHEN_BAD_PART).
//	 * @throws RDFException when loading data fail.
//	 */
//	public void loadToSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
//			WriteGraphType graphType, InsertType insertType) throws RDFException;

//	/**
//	 * Load RDF data from repository to SPARQL endpointURL to the one URI graph
//	 * with endpoint authentication (name,password).
//	 *
//	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
//	 *                        RDF data.
//	 * @param defaultGraphURI name of graph where RDF data are loading.
//	 * @param name            String name needed for authentication.
//	 * @param password        String password needed for authentication.
//	 * @param graphType       One of way, how to solve loading RDF data to graph
//	 *                        when is it is not empty (MERGE, OVERRIDE, FAIL).
//	 * @param insertType      One of way, how solve loading RDF data parts to
//	 *                        SPARQL endpoint (SKIP_BAD_TYPES,
//	 *                        STOP_WHEN_BAD_PART).
//	 * @throws RDFException when loading data to SPARQL endpoint fail.
//	 */
//	public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
//			String name,
//			String password, WriteGraphType graphType, InsertType insertType)
//			throws RDFException;

//	/**
//	 * Load RDF data from repository to SPARQL endpointURL to the collection of
//	 * URI graphs without endpoint authentication.
//	 *
//	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
//	 *                        RDF data.
//	 * @param defaultGraphURI List with names of graph where RDF data are
//	 *                        loading.
//	 * @param graphType       One of way, how to solve loading RDF data to graph
//	 *                        when is it is not empty (MERGE, OVERRIDE, FAIL).
//	 * @param insertType      One of way, how solve loading RDF data parts to
//	 *                        SPARQL endpoint (SKIP_BAD_TYPES,
//	 *                        STOP_WHEN_BAD_PART).
//	 * @throws RDFException when loading data to SPARQL endpoint fail.
//	 */
//	public void loadtoSPARQLEndpoint(URL endpointURL,
//			List<String> endpointGraphsURI, WriteGraphType graphType,
//			InsertType insertType) throws RDFException;

//	/**
//	 * Load RDF data from repository to SPARQL endpointURL to the collection of
//	 * URI graphs with endpoint authentication (name,password).
//	 *
//	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
//	 *                        RDF data.
//	 * @param defaultGraphURI List with names of graph where RDF data are
//	 *                        loading.
//	 * @param userName        String name needed for authentication.
//	 * @param password        String password needed for authentication.
//	 * @param graphType       One of way, how to solve loading RDF data to graph
//	 *                        when is it is not empty (MERGE, OVERRIDE, FAIL).
//	 * @param insertType      One of way, how solve loading RDF data parts to
//	 *                        SPARQL endpoint (SKIP_BAD_TYPES,
//	 *                        STOP_WHEN_BAD_PART).
//	 * @throws RDFException when loading data fail.
//	 */
//	public void loadtoSPARQLEndpoint(URL endpointURL,
//			List<String> endpointGraphsURI, String userName,
//			String password, WriteGraphType graphType, InsertType insertType)
//			throws RDFException;

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph without authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are stored.
	 * @throws RDFException when extraction data from SPARQL endpoint fail.
	 */
	public void addFromSPARQLEndpoint(URL enpointURL, String namedGraph)
			throws RDFException;

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
	public void addFromSPARQLEndpoint(URL endpointURL,
			String namedGraph, String query) throws RDFException;

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
	public void addFromSPARQLEndpoint(URL endpointURL,
			String namedGraph, String user,
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
	 *
	 * @throws RDFException when extraction data from SPARQL endpoint fail.
	 */
	public void addFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphURI, String query, String hostName,
			String password) throws RDFException;

//	/**
//	 * Extract RDF data from SPARQL endpoint to repository using only data from
//	 * URI graph using authentication (name,password).
//	 *
//	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
//	 *                        RDF data.
//	 * @param defaultGraphUri name of graph where RDF data are stored.
//	 * @param query           String SPARQL query.
//	 * @param hostName        String name needed for authentication.
//	 * @param password        String password needed for authentication.
//	 * @param format          Type of RDF format for saving data (example:
//	 *                        TURTLE, RDF/XML,etc.)
//	 * @throws RDFException when extraction data from SPARQL endpoint fail.
//	 */
//	public void extractFromSPARQLEndpoint(URL endpointURL,
//			String defaultGraphUri, String query,
//			String hostName, String password, RDFFormat format) throws RDFException;

//	/**
//	 * Extract RDF data from SPARQL endpoint to repository using only data from
//	 * collection of URI graphs using authentication (name,password).
//	 *
//	 * @param endpointURL         Remote URL connection to SPARQL endpoint
//	 *                            contains RDF data.
//	 * @param defaultGraphsUri    List with names of graph where RDF data are
//	 *                            stored.
//	 * @param query               String SPARQL query.
//	 * @param hostName            String name needed for authentication.
//	 * @param password            String password needed for authentication.
//	 * @param format              Type of RDF format for saving data (example:
//	 *                            TURTLE, RDF/XML,etc.)
//	 * @param useStatisticHandler boolean value if detailed log and statistic
//	 *                            are awailable or not.
//	 * @param extractFail         boolean value, if true stop pipeline(cause
//	 *                            exception) when no triples were extracted. if
//	 *                            false step triple count extraction criterium.
//	 * @throws RDFException when extraction data from SPARQL endpoint fail.
//	 */
//	public void extractFromSPARQLEndpoint(URL endpointURL,
//			List<String> endpointGraphsURI,
//			String query, String hostName, String password, RDFFormat format,
//			boolean useStatisticHandler, boolean extractFail) throws RDFException;

	/**
	 * Transform RDF in repository by SPARQL updateQuery.
	 *
	 * @param updateQuery String value of update SPARQL query.
	 * @throws RDFException when transformation fail.
	 */
	public void transform(String updateQuery) throws RDFException;

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
	public Map<String, List<String>> executeSelectQuery(
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
	public MyTupleQueryResult executeSelectQueryAsTuples(
			String selectQuery) throws InvalidQueryException;

//	/**
//	 * Make construct query over repository data and return file where RDF data
//	 * as result are saved.
//	 *
//	 * @param constructQuery String representation of SPARQL query.
//	 * @param formatType     Choosed type of format RDF data in result.
//	 * @param filePath       String path to file where result with RDF data is
//	 *                       stored.
//	 * @return File with RDF data in defined format as result of construct
//	 *         query.
//	 * @throws InvalidQueryException when query is not valid or creating file
//	 *                               fail.
//	 */
//	public File makeConstructQueryOverRepository(String constructQuery,
//			RDFFormatType formatType, String filePath) throws InvalidQueryException;

	/**
	 * Make construct query over repository data and return interface Graph as
	 * result contains iterator for statements (triples).
	 *
	 * @param constructQuery String representation of SPARQL query.
	 * @return Interface Graph as result of construct SPARQL query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	public Graph executeConstructQuery(
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
	public List<Statement> getTriples();

	/**
	 * Copy all data from repository to targetRepository.
	 *
	 * @param targetRepository goal repository where RDF data are added.
	 */
	public void copyAllDataToTargetDataUnit(RDFDataUnit targetRepo);

	
}
