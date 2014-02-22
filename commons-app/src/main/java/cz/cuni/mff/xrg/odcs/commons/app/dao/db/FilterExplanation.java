package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

/**
 * Class used to explain filter.
 * 
 * @author Petyr
 */
public class FilterExplanation {

	private final String propertyName;

	private final String operation;

	private final Object value;

	/**
	 * If true then represent a complex query, in such case every other field
	 * should be null in such case.
	 */
	private final boolean complex;

	/**
	 * Create explanation for complex filter.
	 */
	public FilterExplanation() {
		this.propertyName = null;
		this.operation = null;
		this.value = null;
		this.complex = true;
	}

	public FilterExplanation(String propertyName, String operation, Object value) {
		this.propertyName = propertyName;
		this.operation = operation;
		this.value = value;
		this.complex = false;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public String getOperation() {
		return operation;
	}

	public Object getValue() {
		return value;
	}

	public boolean isComplex() {
		return complex;
	}

}
