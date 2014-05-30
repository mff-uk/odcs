package cz.cuni.mff.xrg.odcs.loader.rdf;

/**
 * Specify one of possible type of HTTP request used for loading data to SPARQL
 * endpoint.
 * 
 * @author Jiri Tomes
 */
public enum LoaderPostType {

    /**
     * Use POST method and URL encoder.
     */
    POST_URL_ENCODER,
    /**
     * Use POST method and unecoded query.
     */
    POST_UNENCODED_QUERY;
}
