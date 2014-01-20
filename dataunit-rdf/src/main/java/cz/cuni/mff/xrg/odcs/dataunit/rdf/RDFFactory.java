package cz.cuni.mff.xrg.odcs.dataunit.rdf;

import cz.cuni.mff.xrg.odcs.dataunit.rdf.data.Literal;
import cz.cuni.mff.xrg.odcs.dataunit.rdf.data.Node;
import cz.cuni.mff.xrg.odcs.dataunit.rdf.data.Triple;

/**
 * Factory for creating {@link Triple}s and related classes.
 * 
 * @author Petyr
 */
public interface RDFFactory {
	
	/**
	 * Create new empty triple and return it.
	 * 
	 * @return 
	 */
	Triple triple();
	
	/**
	 * Create new empty literal and return it.
	 * 
	 * @return 
	 */
	Literal literal();
	
	/**
	 * Create new empty node and return it.
	 * 
	 * @return 
	 */
	Node node();
	
}
