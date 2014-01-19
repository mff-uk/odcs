package cz.cuni.mff.xrg.odcs.dataunit.rdf.data;

import java.net.URI;

/**
 * Represent the value that can be used in RDF triple.
 * @author Petyr
 */
public interface Value {
	
	Object asObject();
	
	String asString();
	
	URI asURI();
	
	void set(Object newValue);
	
	void set(String newValue);
	
	void set(URI newValue);
			
}
