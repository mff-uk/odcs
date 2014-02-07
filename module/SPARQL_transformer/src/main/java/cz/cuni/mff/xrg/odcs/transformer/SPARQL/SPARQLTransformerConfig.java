package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

/**
 *
 * @author Jiri Tomes
 * @author tknap
 */
public class SPARQLTransformerConfig extends DPUConfigObjectBase {

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

	public List<SPARQLQueryPair> getQueryPairs() {
		return queryPairs;
	}

	@Override
	public boolean isValid() {
		return queryPairs != null;
	}

	@Override
	public void onDeserialize() {
		if (queryPairs == null) {
			queryPairs = new LinkedList<>();
		}
		if (SPARQL_Update_Query != null) {
			queryPairs.add(new SPARQLQueryPair(SPARQL_Update_Query, isConstructType));
		}
	}
}
