package cz.cuni.mff.xrg.odcs.extractor.rdf;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;
import java.util.List;

/**
 * Enum for naming setting values.
 *
 * @author Petyr
 * @author Jiri Tomes
 *
 */
public class RDFExtractorConfig extends DPUConfigObjectBase {

	private String SPARQL_endpoint;

	private String Host_name;

	private String Password;

	private String SPARQL_query;

	private boolean ExtractFail;

	private boolean UseStatisticalHandler;

	private boolean failWhenErrors;

	private Long retryTime;

	private Integer retrySize;

	private ExtractorEndpointParams endpointParams;

	private List<String> GraphsUri;

	public RDFExtractorConfig() {
		this.SPARQL_endpoint = "";
		this.Host_name = "";
		this.Password = "";
		this.SPARQL_query = "";
		this.ExtractFail = true;
		this.UseStatisticalHandler = true;
		this.failWhenErrors = false;
		this.retrySize = -1;
		this.retryTime = 1000L;
		this.endpointParams = new ExtractorEndpointParams();
	}

	public RDFExtractorConfig(String SPARQL_endpoint, String Host_name,
			String Password, String SPARQL_query, boolean ExtractFail,
			boolean UseStatisticalHandler, boolean failWhenErrors, int retrySize,
			long retryTime, ExtractorEndpointParams endpointParams) {

		this.SPARQL_endpoint = SPARQL_endpoint;
		this.Host_name = Host_name;
		this.Password = Password;
		this.SPARQL_query = SPARQL_query;
		this.ExtractFail = ExtractFail;
		this.UseStatisticalHandler = UseStatisticalHandler;
		this.failWhenErrors = failWhenErrors;
		this.retrySize = retrySize;
		this.retryTime = retryTime;
		this.endpointParams = endpointParams;
	}

	public ExtractorEndpointParams getEndpointParams() {
		return endpointParams;
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

	public Long getRetryTime() {
		return retryTime;
	}

	public Integer getRetrySize() {
		return retrySize;
	}

	@Override
	public boolean isValid() {
		return SPARQL_endpoint != null
				&& Host_name != null
				&& Password != null
				&& SPARQL_query != null
				&& retrySize != null
				&& retryTime != null
				&& retryTime > 0
				&& endpointParams != null;
	}

	@Override
	public void onDeserialize() {

		if (retrySize == null) {
			retrySize = -1;
		}
		if (retryTime == null) {
			retryTime = 1000L;
		}

		if (endpointParams == null) {
			endpointParams = new ExtractorEndpointParams();

			if (GraphsUri != null) {
				for (String defaultGraph : GraphsUri) {
					endpointParams.addDefaultGraph(defaultGraph);
				}
			}
		}
	}
}
