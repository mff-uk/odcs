package cz.cuni.xrg.intlib.commons.data;

/**
 * Types of DataUnit interface implementation.
 * 
 * @author Petyr
 *
 */
public enum DataUnitType {
	/**
	 * General RDF type, the application select repository type.
	 * see @{link RDFDataRepository}
	 */
	RDF,
	/**
	 * RDF data unit type.
	 * see @{link RDFDataRepository}
	 */
	RDF_Local,
	/**
	 * RDF data unit type.
	 * see @{link RDFDataRepository}
	 */
	RDF_Virtuoso
}
