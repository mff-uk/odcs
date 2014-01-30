package cz.cuni.mff.xrg.odcs.dataunit.rdf.model;

import cz.cuni.mff.xrg.odcs.dataunit.rdf.RDFFileType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFDataUnitException;
import java.io.File;
import java.util.Collection;

/**
 * Represent the RDF graph.
 * @author Petyr
 */
public interface Graph extends Collection<Statement> {
	
	/**
	 * Load the content of this {@link RDFDataUnit} into given file.
	 * If the file exists then the load method fails.
	 * @param file
	 * @param type
	 * @throws RDFDataUnitException 
	 */
	void load(File file, RDFFileType type) throws RDFDataUnitException;
	
	/**
	 * Execute query over graph and return result.
	 * 
	 * @param query
	 * @return 
	 */
	Graph execute(String query);
	
}