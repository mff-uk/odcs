package cz.cuni.mff.xrg.odcs.dataunit.rdf.model;

/**
 *
 * @author Petyr
 */
public interface Literal {
	
	String getLabel();
	
	/**
	 * Return string representation of value.
	 * @return 
	 */
	String getDataType();

}
