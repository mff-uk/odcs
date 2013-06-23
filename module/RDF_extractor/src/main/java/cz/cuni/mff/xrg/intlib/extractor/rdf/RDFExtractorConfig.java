package cz.cuni.mff.xrg.intlib.extractor.rdf;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.xrg.intlib.commons.configuration.Config;

/**
 * Enum for naming setting values.
 *
 * @author Petyr
 *
 */
public class RDFExtractorConfig implements Config {

	public String SPARQL_endpoint;

	public String Host_name;

	public String Password;

	public List<String> GraphsUri = new LinkedList<>();

	public String SPARQL_query;

	public Boolean ExtractFail;
}
