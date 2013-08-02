package cz.cuni.mff.xrg.intlib.extractor.rdf;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;

/**
 * Enum for naming setting values.
 *
 * @author Petyr
 *
 */
public class RDFExtractorConfig implements DPUConfigObject {

	public String SPARQL_endpoint;

	public String Host_name;

	public String Password;

	public List<String> GraphsUri = new LinkedList<>();

	public String SPARQL_query;

	public boolean ExtractFail;
	
	public boolean UseStatisticalHandler;

	@Override
	public boolean isValid() {
		return SPARQL_endpoint != null &&
				Host_name != null && 
				Password != null &&
				GraphsUri != null && 
				SPARQL_query != null;
		} 
}
