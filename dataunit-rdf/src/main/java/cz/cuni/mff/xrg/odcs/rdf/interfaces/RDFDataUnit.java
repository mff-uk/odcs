package cz.cuni.mff.xrg.odcs.rdf.interfaces;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.enums.SelectFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.handlers.TripleCountHandler;
import cz.cuni.mff.xrg.odcs.rdf.help.LazyTriples;
import cz.cuni.mff.xrg.odcs.rdf.help.MyTupleQueryResultIf;
import cz.cuni.mff.xrg.odcs.rdf.help.OrderTupleQueryResult;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.openrdf.model.BNode;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.Dataset;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;

/**
 * Interface provides methods for working with RDF data repository.
 *
 * @author Jiri Tomes
 * @author Petyr
 *
 */
public interface RDFDataUnit extends DataUnit {

	/**
	 * Extracts metadata (held within the list of predicates) about certain
	 * subjects (subject URIs)
	 *
	 * @param subjectURI Subject URI for which metadata is searched
	 * @param predicates Predicates being searched
	 * @return Pairs predicate-value for the given subject URI
	 */
	public Map<String, List<String>> getRDFMetadataForSubjectURI(
			String subjectURI,
			List<String> predicates);

	/**
	 * Extracts metadata (held within the list of predicates) about certain
	 * files (based on the file path)
	 *
	 * @param filePath   Path to the file.
	 * @param predicates Predicates being searched
	 * @return Pairs predicate-value for the given filePath
	 */
	public Map<String, List<String>> getRDFMetadataForFile(String filePath,
			List<String> predicates);

	/**
	 * Add one RDF triple (statement) to the repository. Subject, predicate,
	 * object may be prepare by the corresponding methods createURI,
	 * createBlankNode, or createLiteral
	 *
	 * @param subject   One of type for subject - URI, Blank node.
	 * @param predicate URI for subject.
	 * @param object    One of type for object - URI, Blank node or Literal.
	 */
	public void addTriple(Resource subject, URI predicate, Value object);

	/**
	 * Remove one RDF triple (statement) if contains in repository.
	 *
	 * @param subject   One of type for subject - URI,BlankNode.
	 * @param predicate URI for subject.
	 * @param object    One of type for object - URI,BlankNode or Literal.
	 */
	public void removeTriple(Resource subject, URI predicate, Value object);

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
	 * @param uri String value for URI, e.g.,
	 *            "http://linked.opendata.cz/resource/test".
	 * @return created URI, e.g.,
	 *         &lt;http://linked.opendata.cz/resource/test&gt.
	 */
	public URI createURI(String uri);

	/**
	 * Create new typed literal.
	 *
	 * @param literalLabel String value for literal, e.g., "myValue".
	 * @param dataType     URI of data type for the literal, e.g.,
	 *                     "http://www.w3.org/2001/XMLSchema#dateTime". It does
	 *                     not support prefixes.
	 * @return Created typed literal, e.g.,
	 *         """myValue"""^^<http://www.w3.org/2001/XMLSchema#dateTime>.
	 */
	public Literal createLiteral(String literalLabel, URI dataType);

	/**
	 * Create new language literal.
	 *
	 * @param literalLabel String value for literal, e.g., "myValue".
	 * @param language     String value for language tag, e.g. "de" for German
	 *                     literal.
	 * @return Created language literal, e.g.,{@code """myValue"""@de}.
	 *
	 */
	public Literal createLiteral(String literalLabel, String language);

	/**
	 * Create new label literal.
	 *
	 * @param literalLabel String value for literal, e.g., "myValue".
	 * @return created language literal.
	 */
	public Literal createLiteral(String literalLabel);

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
	 * Extract RDF triples from RDF file to data unit. It expects RDF/XML
	 * serialization of RDF data
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
	 * @param file               File which contains RDF data to extract.
	 * @param format             Specifies concrete {@link RDFFormat} (e.g.,
	 *                           RDFXML, Turtle, ..) if RDF format can not be
	 *                           detected from file suffix.
	 * @param handlerExtractType Possibilies how to choose handler for data
	 *                           extraction and how to solve finded problems
	 *                           with no valid data
	 * @throws RDFException when extraction fail.
	 */
	public void addFromFile(File file, RDFFormat format,
			HandlerExtractType handlerExtractType) throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file File which contains RDF data to extract.
	 *
	 * @throws RDFException when extraction fail.
	 */
	public void addFromTurtleFile(File file) throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param file File which contains RDF data to extract.
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
	public void loadToFile(File file, RDFFormatType formatType) throws RDFException;

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
	 * Transform RDF in repository by SPARQL updateQuery.
	 *
	 * @param updateQuery String value of update SPARQL query.
	 * @throws RDFException when transformation fail.
	 */
	public void executeSPARQLUpdateQuery(String updateQuery) throws RDFException;

