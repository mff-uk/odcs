package cz.cuni.mff.xrg.odcs.loader.rdf;

/**
 * Specify one of possible type of HTTP request used for loading data to SPARQL
 * endpoint.
 *
 * @author Jiri Tomes
 */
public enum LoaderPostType {

	POST_URL_ENCODER,
	POST_UNENCODED_QUERY;
}
