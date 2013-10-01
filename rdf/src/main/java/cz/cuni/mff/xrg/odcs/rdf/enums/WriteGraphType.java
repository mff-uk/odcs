package cz.cuni.mff.xrg.odcs.rdf.enums;

/**
 * One of chosed way, how to load RDF data to named graph to SPARQL endpoint.
 *
 * @author Jiri Tomes
 */
public enum WriteGraphType {

	OVERRIDE /*Old data are overriden by new added data*/,
	MERGE /*Disjuction of sets new and old data*/,
	FAIL/*If target graph is not empty - throw GraphNotEmptyException */

}
