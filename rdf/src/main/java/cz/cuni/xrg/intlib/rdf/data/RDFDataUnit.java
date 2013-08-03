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
		
	}
	
	/**
	 * Extract triples from string.
	 * 
	 * @param data
	 */
	public void extract(String data) {
		
	}
	
	/**
	 * Extract triples from file and store them in DataUnit. The file type
	 * is determined by file's extension. 
	 * 
	 * @param file
	 */
	public void extract(File file) {
		
	}	
	
	/**
	 * Extract triples from file and store them in DataUnit.
	 * 
	 * @param file Path to the file including file name and extension.
	 * @param formatType File's format type.
	 */
	public void extract(File file, RDFFormatType formatType) {
		
	}
	
	/**
	 * Extract triples from file and store them in DataUnit.
	 * 
	 * @param file Path to the file including file name and extension.
	 * @param formatType File's format type.
	 * @param uri  String name of defined used URI.
	 */
	public void extract(File file, RDFFormatType formatType, String uri) {
		
	}	
	
	/**
	 * Query the SPARQL endpoint and add result to DataUnit.
	 * @param endpointURL
	 * @param defaultGraphUri
	 * @param query
	 */
	public void extract(URL endpointURL, String defaultGraphUri, String query) {
		
	}
	
	/**
	 * Query the SPARQL endpoint and add result to DataUnit. 
	 * @param endpointURL
	 * @param defaultGraphUri
	 * @param query
	 * @param hostName
	 * @param password
	 * @param format
	 */
	public void extract(URL endpointURL, String defaultGraphUri, String query,
			String hostName, String password) {
		
	}
	
	/**
	 * Save all the triples in DataUnit to file.
	 * 
	 * @param file Path to the file.
	 * @param formatType File's format type.
	 */
	public void load(File file, RDFFormatType formatType) {
		
	}
	
	/**
	 * Upload all triples from DataUnit into given SPARQL endpoint with 
	 * no password.
	 * 	 
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.	
	 * @param graphType 
	 */
	public void load(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType) {
		
	}
	
	/**
	 * Upload all triples from DataUnit into given SPARQL endpoint with 
	 * no password.
	 * 	 
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.	
	 * @param graphType 
	 * @param defaultGraphURI List with names of graph where RDF data are loading.
	 */
	public void load(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType, List<String> endpointGraphsURI) {
		
	}	
	
	/**
	 * Upload all triples from DataUnit into given SPARQL endpoint.
	 * 	 
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.	
	 * @param graphType 
	 * @param login
	 * @param password
	 */
	public void load(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType, String login, String password) {
		
	}	
	
	/**
	 * Upload all triples from DataUnit into given SPARQL endpoint.
	 * 	 
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.	
	 * @param graphType 
	 * @param defaultGraphURI List with names of graph where RDF data are loading.
	 * @param login
	 * @param password
	 */
	public void load(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType, List<String> endpointGraphsURI,
			String login, String password) {
		
	}		
	
	/**
	 * Transform data in DataUnit using given query.
	 * @param updateQuery
	 */
	public void transform(String updateQuery) {
		
	}
	
	/**
	 * Delete all the triples in DataUnit.
	 */
	public void clear() {
		
	}
		
	/**
	 * Query the DataUnit and return result.
	 * @param selectQuery
	 * @return
	 */
	public Map<String, List<String>> query(String selectQuery) {
		return null;
	}
	
	/**
	 * Query the DataUnit and save result into File.
	 * @param selectQuery
	 * @param file Path to the output file.
	 * @param format Format of output file.
	 * @return 
	 */
	public File query(String selectQuery, File file, RDFFormatType format) {
		return null;
	}	
	
	/**
	 * List all triples (Statements) in DataUnit.
	 * @see {@link RDFDataUnit#query(String)}.
	 * @return
	 */
	public List<Statement> getTriples() {
		return null;
	}
	
	/**
	 * Return number of triples in repository.
	 * @return
	 */
	public long getSize() {
		return (long)0;
	}
	
	//						DataUnit functionality							///
	
	@Override
	public void madeReadOnly() {
	}

	@Override
	public void merge(DataUnit unit) throws IllegalArgumentException {
	}

	@Override
	public void delete() {
	}	
	
	@Override
	public void release() {
	}

	@Override
	public void save(File directory) throws RuntimeException {
	}

	@Override
	public void load(File directory)
			throws FileNotFoundException,
				RuntimeException {
	}

	@Override
	public DataUnitType getType() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

}
