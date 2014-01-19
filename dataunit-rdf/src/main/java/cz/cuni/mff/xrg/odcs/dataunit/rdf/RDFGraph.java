package cz.cuni.mff.xrg.odcs.dataunit.rdf;

import cz.cuni.mff.xrg.odcs.dataunit.rdf.data.Triple;
import java.io.File;
import java.util.Collection;

/**
 * Represent the RDF graph.
 * @author Petyr
 */
public interface RDFGraph extends Collection<Triple> {
	
	/**
	 * Load the content of this {@link RDFDataUnit} into given file.
	 * If the file exists then the load method fails.
	 * @param file
	 * @param type
	 * @throws RDFException 
	 */
	void load(File file, RDFFileType type) throws RDFException;
		
}