	/**
	 * Transform RDF in repository by SPARQL updateQuery.
	 *
	 * @param updateQuery String value of update SPARQL query.
	 * @param dataset     Set of graph URIs used for update query.
	 * @throws RDFException when transformation fault.
	 */
	public void executeSPARQLUpdateQuery(String updateQuery, Dataset dataset)
			throws RDFException;

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
	public File executeSelectQuery(String selectQuery,
			String filePath, SelectFormatType selectType)
			throws InvalidQueryException;

	/**
	 * Make select query over repository data and return MyTupleQueryResult
	 * class as result.
	 *
	 * @param selectQuery String representation of SPARQL select query.
	 * @return MyTupleQueryResult representation of SPARQL select query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	public MyTupleQueryResultIf executeSelectQueryAsTuples(
			String selectQuery) throws InvalidQueryException;

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
	 *         query.
	 * @throws InvalidQueryException when query is not valid or containst LIMIT
	 *                               or OFFSET keyword.
	 */
	public OrderTupleQueryResult executeOrderSelectQueryAsTuples(
			String orderSelectQuery) throws InvalidQueryException;

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
	public File executeConstructQuery(String constructQuery,
			RDFFormatType formatType, String filePath) throws InvalidQueryException;

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
	 * Make construct query over graph URIs in dataSet and return interface
	 * Graph as result contains iterator for statements (triples).
	 *
	 * @param constructQuery String representation of SPARQL query.
	 * @param dataSet        Set of graph URIs used for construct query.
	 * @return Interface Graph as result of construct SPARQL query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	public Graph executeConstructQuery(String constructQuery, Dataset dataSet)
			throws InvalidQueryException;

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
	 *
	 * @return instance with iterator behavior for lazy returning all triples in
	 *         repository, which are split to parts using default split value
	 *         (see {@link LazyTriples#DEFAULT_SPLIT_SIZE}).
	 */
	public LazyTriples getTriplesIterator();

	/**
	 *
	 * @param splitSize number of triples returns in each return part using
	 *                  method {@link LazyTriples#getTriples() }.
	 * @return instance with iterator behavior for lazy returning all triples in
	 *         repository, which are split to parts - each has triples at most
	 *         as defined splitSize.
	 */
	public LazyTriples getTriplesIterator(long splitSize);

	/**
	 * Copy all data from repository to targetRepository.
	 *
	 * @param targetRepo goal repository where RDF data are added.
	 */
	public void copyAllDataToTargetDataUnit(RDFDataUnit targetRepo);

	/**
	 * Returns graph contains all RDF triples as result of describe query for
	 * given Resource URI. If graph is empty, there is are no triples for
	 * describing.
	 *
	 * @param uriResource Subject or object URI as resource use to describe it.
	 * @return Graph contains all RDF triples as result of describe query for
	 *         given Resource URI. If graph is empty, there is are no triples
	 *         for describing.
	 * @throws InvalidQueryException if resource is not URI type (e.g.
	 *                               BlankNode, some type of Literal (in object
	 *                               case))
	 */
	public Graph describeURI(Resource uriResource) throws InvalidQueryException;

	/**
	 * Returns shared connection to repository.
	 *
	 * @return Shared connection to repository.
	 * @throws RepositoryException If something went wrong during the creation
	 *                             of the Connection.
	 */
	public RepositoryConnection getConnection() throws RepositoryException;

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
	 * Returns URI representation of graph where RDF data are stored.
	 *
	 * @return URI representation of graph where RDF data are stored.
	 */
	public URI getDataGraph();

	/**
	 * Create RDF parser for given RDF format and set RDF handler where data are
	 * insert to.
	 *
	 * @param format  RDF format witch is set to RDF parser
	 * @param handler Type of handler where RDF parser used for parsing.
	 * @return RDFParser for given RDF format and set RDF handler.
	 */
	public RDFParser getRDFParser(RDFFormat format, TripleCountHandler handler);

	/**
	 * Removes all RDF data from repository.
	 */
	public void cleanAllData();

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
	 * Returns count of parts in repository by defined chunkSize.
	 *
	 * @param chunkSize size of triples/statements in one part.
	 * @return Count of parts in repository by defined chunkSize.
	 */
	public long getPartsCount(long chunkSize);

	/**
	 * Method called after restarting after DB. Calling method
	 * {@link #getConnection()} provides to get new instance of connection.
	 */
	public void restartConnection();

	/**
	 * Returns dataset for graphs set in reposiotory as default.
	 *
	 * @return dataset for graphs set in reposiotory as default.
	 */
	public Dataset getDataSet();

	/**
	 * Add statements to defined graph name. Data in this graph could be used
	 * only in SPARQL queries for named graph cause. If graph graph is different
	 * from default, others methods don`t know anything about here stored
	 * triples.
	 *
	 * @param statements connection of statements you want to add
	 * @param graphName  Graph name where statements are added.
	 */
	public void addStatementsToGraph(List<Statement> statements, URI graphName);
}
