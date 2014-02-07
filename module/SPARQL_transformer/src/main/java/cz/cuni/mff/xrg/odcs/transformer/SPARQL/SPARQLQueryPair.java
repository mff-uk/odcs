package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

/**
 * Responsible for keeping pair of SPARQL query and boolean value, where value
 * is TRUE if case of 'special' contruct type (for more see
 * {@link PlaceholdersHelper}) and FALSE for standard SPARQL update query.
 *
 * @author Jiri Tomes
 */
public class SPARQLQueryPair {

	private String SPARQLQuery;

	private boolean isConstructType;

	public SPARQLQueryPair(String SPARQLQuery, boolean isConstructType) {
		this.SPARQLQuery = SPARQLQuery;
		this.isConstructType = isConstructType;
	}

	public String getSPARQLQuery() {
		return SPARQLQuery;
	}

	public boolean isConstructType() {
		return isConstructType;
	}
}
