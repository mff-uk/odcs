package cz.cuni.mff.xrg.odcs.dataunit.rdf;

/**
 * Supported types of RDF file formats.
 * 
 * @author Petyr
 */
public enum RDFFileType {
	/**
	 * Xml syntax.
	 */
	RDFXML,
	/**
	 * As triples - subject, predicate, object.
	 */
	N3,
	/**
	 * Extended turtle forma (TTL).
	 */	
	TRIG,
	/**
	 * Turtle format - extension of N3 type.
	 */
	TTL,
	/**
	 * TRIX RDF format.
	 */
	TRIX,
	/**
	 * N-Triples format.
	 */
	NT;
}