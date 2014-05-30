package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

/**
 * Responsible for keeping pair of SPARQL query and boolean value, where value
 * is TRUE if case of 'special' contruct type (for more see {@link PlaceholdersHelper}) and FALSE for standard SPARQL update query.
 * 
 * @author Jiri Tomes
 */
public class SPARQLQueryPair {

    private String SPARQLQuery;

    private boolean isConstructType;

    /**
     * Create new instance of {@link SPARQLQueryPair}.
     * 
     * @param SPARQLQuery
     *            SPARQL query value as string
     * @param isConstructType
     *            boolean value, if SPARQL query is construct or
     *            not.
     */
    public SPARQLQueryPair(String SPARQLQuery, boolean isConstructType) {
        this.SPARQLQuery = SPARQLQuery;
        this.isConstructType = isConstructType;
    }

    /**
     * Returns SPARQL query value as string.
     * 
     * @return SPARQL query.
     */
    public String getSPARQLQuery() {
        return SPARQLQuery;
    }

    /**
     * Returns collection of {@link SPARQLQueryPair} instance.
     * 
     * @return true, if SPARQL query is construct, false otherwise.
     */
    public boolean isConstructType() {
        return isConstructType;
    }
}
