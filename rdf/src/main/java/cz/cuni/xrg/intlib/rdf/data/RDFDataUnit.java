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

/*
 * For Load, Transform, Query exception I suggest to use base exception RDFDataUnitException .. 
 * and then inherit more specific exception.  
 */
/**
 * RDF DataUnit.
 *
 * @author Petyr
 *
 */
public class RDFDataUnit implements DataUnit {

	private RDFDataRepository repository;

	// TODO Jirka(from Petr): Could you please update the comments for createLocalRepo
	// I do not fully understand the meaning of repoPath and fileName
	// and add comments to this method as well
	/**
	 * Create RDFDataUnit as local repository.
	 * @param dataUnitName DataUnit's name.
	 * @param repoPath
	 * @param fileName
	 * @return
	 */
	public static RDFDataUnit createLocal(String dataUnitName,
			String repoPath, String fileName) {
		
		RDFDataRepository repository = LocalRDFRepo.createLocalRepo(repoPath, 
				fileName, dataUnitName);
		
		return new RDFDataUnit(repository);
	}
	
	/**
	 * Create RDFDataUnit that use given Virtuoso as storage.
	 * @param dataUnitName DataUnit's name.
	 * @param hostName Host name for Virtuoso.
	 * @param port Virutuoso's port.
	 * @param user User name.
	 * @param password Password.
	 * @param defaultGraph Default graph.
	 * @return
	 */
	public static RDFDataUnit createVirtuoso(String dataUnitName,
			String hostName, String port, String user, 
			String password, String defaultGraph) {
		
		RDFDataRepository repository = VirtuosoRDFRepo
				.createVirtuosoRDFRepo(hostName, port, user, password,
				defaultGraph, dataUnitName);
		
		return new RDFDataUnit(repository);
	}
	
	/**
	 * Create wrap-RDFDataUnit over given repository.
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
