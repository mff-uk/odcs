package cz.cuni.mff.xrg.odcs.dataunit.rdf.model;

import java.net.URI;

/**
 * Represent the single RDF statement.
 * 
 * @author Petyr
 */
public interface Statement {
	
	/**
	 * Current value of subject.
	 * @return 
	 */
	Value s();
	
	/**
	 * Current value of predicate. 
	 * @return 
	 */
	URI p();
	
	/**
	 * Current value of object.
	 * @return 
	 */
	Value o();

	/**
	 * Set subject to given value and return this instance.
	 * @param newSubject
	 * @return 
	 */
	Statement s(Value newSubject);
	
	/**
	 * Set predicate to given value and return this instance.
	 * @param newPredicate
	 * @return 
	 */
	Statement p(URI newPredicate);
	
	/**
	 * Set object to given value and return this instance.
	 * @param newObject
	 * @return 
	 */
	Statement o(Value newObject);
		
}