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

	private String SPARQL_endpoint;

	private String Host_name;

	private String Password;

	private List<String> GraphsUri;

	private String SPARQL_query;

	private boolean ExtractFail;

	private boolean UseStatisticalHandler;

	private boolean failWhenErrors;

	public RDFExtractorConfig() {
		this.SPARQL_endpoint = "";
		this.Host_name = "";
		this.Password = "";
		this.GraphsUri = new LinkedList<>();
		this.SPARQL_query = "";
		this.ExtractFail = true;
		this.UseStatisticalHandler = true;
		this.failWhenErrors = false;
	}

	public RDFExtractorConfig(String SPARQL_endpoint, String Host_name,
			String Password,
			List<String> GraphsUri, String SPARQL_query, boolean ExtractFail,
			boolean UseStatisticalHandler, boolean failWhenErrors) {

		this.SPARQL_endpoint = SPARQL_endpoint;
		this.Host_name = Host_name;
		this.Password = Password;
		this.GraphsUri = GraphsUri;
		this.SPARQL_query = SPARQL_query;
		this.ExtractFail = ExtractFail;
		this.UseStatisticalHandler = UseStatisticalHandler;
		this.failWhenErrors = failWhenErrors;
	}

	public String getSPARQLEndpoint() {
		return SPARQL_endpoint;
	}

	public String getHostName() {
		return Host_name;
	}

	public String getPassword() {
		return Password;
	}

	public List<String> getGraphsUri() {
		return GraphsUri;
	}

	public String getSPARQLQuery() {
		return SPARQL_query;
	}

	public boolean isExtractFail() {
		return ExtractFail;
	}

	public boolean isUsedStatisticalHandler() {
		return UseStatisticalHandler;
	}

	public boolean isFailWhenErrors() {
		return failWhenErrors;
	}

	@Override
	public boolean isValid() {
		return SPARQL_endpoint != null
				&& Host_name != null
				&& Password != null
				&& GraphsUri != null
				&& SPARQL_query != null;
	}
}
