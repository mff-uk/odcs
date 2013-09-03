package cz.cuni.mff.xrg.intlib.transformer.SPARQL;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;

/**
 *
 * @author Jiri Tomes
 * @author tknap
 */
public class SPARQLTransformerConfig implements DPUConfigObject {

	public String SPARQL_Update_Query = "";

	@Override
	public boolean isValid() {
		return !SPARQL_Update_Query.isEmpty();
	}
}
