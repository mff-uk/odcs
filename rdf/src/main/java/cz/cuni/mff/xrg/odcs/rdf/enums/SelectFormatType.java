package cz.cuni.mff.xrg.odcs.rdf.enums;

/**
 * Possibilities of formats as output of SPARQL select query.
 *
 * @author Jiri Tomes
 */
public enum SelectFormatType {

	/**
	 * Data as result of SPARQL select are saved in RDF/XML format.
	 */
	XML,
	/**
	 * Data as result of SPARQL select are saved in CSV format.
	 */
	CSV,
	/**
	 * Data as result of SPARQL select are saved in JSON format.
	 */
	JSON,
	/**
	 * Data as result of SPARQL select are saved in TSV format.
	 */
	TSV;
}
