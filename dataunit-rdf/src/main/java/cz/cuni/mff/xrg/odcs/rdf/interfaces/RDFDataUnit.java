package cz.cuni.mff.xrg.odcs.rdf.interfaces;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.Dataset;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.enums.SelectFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.handlers.TripleCountHandler;
import cz.cuni.mff.xrg.odcs.rdf.help.OrderTupleQueryResult;

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
    @Deprecated
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
    @Deprecated
	public Map<String, List<String>> getRDFMetadataForFile(String filePath,
			List<String> predicates);

	/**
	 * Transform RDF in repository by SPARQL updateQuery.
	 *
	 * @param updateQuery String value of update SPARQL query.
	 * @throws RDFException when transformation fail.
	 */
    //TODO move to rdf transformer
    @Deprecated
	public void executeSPARQLUpdateQuery(String updateQuery) throws RDFException;

	/**
	 * Transform RDF in repository by SPARQL updateQuery.
	 *
	 * @param updateQuery String value of update SPARQL query.
	 * @param dataset     Set of graph URIs used for update query.
	 * @throws RDFException when transformation fault.
	 */
    //TODO move to rdf transformer
    @Deprecated
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
    //TODO move to rdf transformer
    @Deprecated
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
    @Deprecated
	public TupleQueryResult executeSelectQueryAsTuples(
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
    @Deprecated
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
    //TODO move to rdf transformer ?
    @Deprecated
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
    //TODO move to rdf transformer ?
    @Deprecated
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
    //TODO move to rdf transformer ?
    @Deprecated
	public Graph executeConstructQuery(String constructQuery, Dataset dataSet)
			throws InvalidQueryException;

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
    @Deprecated
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
	 * Returns URI representation of graph where RDF data are stored.
	 *
	 * @return URI representation of graph where RDF data are stored.
	 */
	public URI getDataGraph();

}
