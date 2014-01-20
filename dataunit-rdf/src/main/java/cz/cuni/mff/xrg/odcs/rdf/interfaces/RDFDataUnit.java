package cz.cuni.mff.xrg.odcs.rdf.interfaces;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.enums.SelectFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.help.LazyTriples;

import java.io.File;
import java.util.List;
import org.openrdf.model.BNode;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.Dataset;
import org.openrdf.rio.RDFFormat;

/**
 * Provides method for working with RDF data repository.
 *
 * @author Jiri Tomes
 * @author Petyr
 *
 */
public interface RDFDataUnit extends DataUnit {

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
	 * Transform RDF in repository by SPARQL updateQuery.
	 *
	 * @param updateQuery String value of update SPARQL query.
	 * @throws RDFException when transformation fail.
	 */
	public void executeSPARQLUpdateQuery(String updateQuery) throws RDFException;

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
