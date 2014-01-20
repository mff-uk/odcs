package cz.cuni.mff.xrg.odcs.dataunit.rdf.data;

/**
 * Represent a single node in RDF graph.
 * 
 * @author Petyr
 */
public interface Node extends Comparable<Node>, Value {

	/**
	 * retrieves this node's identifier.
	 *
	 * @return A blank node identifier.
	 */
	public String getID();

}
