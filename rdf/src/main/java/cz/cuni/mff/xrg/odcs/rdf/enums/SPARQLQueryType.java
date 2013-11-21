package cz.cuni.mff.xrg.odcs.rdf.enums;

/**
 * Possible types of SPARQL queries.
 *
 * @author Jiri Tomes
 */
public enum SPARQLQueryType {

	SELECT,
	CONSTRUCT,
        DESCRIBE,
	/*as value for syntax error or other values*/
	UNKNOWN;
}
