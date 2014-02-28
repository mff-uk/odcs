package cz.cuni.mff.xrg.odcs.commons.data;

/**
 * Types of DataUnit interface implementation.
 * 
 * @author Petyr
 *
 */
public enum DataUnitType {
	/**
	 * General RDF type, the repository will be selected by the application.
	 */
	RDF,
	/**
	 * RDF data unit type with local storage.
	 */
	RDF_Local,
	/**
	 * RDF data unit type with Virtuoso as RDF storage.
	 */
	RDF_Virtuoso,
	/**
	 * Represent file data unit.
	 */
	FILE
}
