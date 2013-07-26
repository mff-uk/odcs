package cz.cuni.xrg.intlib.rdf.enums;

/**
 * Possible types of SPARQL queries.
 *
 * @author Jiri Tomes
 */
public enum SPARQLQueryType {

	SELECT,
	CONSTRUCT,
	/*as value for syntax error or other values*/
	UNKNOWN;
}
