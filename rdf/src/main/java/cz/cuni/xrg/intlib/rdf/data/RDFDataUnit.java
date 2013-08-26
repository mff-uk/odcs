package cz.cuni.xrg.intlib.rdf.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Statement;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.rdf.enums.FileExtractType;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;
import cz.cuni.xrg.intlib.rdf.enums.WriteGraphType;
import cz.cuni.xrg.intlib.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.rdf.exceptions.InvalidQueryException;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFDataUnitException;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.impl.VirtuosoRDFRepo;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;
import org.openrdf.model.Graph;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.rio.RDFFormat;

/**
 * RDF DataUnit. For Load, Transform, Query. In case of problem throws
 * RDFDataUnitException.
 *
 * @author Jiri Tomes
 * @author Petyr
 *
 */
public class RDFDataUnit implements DataUnit {

	private RDFDataRepository repository;

	/**
	 * Create RDFDataUnit as local RDF repository.
	 *
	 * @param dataUnitName DataUnit's name. If not used in Pipeline can be empty
	 *                     String.
	 * @param repoPath     String path to directory where can be repository
	 *                     stored.
	 * @param id           String file name - unique ID, where is repository in
	 *                     directory stored.
	 * @param namedGraph   String name of graph, where RDF data are saved.
	 * @return
	 */
	public static RDFDataUnit createLocal(String dataUnitName,
			String repoPath, String id, String namedGraph) {

		RDFDataRepository repository = LocalRDFRepo.createLocalRepo(repoPath,
				id, dataUnitName);

		repository.setDataGraph(namedGraph);

		return new RDFDataUnit(repository);
	}

	/**
	 * Create RDFDataUnit ad new instance of VirtuosoRepository as storage.
	 *
	 * @param dataUnitName DataUnit's name. If not used in Pipeline can be empty
	 *                     String.
	 * @param hostName     String name of host need to Virtuoso connection.
	 * @param port         String value of number of port need for connection to
	 *                     Virtuoso.
	 * @param user         A default graph name, used for Sesame calls, when
	 *                     contexts list is empty, exclude exportStatements,
	 *                     hasStatement, getStatements methods.
	 * @param password     The user's password.
	 * @param namedGraph   A default graph name, used for Sesame calls, when
	 *                     contexts list is empty, exclude exportStatements,
	 *                     hasStatement, getStatements methods.
	 * @return
	 */
	public static RDFDataUnit createVirtuoso(String dataUnitName,
			String hostName, String port, String user,
			String password, String namedGraph) {

		RDFDataRepository repository = VirtuosoRDFRepo
				.createVirtuosoRDFRepo(hostName, port, user, password,
				namedGraph, dataUnitName);

		return new RDFDataUnit(repository);
	}

	/**
	 * Create wrap-RDFDataUnit over given repository.
	 *
	 * @param repository
	 */
	protected RDFDataUnit(RDFDataRepository repository) {
		this.repository = repository;
	}

//	/**
//	 * Add one tripple RDF (statement) to the repository. There is common namespace for subject, predicate, object. 
//	 *
//	 * @param commonNamespace     Common namespace for subject, predicate, object
//	 * @param subject   Subject (relative)
//	 * @param predicate Predicate
//	 * @param object    Object
//	 */
//	public void addRDFTriple(String commonNamespace, String subject,
//			String predicate,
//			String object) {
//
//		repository.addTriple(commonNamespace, subject, predicate, object);
//
//	}
	/**
	 * Add one RDF triple to the repository.
	 *
	 * @param subject   Subject
	 * @param predicate Predicate
	 * @param object    Object
	 */
	public void addTriple(String subject,
			String predicate, String object) {

		repository.addTriple(subject, predicate, object);
	}

	/**
	 * Extract triples from file and store them in DataUnit. The file type is
	 * determined by file's extension.
	 *
	 * @param file File contains RDF data to extract.
	 * @throws RDFDataUnitException when extraction fail.
	 */
	public void addTriplesFromFile(File file) throws RDFDataUnitException {
		repository.extractFromFile(file);
	}

