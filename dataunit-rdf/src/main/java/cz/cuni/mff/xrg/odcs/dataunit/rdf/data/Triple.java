package cz.cuni.mff.xrg.odcs.dataunit.rdf.data;

import java.net.URI;

/**
 * Represent the single RDF Triple.
 * @author Petyr
 */
public interface Triple {
	
	Value getSubject();
	
	URI getPredicate();
	
	Value getObject();

	void getSubject(Value newSubject);
	
	void getPredicate(URI newPredicate);
	
	void getObject(Value newObject);
		
}
