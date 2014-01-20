package cz.cuni.mff.xrg.odcs.loader.rdf;

/**
 * Specify params should be used when POSTing RDF data to target SPARQL
 * endpoint.
 *
 * @author Jiri Tomes
 */
public class LoaderEndpointParams {

	private String queryParam;

	private String defaultGraphParam;

	private LoaderPostType postType;

	/**
	 * Create SPARQL loader default setting for VIRTUOSO endpoint.
	 */
	public LoaderEndpointParams() {
		this.queryParam = "update";
		this.defaultGraphParam = "using-graph-uri";
		this.postType = LoaderPostType.POST_URL_ENCODER;
	}

	/**
	 * Create SPARQL loader setting for ENDPOINT depends on given parameters.
	 *
	 * @param queryParam        String value of query parameter.
	 * @param defaultGraphParam String value of default graph parameter.
	 * @param postType          HTTP request type for SPARQL loader.
	 */
	public LoaderEndpointParams(String queryParam, String defaultGraphParam,
			LoaderPostType postType) {
		this.queryParam = queryParam;
		this.defaultGraphParam = defaultGraphParam;
		this.postType = postType;
	}

	/**
	 * Returns HTTP request type for SPARQL loader.
	 *
	 * @return HTTP request type for SPARQL loader.
	 */
	public LoaderPostType getPostType() {
		return postType;
	}

	/**
	 * Returns string value of query parameter.
	 *
	 * @return String value of query parameter.
	 */
	public String getQueryParam() {
		return queryParam;
	}

	/**
	 * Returns string value of default graph parameter.
	 *
	 * @return String value of default graph parameter.
	 */
	public String getDefaultGraphParam() {
		return defaultGraphParam;
	}
}
