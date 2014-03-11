package cz.cuni.mff.xrg.odcs.loader.rdf;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;

/**
 * SPARQL loader configuration.
 *
 * @author Petyr
 * @author Jiri Tomes
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

	/**
	 * True if the input should be copied to the output.
	 */
	private boolean penetrable;	
	
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
		this.penetrable = false;
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
		this.penetrable = false;
	}

	/**
	 * Returns URL address of SPARQL endpoint as string.
	 *
	 * @return URL address of SPARQL endpoint as string.
	 */
	public String getSPARQLEndpoint() {
		return SPARQL_endpoint;
	}

	/**
	 * Returns parameters for target SPARQL endpoint as
	 * {@link LoaderEndpointParams} instance.
	 *
	 * @return parameters for target SPARQL endpoint as
	 *         {@link LoaderEndpointParams} instance.
	 */
	public LoaderEndpointParams getEndpointParams() {
		return endpointParams;
	}

	/**
	 * Returns host name for target SPARQL endpoint.
	 *
	 * @return host name for target SPARQL endpoint.
	 */
	public String getHostName() {
		return Host_name;
	}

	/**
	 * Returns password for access to the target SPARQL endpoint.
	 *
	 * @return password for access to the target SPARQL endpoint.
	 */
	public String getPassword() {
		return Password;
	}

	/**
	 * Returns list of graphs where RDF data will be loaded.
	 *
	 * @return list of graphs where RDF data will be loaded.
	 */
	public List<String> getGraphsUri() {
		return GraphsUri;
	}

	/**
	 * Returns one of way how to load RDF data to named graph to SPARQL
	 * endpoint. See {@link WriteGraphType}.
	 *
	 * @return one of way how to load RDF data to named graph to SPARQL
	 *         endpoint.
	 */
	public WriteGraphType getGraphOption() {
		return graphOption;
	}

	/**
	 * Returns one of way how to load RDF data insert part to the SPARQL
	 * endpoint.
	 *
	 * @return one of way how to load RDF data insert part to the SPARQL
	 *         endpoint.
	 */
	public InsertType getInsertOption() {
		return insertOption;
	}

	/**
	 * Returns the size of one data part for loading.
	 *
	 * @return the size of one data part for loading.
	 */
	public long getChunkSize() {
		return chunkSize;
	}

	/**
	 * Returns true, if data are validated before loading to SPARQL endpoint,
	 * false otherwise.
	 *
	 * @return true, if data are validated before loading to SPARQL endpoint,
	 *         false otherwise.
	 */
	public boolean isValidDataBefore() {
		return validDataBefore;
	}

	/**
	 * Returns time in ms how long wait before re-connection attempt.
	 *
	 * @return Time in ms how long wait before re-connection attempt.
	 */
	public Long getRetryTime() {
		return retryTime;
	}

	/**
	 * Returns count of re-connection if connection failed. For infinite loop
	 * use zero or negative integer.
	 *
	 * @return Count of re-connection if connection failed. For infinite loop
	 *         use zero or negative integer.
	 *
	 */
	public Integer getRetrySize() {
		return retrySize;
	}

	public String getSPARQL_endpoint() {
		return SPARQL_endpoint;
	}

	public void setSPARQL_endpoint(String SPARQL_endpoint) {
		this.SPARQL_endpoint = SPARQL_endpoint;
	}

	public String getHost_name() {
		return Host_name;
	}

	public void setHost_name(String Host_name) {
		this.Host_name = Host_name;
	}

	public boolean isPenetrable() {
		return penetrable;
	}

	public void setPenetrable(boolean redirectInput) {
		this.penetrable = redirectInput;
	}
		
	/**
	 * Returns true, if DPU configuration is valid, false otherwise.
	 *
	 * @return true, if DPU configuration is valid, false otherwise.
	 */
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
	
}
