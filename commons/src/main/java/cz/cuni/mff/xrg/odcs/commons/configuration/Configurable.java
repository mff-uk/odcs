package cz.cuni.mff.xrg.odcs.commons.configuration;

/**
 * Interface describes object that can be configured by using configuration.
 * 
 * @author Petyr
 * @param <C> Configuration object that carries the configuration.
 *
 */
public interface Configurable <C extends DPUConfigObject> {

	/**
	 * Deserialize given configuration and then use it to 
	 * configure object. If the invalid configuration is given then 
	 * {@link ConfigException} is thrown. For null the configuration
	 * is left unchanged.
	 * @param config Serialized configuration.
	 * @throws ConfigException
	 */
	void configure(String config) throws ConfigException;
	
	/**
	 * Return serialized configuration object. If no configuration has 
	 * been previously set by {@link #configure(java.lang.String) } then serialized default
	 * configuration should be returned.
	 * @return Serialized configuration.
	 * @throws ConfigException If the configuration can't be serialized.
	 */
	String getConf() throws ConfigException;
	
}
