package cz.cuni.xrg.intlib.commons.configuration;


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
	 * {@link ConfigException} is thrown.
	 * @param c Serialized configuration.
	 * @throws ConfigException
	 */
	void configure(byte[] c) throws ConfigException;
	
	/**
	 * Return serialised configuration object.
	 * @return Serialised configuration.
	 */
	byte[] getConf();
	
	/**
	 * Configure object based on passed object. If the object does not
	 * contains valid configuration ConfigException should be thrown.
	 * @param c Object with configuration.
	 * @throws ConfigException In case of invalid configuration.
	 */
	@Deprecated
	void configure(C c) throws ConfigException;

	/**
	 * Return object configuration. 
	 * @return Object configuration.
	 */
	@Deprecated
	C getConfiguration();
}
