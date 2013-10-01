package cz.cuni.mff.xrg.odcs.commons.configuration;


/**
 * Interface describes object that can be configured by using configuration.
 * 
 * @author Petyr
 *
 */
public interface Configurable <C extends DPUConfigObject> {

	/**
	 * Deserialize given configuration and then use it to 
	 * configure object. If the invalid configuration is given then 
	 * {@link ConfigException} is thrown. For null the configuration
	 * is left unchanged.
	 * @param c Serialized configuration.
	 * @throws ConfigException
	 */
	void configure(byte[] c) throws ConfigException;
	
	/**
	 * Return serialized configuration object.
	 * @return Serialized configuration.
	 * @throws ConfigException If the configuration can't be serialized.
	 */
	byte[] getConf() throws ConfigException;
	
}
