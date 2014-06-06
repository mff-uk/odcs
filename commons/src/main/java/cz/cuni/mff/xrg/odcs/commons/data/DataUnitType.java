package cz.cuni.mff.xrg.odcs.commons.data;

/**
 * Types of DataUnit interface implementation.
 * 
 * @author Petyr
 */
public enum DataUnitType {
    /**
     * General RDF type, the repository will be selected by the application.
     */
    RDF,
    /**
     * Represent file data unit.
     */
    FILE, 
    FILES
}
