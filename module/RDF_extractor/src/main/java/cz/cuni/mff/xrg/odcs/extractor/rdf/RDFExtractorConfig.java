package cz.cuni.mff.xrg.odcs.extractor.rdf;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

/**
 * Enum for naming setting values.
 *
 * @author Petyr
 *
 */
public class RDFExtractorConfig extends DPUConfigObjectBase {

	public String SPARQL_endpoint = "";

	public String Host_name = "";

	public String Password = "";

	public List<String> GraphsUri = new LinkedList<>();

	public String SPARQL_query = "";

	public boolean ExtractFail = true;

	public boolean UseStatisticalHandler = true;

	public boolean failWhenErrors = false;

	@Override
	public boolean isValid() {
		return SPARQL_endpoint != null
				&& Host_name != null
				&& Password != null
				&& GraphsUri != null
				&& SPARQL_query != null;
	}
}
