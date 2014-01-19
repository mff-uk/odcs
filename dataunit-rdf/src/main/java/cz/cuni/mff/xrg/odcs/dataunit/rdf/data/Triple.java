package cz.cuni.mff.xrg.odcs.dataunit.rdf.data;

/**
 * Represent the single RDF Triple.
 * @author Petyr
 */
public interface Triple {
	
	Value getSubject();
	
	Value getPredicate();
	
	Value getObject();

	void getSubject(Value newSubject);
	
	void getPredicate(Value newPredicate);
	
	void getObject(Value newObject);
		
}
