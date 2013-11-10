package cz.cuni.mff.xrg.odcs.commons.app.dao.db.filter;

/**
 * Filter for equality of given property.
 * 
 * @author Petyr
 */
public class Compare extends BaseFilter {
	
	Object propertyValue;
	
	CompareType type;
	
	private Compare(String propertyName, Object propertyValue, CompareType type) {
		super(propertyName);
		this.propertyValue = propertyValue;
		this.type = type;
	}
	
	public static Compare equal(String propertyName, Object object) {
		return new Compare(propertyName, object, CompareType.EQUAL);
	}
	
	public static Compare greater(String propertyName, Comparable object) {
		return new Compare(propertyName, object, CompareType.GREATER);
	}
	
	public static Compare greaterEqual(String propertyName, Comparable object) {
		return new Compare(propertyName, object, CompareType.GREATER_OR_EQUAL);
	}
	
	public static Compare less(String propertyName, Comparable object) {
		return new Compare(propertyName, object, CompareType.LESS);
	}
	
	public static Compare lessEqual(String propertyName, Comparable object) {
		return new Compare(propertyName, object, CompareType.LESS_OR_EQUAL);
	}
	
}
