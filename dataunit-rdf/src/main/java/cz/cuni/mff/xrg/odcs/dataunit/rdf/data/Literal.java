package cz.cuni.mff.xrg.odcs.dataunit.rdf.data;

import java.net.URI;

/**
 * An RDF literal consisting of a label (the value) and optionally a language
 * tag or a datatype (but not both).
 * 
 * @author Petyr
 */
public interface Literal extends Value {
	
	/**
	 * Gets the label of this literal.
	 * 
	 * @return The literal's label.
	 */
	public String getLabel();
	
	/**
	 * Gets the language tag for this literal, normalized to lower case.
	 * 
	 * @return The language tag for this literal, or <tt>null</tt> if it
	 *         doesn't have one.
	 */
	public String getLanguage();

	/**
	 * Gets the datatype for this literal.
	 * 
	 * @return The datatype for this literal, or <tt>null</tt> if it doesn't
	 *         have one.
	 */
	public URI getDatatype();
	
	/**
	 * Return value as an object.
	 * 
	 * @return 
	 */
	public Object asObject();
	
}
