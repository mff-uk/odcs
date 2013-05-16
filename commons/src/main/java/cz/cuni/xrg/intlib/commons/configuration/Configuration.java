package cz.cuni.xrg.intlib.commons.configuration;

import java.io.Serializable;
import java.util.Map;

/**
 * Interface for dpu's configuration.
 * 
 * @author Petyr
 */
public interface Configuration {
		
	/**
	 * Return value for given id.
	 * 
	 * @param parameter id
	 * @return null if there is not object stored under given id
	 */
	public Serializable getValue(String parameter);
	
	/**
	 * Store given object under given id. If object already exist
	 * then it's rewritten.
	 * 
	 * @param parameter object id
	 * @param value object to store
	 */
	public void setValue(String parameter, Serializable value);
	
	/**
	 * Fetches map with complete configuration.
	 * 
	 * @return configuration values
	 */
	public Map<String, Serializable> getValues();
	
	/**
	 * Replaces configuration values for new ones.
	 * 
	 * @param values 
	 */
	public void setValues(Map<String, Serializable> values);
}
