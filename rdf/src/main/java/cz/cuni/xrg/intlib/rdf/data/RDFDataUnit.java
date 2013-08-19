package cz.cuni.xrg.intlib.rdf.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Statement;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;
import cz.cuni.xrg.intlib.rdf.enums.WriteGraphType;
import cz.cuni.xrg.intlib.rdf.exceptions.InvalidQueryException;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFDataUnitException;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.impl.VirtuosoRDFRepo;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;
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

	/**
	 * Add one tripple RDF (statement) to the repository.
	 *
	 * @param namespace     String name of defined namespace
	 * @param subjectName   String name of subject
	 * @param predicateName String name of predicate
	 * @param objectName    String name of object
	 */
	public void addRDFTriple(String namespace, String subjectName,
			String predicateName,
			String objectName) {

		repository.addTriple(namespace, subjectName, predicateName, objectName);

	}

	/**
	 * Add one tripple RDF (statement) to the repository (default empty
	 * namespace).
	 *
	 * @param subjectName   String name of subject
	 * @param predicateName String name of predicate
	 * @param objectName    String name of object
	 */
	public void addRDFTriple(String subjectName,
			String predicateName, String objectName) {

		repository.addTriple(subjectName, predicateName, objectName);
	}

	/**
	 * Extract triples from file and store them in DataUnit. The file type is
	 * determined by file's extension.
	 *
	 * @param file File contains RDF data to extract.
	 * @throws RDFDataUnitException when extraction fail.
	 */
	public void extractRDFFromFile(File file) throws RDFDataUnitException {
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
	public void extractRDFFromFile(File file, RDFFormat format) throws RDFDataUnitException {
		repository.extractFromFile(file, format);
	}

	/**
	 * Extract triples from file and store them in DataUnit.
	 *
	 * @param file    File contains RDF data to extract.
	 * @param format  Specifies concrete {@link RDFFormat} (e.g., RDFXML,
	 *                Turtle, ..) if RDF format can not be detected from file
	 *                suffix.
	 * @param baseURI String name of defined used URI prefix namespace used by
	 *                all triples.
	 * @throws RDFDataUnitException when extraction fail.
	 */
	public void extractRDFFromFile(File file, RDFFormat format, String baseURI)
			throws RDFDataUnitException {
		repository.extractFromFile(file, format, baseURI);
	}

	/**
	 * Extract RDF triples from RDF file and store them in DataUnit.
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
	public void extractRDFFromSPARQLEndpoint(URL endpointURL,
			String namedGraph, String query)
			throws RDFDataUnitException {

		repository
				.extractfromSPARQLEndpoint(endpointURL, namedGraph, query);
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
	public void extractRDFFromSPARQLEndpoint(URL endpointURL,
			String namedGraph, String query,
			String hostName, String password) throws RDFDataUnitException {

		repository
				.extractFromSPARQLEndpoint(endpointURL, namedGraph, query,
				hostName, password);
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
	public void loadRDFToFile(File file, RDFFormatType formatType) throws RDFDataUnitException {
		repository.loadToFile(file, formatType);
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
	 * @param updateQuery String value of update SPARQL makeSelectQuery.
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
	 * Make select makeSelectQuery over RDF data in DataUnit and return tables
	 * as result.
	 *
	 * @param selectQuery String representation of SPARQL makeSelectQuery.
	 * @return <code>Map&lt;String,List&lt;String&gt;&gt;</code> as table, where
	 *         map key is column name and <code>List&lt;String&gt;</code> are
	 *         string values in this column. When makeSelectQuery is invalid,
	 *         return * empty <code>Map</code>.
	 * @throws InvalidQueryException when makeSelectQuery is not valid.
	 */
	public Map<String, List<String>> makeSelectQuery(String selectQuery) throws InvalidQueryException {
		return repository.makeSelectQueryOverRepository(selectQuery);
	}

	/**
	 * Make select makeSelectQuery over RDF data in DataUnit and return file as
	 * SPARQL XML result.
	 *
	 * @param selectQuery String representation of SPARQL makeSelectQuery
	 * @param filePath    String path to file for saving result of
	 *                    makeSelectQuery in SPARQL XML syntax.
	 * @return File contains result of given SPARQL select makeSelectQuery.
	 * @throws InvalidQueryException when makeSelectQuery is not valid.
	 */
	public File makeSelectQuery(String selectQuery, String filePath) throws InvalidQueryException {
		return repository.makeSelectQueryOverRepository(selectQuery, filePath);

	}

	/**
	 * Make construct query over repository RDF data in DataUnit and return file
	 * where RDF data as result are saved.
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
	public File makeConstructQuery(String constructQuery, String filePath,
			RDFFormatType formatType) throws InvalidQueryException {

		return repository.makeConstructQueryOverRepository(constructQuery,
				formatType, filePath);
	}

	/**
	 * Get list all RDF triples (Statements) in DataUnit.
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
	 * exception. The directory doesn't have to exist. The directory can be the
	 * same as the DataUnit working directory!
	 *
	 * @throws RuntimeException
	 */
	@Override
	public void save(File directory) throws RuntimeException {
		repository.save(directory);
	}

	/**
	 * Save DataUnit context into given directory. In case of any problem throws
	 * exception. The directory doesn't have to exist. The directory can be the
	 * same as the DataUnit working directory!
	 *
	 * @throws RuntimeException
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