	/**
	 * Extract triples from file and store them in DataUnit.
	 *
	 * @param file   File contains RDF data to extract.
	 * @param format Specifies concrete {@link RDFFormat} (e.g., RDFXML, Turtle,
	 *               ..) if RDF format can not be detected from file suffix.
	 * @throws RDFDataUnitException when extraction fail.
	 */
	public void addTriplesFromFile(File file, RDFFormat format) throws RDFDataUnitException {
		repository.extractFromFile(file, format);
	}

	/**
	 * Extract triples from file and store them in DataUnit.
	 *
	 * @param file    File contains RDF data to extract.
	 * @param format  Specifies concrete {@link RDFFormat} (e.g., RDFXML,
	 *                Turtle, ..) if RDF format can not be detected from file
	 *                suffix.
	 * @param baseURI String name of defined URI prefix namespace used by adding
	 *                new triple statement as united prefix. If this string is
	 *                empty, is needed to use whole URI name for each element of
	 *                new added triple.
	 * @throws RDFDataUnitException when extraction fail.
	 */
	public void addTriplesFromFile(File file, RDFFormat format, String baseURI)
			throws RDFDataUnitException {
		repository.extractFromFile(file, format, baseURI);
	}

	/**
	 * Extract RDF triples from RDF file and store them in DataUnit.
	 *
	 * @param extractType         One of defined enum type for extraction data
	 *                            from file.
	 * @param path                String path to file/directory
	 * @param suffix              String suffix of fileName (example: ".ttl",
	 *                            ".xml", etc)
	 * @param baseURI             String name of defined URI prefix namespace
	 *                            used by adding new triple statement as united
	 *                            prefix. If this string is empty, is needed to
	 *                            use whole URI name for each element of new
	 *                            added triple.
	 * @param useSuffix           boolean value, if extract files only with
	 *                            defined suffix or not.
	 * @param useStatisticHandler boolean value if detailed log and statistic
	 *                            are awailable or not.
	 * @throws RDFDataUnitException when extraction fail.
	 */
	public void addTriplesFromFile(FileExtractType extractType,
			String path, String suffix,
			String baseURI, boolean useSuffix, boolean useStatisticHandler)
			throws RDFDataUnitException {

		repository.extractFromFile(extractType, path, suffix, baseURI,
				useSuffix, useStatisticHandler);
	}

