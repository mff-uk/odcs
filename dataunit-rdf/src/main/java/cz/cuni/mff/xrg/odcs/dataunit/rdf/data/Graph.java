package cz.cuni.mff.xrg.odcs.dataunit.rdf.data;

import cz.cuni.mff.xrg.odcs.dataunit.rdf.RDFException;
import cz.cuni.mff.xrg.odcs.dataunit.rdf.RDFFileType;
import java.io.File;
import java.util.Collection;

/**
 * Represents the RDF graph.
 * 
 * @author Petyr
 */
public interface Graph extends Collection<Triple> {
	
	/**
	 * Load the content of this {@link RDFDataUnit} into given file.
	 * If the file exists then the load method fails.
	 * 
	 * @param file
	 * @param type
	 * @throws RDFException 
	 */
	void load(File file, RDFFileType type) throws RDFException;
		
}
