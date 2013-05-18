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
	RDF(1),
	/**
	 * RDF data unit type.
	 * see @{link RDFDataRepository}
	 */
	RDF_Local(1),
	/**
	 * RDF data unit type.
	 * see @{link RDFDataRepository}
	 */
	RDF_Virtuoso(1);
	
	/**
	 * Determine DataUnit group, the types in the same 
	 * group can be recast.
	 */
	private int group;
	
	private DataUnitType(int group) {
		this.group = group;
	}
	
	/**
	 * Check if the source DataUnit sub-type (this) can be cast to the 
	 * target DataUnit sub-type.
	 * @param target Type of target.
	 * @return
	 */
	public boolean canBeCastTo(DataUnitType target) {
		return this == target || this.group == target.group;
	}
	
}
