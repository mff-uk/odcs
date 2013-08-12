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
	 * @param defaultGraph String name of graph, where RDF data are saved.
	 * @return
	 */
	public static RDFDataUnit createLocal(String dataUnitName,
			String repoPath, String id, String defaultGraph) {

		RDFDataRepository repository = LocalRDFRepo.createLocalRepo(repoPath,
				id, dataUnitName);

		repository.setDataGraph(defaultGraph);

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
	 * @param defaultGraph A default graph name, used for Sesame calls, when
	 *                     contexts list is empty, exclude exportStatements,
	 *                     hasStatement, getStatements methods.
	 * @return
	 */
	public static RDFDataUnit createVirtuoso(String dataUnitName,
			String hostName, String port, String user,
			String password, String defaultGraph) {

		RDFDataRepository repository = VirtuosoRDFRepo
				.createVirtuosoRDFRepo(hostName, port, user, password,
				defaultGraph, dataUnitName);

		/*
		 * ANSWER TO PETR:
		 * Default graph we need for all. Here you dont need set data graph 
		 * (is it in constructor) - only in Vituoso case.
		 * 
		 */
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
	public void add(String namespace, String subjectName,
			String predicateName,
			String objectName) {

		repository.addTriple(namespace, subjectName, predicateName, objectName);

	}

	/**
	 * Extract triples from file and store them in DataUnit. The file type is
	 * determined by file's extension.
	 *
	 * @param file
	 */
	public void extract(File file) throws RDFDataUnitException {
		repository.extractFromFile(file);
	}

	/**
	 * Extract triples from file and store them in DataUnit.
	 *
	 * @param file   Path to the file including file name and extension.
	 * @param format File's format type.
	 */
	public void extract(File file, RDFFormat format) throws RDFDataUnitException {
		repository.extractFromFile(file, format);
	}

	/**
	 * Extract triples from file and store them in DataUnit.
	 *
	 * @param file    Path to the file including file name and extension.
	 * @param format  File's format type.
	 * @param baseURI String name of defined used URI.
	 */
	public void extract(File file, RDFFormat format, String baseURI) throws RDFDataUnitException {
		repository.extractFromFile(file, format, baseURI);
	}

	/**
	 * Query the SPARQL endpoint and add result to DataUnit.
	 *
	 * @param endpointURL
	 * @param defaultGraphUri
	 * @param query
	 */
	public void extract(URL endpointURL, String defaultGraphUri, String query)
			throws RDFDataUnitException {

		repository
				.extractfromSPARQLEndpoint(endpointURL, defaultGraphUri, query);
	}

	/**
	 * Query the SPARQL endpoint and add result to DataUnit.
	 *
	 * @param endpointURL
	 * @param defaultGraphUri
	 * @param query
	 * @param hostName
	 * @param password
	 * @param format
	 */
	public void extract(URL endpointURL, String defaultGraphUri, String query,
			String hostName, String password) throws RDFDataUnitException {

		repository
				.extractFromSPARQLEndpoint(endpointURL, defaultGraphUri, query,
				hostName, password);
	}

	/**
	 * Save all the triples in DataUnit to file.
	 *
	 * @param file       Path to the file.
	 * @param formatType File's format type.
	 */
	public void load(File file, RDFFormatType formatType) throws RDFDataUnitException {
		repository.loadToFile(file, formatType);
	}

	/**
	 * Upload all triples from DataUnit into given SPARQL endpoint with no
	 * password.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.
	 * @param graphType
	 */
	public void load(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType) throws RDFDataUnitException {
		repository.loadtoSPARQLEndpoint(endpointURL, defaultGraphURI, graphType);
	}

	/**
	 * Upload all triples from DataUnit into given SPARQL endpoint with no
	 * password.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.
	 * @param graphType
	 * @param defaultGraphURI List with names of graph where RDF data are
	 *                        loading.
	 */
	public void load(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType, List<String> endpointGraphsURI) throws RDFDataUnitException {

		repository.loadtoSPARQLEndpoint(endpointURL, endpointGraphsURI,
				graphType);
	}

	/**
	 * Upload all triples from DataUnit into given SPARQL endpoint.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.
	 * @param graphType
	 * @param login
	 * @param password
	 */
	public void load(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType, String login, String password) throws RDFDataUnitException {

		repository.loadtoSPARQLEndpoint(endpointURL, defaultGraphURI, login,
				password, graphType);
	}

	/**
	 * Upload all triples from DataUnit into given SPARQL endpoint.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.
	 * @param graphType
	 * @param defaultGraphURI List with names of graph where RDF data are
	 *                        loading.
	 * @param login
	 * @param password
	 */
	public void load(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType, List<String> endpointGraphsURI,
			String login, String password) throws RDFDataUnitException {

		repository.loadtoSPARQLEndpoint(endpointURL, endpointGraphsURI, login,
				password, graphType);
	}

	/**
	 * Transform data in DataUnit using given query.
	 *
	 * @param updateQuery
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
	 * Query the DataUnit and return result.
	 *
	 * @param selectQuery
	 * @return
	 */
	public Map<String, List<String>> query(String selectQuery) throws InvalidQueryException {
		return repository.makeSelectQueryOverRepository(selectQuery);
	}

	/**
	 * Query the DataUnit and save result into File.
	 *
	 * @param selectQuery
	 * @param file        Path to the output file.
	 * @param format      Format of output file.
	 * @return
	 */
	public File query(String selectQuery, File file, RDFFormatType format) {
		return null;
		/**
		 * TODO JIRKA - IMPLEMENTATION.
		 */
	}

	/**
	 * List all triples (Statements) in DataUnit.
	 *
	 * @see {@link RDFDataUnit#query(String)}.
	 * @return
	 */
	public List<Statement> getTriples() {
		return repository.getRepositoryStatements();
	}

	/**
	 * Return number of triples in repository.
	 *
	 * @return
	 */
	public long getSize() {
		return repository.getTripleCount();
	}

	@Override
	public void madeReadOnly() {
		repository.madeReadOnly();
	}

	@Override
	public void merge(DataUnit unit) throws IllegalArgumentException {
		repository.merge(unit);
	}

	@Override
	public void delete() {
		repository.delete();
	}

	@Override
	public void release() {
		repository.release();
	}

	@Override
	public void save(File directory) throws RuntimeException {
		repository.save(directory);
	}

	@Override
	public void load(File directory)
			throws FileNotFoundException,
			RuntimeException {
		repository.load(directory);
	}

	@Override
	public DataUnitType getType() {
		return repository.getType();
	}

	@Override
	public String getName() {
		return repository.getName();
	}

	@Override
	public boolean isReadOnly() {
		return repository.isReadOnly();
	}
}
