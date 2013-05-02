package cz.cuni.xrg.intlib.commons.configuration;

/**
 * Class for basic configuration.
 * @author Petyr
 *
 */
public interface Configuration {
		
	/**
	 * Return value for given id.
	 * @param parameter id
	 * @return null if there is not object stored under given id
	 */
	public Object getValue(String parameter);
	
	/**
	 * Store given object under given id. If object already exist
	 * then it's rewritten.
	 * @param parameter object id
	 * @param value object to store
	 */
	public void setValue(String parameter, Object value);
}
