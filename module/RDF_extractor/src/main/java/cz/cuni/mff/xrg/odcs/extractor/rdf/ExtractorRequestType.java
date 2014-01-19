package cz.cuni.mff.xrg.odcs.extractor.rdf;

/**
 * Specify one of possible type of HTTP request used for data extraction from
 * SPARQL endpoint.
 *
 * @author Jiri Tomes
 */
public enum ExtractorRequestType {

	GET_URL_ENCODER,
	POST_URL_ENCODER,
	POST_UNENCODED_QUERY;
}
