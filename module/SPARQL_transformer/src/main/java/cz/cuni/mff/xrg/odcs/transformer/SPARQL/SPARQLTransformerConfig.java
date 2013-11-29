package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

/**
 *
 * @author Jiri Tomes
 * @author tknap
 */
public class SPARQLTransformerConfig extends DPUConfigObjectBase {

	private String SPARQL_Update_Query;

	private boolean isConstructType;

	public SPARQLTransformerConfig() {
		this.SPARQL_Update_Query = "";
		this.isConstructType = false;
	}

	public SPARQLTransformerConfig(String SPARQL_Update_Query,
			boolean isConstructType) {
		this.SPARQL_Update_Query = SPARQL_Update_Query;
		this.isConstructType = isConstructType;
	}

	public String getSPARQLUpdateQuery() {
		return SPARQL_Update_Query;
	}

	public boolean isConstructType() {
		return isConstructType;
	}

	@Override
	public boolean isValid() {
		return SPARQL_Update_Query != null;
	}
}
