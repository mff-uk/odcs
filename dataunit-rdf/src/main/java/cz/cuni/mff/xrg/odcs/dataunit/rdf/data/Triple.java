package cz.cuni.mff.xrg.odcs.dataunit.rdf.data;

import java.net.URI;

/**
 * Represent the single RDF Triple.
 * 
 * @author Petyr
 */
public interface Triple {
	
	/**
	 * Current value of subject.
	 * 
	 * @return 
	 */
	Node s();
	
	/**
	 * Current value of predicate. 
	 * 
	 * @return 
	 */
	URI p();
	
	/**
	 * Current value of object.
	 * 
	 * @return 
	 */
	Value o();

	/**
	 * Set subject to given value and return this instance.
	 * 
	 * @param newSubject
	 * @return 
	 */
	Triple s(Node newSubject);
	
	/**
	 * Set predicate to given value and return this instance.
	 * 
	 * @param newPredicate
	 * @return 
	 */
	Triple p(URI newPredicate);
	
	/**
	 * Set object to given value and return this instance.
	 * 
	 * @param newObject {@link Literal} or {@link Node}.
	 * @return 
	 */
	Triple o(Value newObject);
		
}
