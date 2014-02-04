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

	private List<String> SPARQL_Update_Query;

	private boolean isConstructType;

	public SPARQLTransformerConfig() {
		this.SPARQL_Update_Query = new LinkedList<>();
		this.isConstructType = false;
		

	}

	public SPARQLTransformerConfig(List<String> SPARQL_Update_Query,
			boolean isConstructType) {
		this.SPARQL_Update_Query = SPARQL_Update_Query;
		this.isConstructType = isConstructType;
	}

	public List<String> getSPARQLUpdateQuery() {
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
