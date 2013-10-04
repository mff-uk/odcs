package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

/**
 *
 * @author Jiri Tomes
 * @author tknap
 */
public class SPARQLTransformerConfig extends DPUConfigObjectBase {

	public String SPARQL_Update_Query = " ";

	public boolean isConstructType = false;

	@Override
	public boolean isValid() {
		return !SPARQL_Update_Query.isEmpty();
	}
}
