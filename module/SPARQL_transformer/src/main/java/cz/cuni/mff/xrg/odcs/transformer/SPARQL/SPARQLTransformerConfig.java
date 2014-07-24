package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import java.util.LinkedList;
import java.util.List;

/**
 * SPARQL transformer configuration.
 * 
 * @author Jiri Tomes
 * @author tknap
 */
public class SPARQLTransformerConfig {

    private List<SPARQLQueryPair> queryPairs;

    private String SPARQL_Update_Query;

    boolean isConstructType;

    public SPARQLTransformerConfig() {
        this.queryPairs = new LinkedList<>();
    }

    public SPARQLTransformerConfig(String query, boolean isContructType) {
        this.queryPairs = new LinkedList<>();
        this.queryPairs.add(new SPARQLQueryPair(query, isContructType));
    }

    public SPARQLTransformerConfig(List<SPARQLQueryPair> queryPairs) {
        this.queryPairs = queryPairs;
    }

    /**
     * Returns collection of {@link SPARQLQueryPair} instance.
     * 
     * @return collection of {@link SPARQLQueryPair} instance.
     */
    public List<SPARQLQueryPair> getQueryPairs() {
        return queryPairs;
    }

    /**
     * Returns true, if DPU configuration is valid, false otherwise.
     * 
     * @return true, if DPU configuration is valid, false otherwise.
     */
    public boolean isValid() {
        return queryPairs != null;
    }

    public String getSPARQL_Update_Query() {
        return SPARQL_Update_Query;
    }

    public void setSPARQL_Update_Query(String SPARQL_Update_Query) {
        this.SPARQL_Update_Query = SPARQL_Update_Query;
    }

    public boolean isIsConstructType() {
        return isConstructType;
    }

    public void setIsConstructType(boolean isConstructType) {
        this.isConstructType = isConstructType;
    }

    public void setQueryPairs(List<SPARQLQueryPair> queryPairs) {
        this.queryPairs = queryPairs;
    }

    /**
     * Fill missing configuration with default values.
     */
//    @Override
//    public void onDeserialize() {
//        if (SPARQL_Update_Query != null) {
//            queryPairs.add(new SPARQLQueryPair(SPARQL_Update_Query,
//                    isConstructType));
//        }
//    }
}
