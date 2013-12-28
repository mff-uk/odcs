package cz.cuni.mff.xrg.odcs.rdf.enums;

/**
 *
 * Type of problems we want to find out in case of parsing RDF data triples
 * using RDF handler.
 *
 * @author Jiri Tomes
 */
public enum ParsingConfictType {

	/**
	 * Define warning type find out in case of parsing RDF triples.
	 */
	WARNING,
	/**
	 * Define error type find out in case of parsing RDF triples.
	 */
	ERROR;

	/**
	 *
	 * @return Name of {@link ParsingConfictType} starts with capital first
	 *         letter (others are small).
	 */
	@Override
	public String toString() {
		String typeName = this.name().toLowerCase();

		String firstLetter = typeName.substring(0, 1).toUpperCase();
		String restWord = typeName.substring(1);

		return firstLetter + restWord;
	}
}
