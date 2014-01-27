package cz.cuni.mff.xrg.odcs.loader.rdf;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;

/**
 *
 * @author Petyr
 * @author Jiri Tomes
 *
 */
public class RDFLoaderConfig extends DPUConfigObjectBase {

	private String SPARQL_endpoint;

	private String Host_name;

	private String Password;

	private List<String> GraphsUri;

	private WriteGraphType graphOption;

	private InsertType insertOption;

	private long chunkSize;

	private boolean validDataBefore;

	private Long retryTime;

	private Integer retrySize;

	private LoaderEndpointParams endpointParams;

	public RDFLoaderConfig() {
		this.SPARQL_endpoint = "";
		this.Host_name = "";
		this.Password = "";
		this.GraphsUri = new LinkedList<>();
		this.graphOption = WriteGraphType.OVERRIDE;
		this.insertOption = InsertType.STOP_WHEN_BAD_PART;
		this.chunkSize = 100;
		this.validDataBefore = false;
		this.retrySize = -1;
		this.retryTime = 1000L;
		this.endpointParams = new LoaderEndpointParams();

	}

	public RDFLoaderConfig(String SPARQL_endpoint, String Host_name,
			String Password,
			List<String> GraphsUri, WriteGraphType graphOption,
			InsertType insertOption, long chunkSize, boolean validDataBefore,
			long retryTime, int retrySize, LoaderEndpointParams endpointParams) {

		this.SPARQL_endpoint = SPARQL_endpoint;
		this.Host_name = Host_name;
		this.Password = Password;
		this.GraphsUri = GraphsUri;
		this.graphOption = graphOption;
		this.insertOption = insertOption;
		this.chunkSize = chunkSize;
		this.validDataBefore = validDataBefore;
		this.retryTime = retryTime;
		this.retrySize = retrySize;
		this.endpointParams = endpointParams;
	}

	public String getSPARQLEndpoint() {
		return SPARQL_endpoint;
	}

	public LoaderEndpointParams getEndpointParams() {
		return endpointParams;
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

	public WriteGraphType getGraphOption() {
		return graphOption;
	}

	public InsertType getInsertOption() {
		return insertOption;
	}

	public long getChunkSize() {
		return chunkSize;
	}

	public boolean isValidDataBefore() {
		return validDataBefore;
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
				&& GraphsUri != null
				&& graphOption != null
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
			endpointParams = new LoaderEndpointParams();
		}
	}
}