	/**
	 * Extract RDF triples from RDF file and store them in DataUnit.
	 *
	 * @param file                  File contains RDF data to extract.
	 * @param format                Specifies concrete {@link RDFFormat} (e.g.,
	 *                              RDFXML, Turtle, ..) if RDF format can not be
	 *                              detected from file suffix.
	 * @param baseURI               String name of defined URI prefix namespace
	 *                              used by adding new triple statement as
	 *                              united prefix. If this string is empty, is
	 *                              needed to use whole URI name for each
	 *                              element of new added triple.
	 * @param useStatisticalHandler boolean value, if during extraction needed
	 *                              detail statistic about RDF triples and
	 *                              detailed log or not.
	 * @throws RDFException when extraction fail.
	 */
	public void addTriplesFromFile(File file, RDFFormat format, String baseURI,
			boolean useStatisticalHandler) throws RDFDataUnitException {
		repository.extractFromFile(file, format, baseURI, useStatisticalHandler);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to DataUnit using only data from
	 * URI graph without authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param namedGraph      name of graph where RDF data are stored.
	 * @param makeSelectQuery String SPARQL makeSelectQuery.
	 * @throws RDFDataUnitException when extraction data from SPARQL endpoint
	 *                              fail.
	 */
	public void addTriplesFromSPARQLEndpoint(URL endpointURL,
			String namedGraph, String query)
			throws RDFDataUnitException {

		repository.extractfromSPARQLEndpoint(endpointURL, namedGraph, query);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to DataUnit using only data from
	 * URI graph using authentication (name,password).
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param namedGraph      name of graph where RDF data are stored.
	 * @param makeSelectQuery String SPARQL makeSelectQuery.
	 * @param hostName        String name needed for authentication.
	 * @param password        String password needed for authentication.
	 * @throws RDFDataUnitException when extraction data from SPARQL endpoint
	 *                              fail.
	 */
	public void addTriplesFromSPARQLEndpoint(URL endpointURL,
			String namedGraph, String query,
			String hostName, String password) throws RDFDataUnitException {

		repository.extractFromSPARQLEndpoint(endpointURL, namedGraph, query,
				hostName, password);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to DataUnit using only data from
	 * collection of URI graphs using authentication (name,password).
	 *
	 * @param endpointURL         Remote URL connection to SPARQL endpoint
	 *                            contains RDF data.
	 * @param namedGraphs         List with names of graph where RDF data are
	 *                            stored.
	 * @param query               String SPARQL query.
	 * @param hostName            String name needed for authentication.
	 * @param password            String password needed for authentication.
	 * @param format              Type of RDF format for saving data (example:
	 *                            TURTLE, RDF/XML,etc.)
	 * @param useStatisticHandler boolean value if detailed log and statistic
	 *                            are awailable or not.
	 * @throws RDFDataUnitException when extraction data from SPARQL endpoint
	 *                              fail.
	 */
	public void addTriplesFromSPARQLEndpoint(URL endpointURL,
			List<String> namedGraphs,
			String query, String hostName, String password, RDFFormat format,
			boolean useStatisticHandler) throws RDFDataUnitException {

		repository.extractFromSPARQLEndpoint(endpointURL, namedGraphs,
				query, hostName, password, format, useStatisticHandler);
	}

	/**
	 * Save all triples in DataUnit to defined file in defined RDF format.
	 *
	 * @param file       File where data be saved.
	 * @param formatType Type of RDF format for saving data (example: TURTLE,
	 *                   RDF/XML,etc.)
	 * @throws RDFDataUnitException when saving data to file fail.
	 *
	 */
	public void saveTriplesToFile(File file, RDFFormatType formatType) throws RDFDataUnitException {
		repository.loadToFile(file, formatType);
	}

	/**
	 * Save all triples in DataUnit to defined file in defined RDF format.
	 *
	 * @param directoryPath    Path to directory, where file with RDF data will
	 *                         be saved.
	 * @param fileName         Name of file for saving RDF data.
	 * @param formatType       Type of RDF format for saving data (example:
	 *                         TURTLE, RDF/XML,etc.)
	 * @param canFileOverWrite boolean value, if existing file can be
	 *                         overwritten.
	 * @param isNameUnique     boolean value, if every pipeline execution has
	 *                         his unique name.
	 * @throws CannotOverwriteFileException when file is protected for
	 *                                      overwritting.
	 * @throws RDFDataUnitException         when loading data to file fail.
	 */
	public void saveTriplesToFile(String directoryPath,
			String fileName, RDFFormatType formatType,
			boolean canFileOverWrite, boolean isNameUnique) throws CannotOverwriteFileException, RDFDataUnitException {

		repository.loadToFile(directoryPath, fileName, formatType,
				canFileOverWrite, isNameUnique);
	}

	/**
	 * Upload all data from DataUnit into given SPARQL endpoint to the one URI
	 * graph without endpoint authentication.
	 *
	 * @param endpointURL Remote URL connection to SPARQL endpoint contains RDF
	 *                    data.
	 * @param namedGraph  name of graph where RDF data are loading.
	 * @param graphType   One of way, how to solve loading RDF data to graph
	 *                    when is it is not empty (MERGE, OVERRIDE, FAIL).
	 * @throws RDFDataUnitException when loading data to SPARQL endpoint fail.
	 */
	public void loadToSPARQLEndpoint(URL endpointURL, String namedGraph,
			WriteGraphType graphType) throws RDFDataUnitException {
		repository.loadtoSPARQLEndpoint(endpointURL, namedGraph, graphType);
	}

	/**
	 * Upload all data from DataUnit into given SPARQL endpoint to the one URI
	 * graph with endpoint authentication (name,password).
	 *
	 * @param endpointURL Remote URL connection to SPARQL endpoint contains RDF
	 *                    data.
	 * @param namedGraph  name of graph where RDF data are loading.
	 * @param login       String name needed for authentication.
	 * @param password    String password needed for authentication.
	 * @param graphType   One of way, how to solve loading RDF data to graph
	 *                    when is it is not empty (MERGE, OVERRIDE, FAIL).
	 * @throws RDFDataUnitException when loading data to SPARQL endpoint fail.
	 */
	public void loadToSPARQLEndpoint(URL endpointURL, String namedGraph,
			String login, String password, WriteGraphType graphType) throws RDFDataUnitException {

		repository.loadtoSPARQLEndpoint(endpointURL, namedGraph, login,
				password, graphType);
	}

	/**
	 * Upload all data from DataUnit into given SPARQL endpoint to the
	 * collection of URI graphs without endpoint authentication.
	 *
	 * @param endpointURL       Remote URL connection to SPARQL endpoint
	 *                          contains RDF data.
	 * @param endpointGraphsURI List with names of graph where RDF data are
	 *                          loading.
	 * @param graphType         One of way, how to solve loading RDF data to
	 *                          graph when is it is not empty (MERGE, OVERRIDE,
	 *                          FAIL).
	 * @throws RDFDataUnitException when loading data to SPARQL endpoint fail.
	 */
	public void loadToSPARQLEndpoint(URL endpointURL,
			List<String> endpointGraphsURI, WriteGraphType graphType) throws RDFDataUnitException {

		repository.loadtoSPARQLEndpoint(endpointURL, endpointGraphsURI,
				graphType);
	}

	/**
	 * Upload all data from DataUnit into given SPARQL endpoint to the
	 * collection of URI graphs with endpoint authentication (name,password).
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI List with names of graph where RDF data are
	 *                        loading.
	 * @param login           String name needed for authentication.
	 * @param password        String password needed for authentication.
	 * @param graphType       One of way, how to solve loading RDF data to graph
	 *                        when is it is not empty (MERGE, OVERRIDE, FAIL).
	 * @throws RDFDataUnitException when loading data to SPARQL endpoint fail.
	 */
	public void loadToSPARQLEndpoint(URL endpointURL,
			List<String> endpointGraphsURI,
			String login, String password, WriteGraphType graphType) throws RDFDataUnitException {

		repository.loadtoSPARQLEndpoint(endpointURL, endpointGraphsURI, login,
				password, graphType);
	}

	/**
	 * Transform RDF data in DataUnit using SPARQL given makeSelectQuery.
	 *
	 * @param updateQuery SPARQL Update query
	 * @throws RDFDataUnitException when transformation fail.
	 *
	 */
	public void transform(String updateQuery) throws RDFDataUnitException {
		repository.transformUsingSPARQL(updateQuery);
	}

	/**
	 * Delete all the triples in DataUnit.
	 */
	public void clear() {
		repository.cleanAllData();
	}

	/**
	 * Executes SPARQL select query over RDF data in DataUnit.
	 *
	 * @param selectQuery String representation of SPARQL makeSelectQuery.
	 * @return <code>Map&lt;String,List&lt;String&gt;&gt;</code> as table, where
	 *         map key is column name and <code>List&lt;String&gt;</code> are
	 *         string values in this column. When makeSelectQuery is invalid,
	 *         return * empty <code>Map</code>.
	 * @throws InvalidQueryException when makeSelectQuery is not valid.
	 */
	public Map<String, List<String>> executeSelectQuery(String selectQuery)
			throws InvalidQueryException {
		return repository.makeSelectQueryOverRepository(selectQuery);
	}

	/**
	 * Execute SPARQL query over RDF data in DataUnit.
	 *
	 * @param selectQuery String representation of SPARQL makeSelectQuery
	 * @param filePath    String path to file for saving result of
	 *                    makeSelectQuery in SPARQL XML syntax.
	 * @return File contains result of given SPARQL select query.
	 * @throws InvalidQueryException when makeSelectQuery is not valid.
	 */
	public File executeSelectQuery(String selectQuery, String filePath) throws InvalidQueryException {
		return repository.makeSelectQueryOverRepository(selectQuery, filePath);

	}

	/**
	 * Make select query over RDF data in DataUnit and return TupleQueryResult
	 * interface as result.
	 *
	 * @param selectQuery String representation of SPARQL select query.
	 * @return TupleQueryResult representation of SPARQL select query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	public TupleQueryResult executeSelectQueryTupleQueryResult(
			String selectQuery) throws InvalidQueryException {
		return repository.makeSelectQueryOverRepositoryAsResult(selectQuery);
	}

	/**
	 * Make construct query over RDF data in DataUnit and return file where RDF
	 * data as result are saved.
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
	public File executeConstructQuery(String constructQuery, String filePath,
			RDFFormatType formatType) throws InvalidQueryException {

		return repository.makeConstructQueryOverRepository(constructQuery,
				formatType, filePath);
	}

	/**
	 * Make construct query over RDF data in DataUnit and return interface Graph
	 * as result contains iterator for statements (triples).
	 *
	 * @param constructQuery String representation of SPARQL query.
	 * @return Interface Graph as result of construct SPARQL query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	public Graph executeConstructQuery(String constructQuery) throws InvalidQueryException {
		return repository.makeConstructQueryOverRepository(constructQuery);
	}

	/**
	 * Get list of all RDF triples (Statements) in DataUnit.
	 *
	 * @see {@link RDFDataUnit#makeSelectQuery(String)}.
	 * @return list of RDF triples
	 */
	public List<Statement> getTriples() {
		return repository.getRepositoryStatements();
	}

	/**
	 * Return number of triples in repository.
	 *
	 * @return number of triples - size of DataUnit.
	 */
	public long getSize() {
		return repository.getTripleCount();
	}

	/**
	 * Made this DataUnit read-only. This instance will be used as a input for
	 * some DPU.
	 */
	@Override
	public void madeReadOnly() {
		repository.madeReadOnly();
	}

	/**
	 * Merge (add) data from given DataUnit into this DataUnit. If the unit has
	 * wrong type then the {@link IllegalArgumentException} should be thrown.
	 * The method must not modify the content parameter (unit). The given
	 * DataUnit is not in read-only mode.
	 *
	 * @param unit {@link DataUnit} to merge with
	 * @throws {@link IllegalArgumentException} In case of unsupported unit
	 *                                          type.
	 */
	@Override
	public void merge(DataUnit unit) throws IllegalArgumentException {
		repository.merge(unit);
	}

	/**
	 * Delete all data/file/resources related to the DataUnit. Can be called
	 * even when the DataUnit is in read only mode. Can't be called before of
	 * after {@link #release()}
	 */
	@Override
	public void delete() {
		repository.delete();
	}

	/**
	 * Release all locks, prepare for destroy in memory representation of
	 * DataUnit. Can be called even when the DataUnit is in read only mode.
	 * Can't be called before of after {@link #delete()}
	 */
	@Override
	public void release() {
		repository.release();
	}

	/**
	 * Save DataUnit context into given directory. In case of any problem throws
	 * exception. The directory doesn't have to exist.
	 *
	 * @throws RuntimeException
	 */
	@Override
	public void save(File directory) throws RuntimeException {
		repository.save(directory);
	}

	/**
	 * Save DataUnit context into given directory. The directory doesn't have to
	 * exist. The directory can be the same as the DataUnit working directory!
	 *
	 * @throws RuntimeException is loading context failed.
	 */
	@Override
	public void load(File directory)
			throws FileNotFoundException,
			RuntimeException {
		repository.load(directory);
	}

	/**
	 * Return type of data unit interface implementation.
	 *
	 * @return DataUnit type.
	 */
	@Override
	public DataUnitType getType() {
		return repository.getType();
	}

	/**
	 * Return dataUnit's name. The DataUnit name should be set in constructor.
	 *
	 * @return String name of data unit.
	 */
	@Override
	public String getName() {
		return repository.getName();
	}

	/**
	 * Return true if DataUnit is in read only state.
	 *
	 * @see {@link #madeReadOnly}
	 * @return True if data in DataUnit are read only, false otherwise.
	 */
	@Override
	public boolean isReadOnly() {
		return repository.isReadOnly();
	}
}
