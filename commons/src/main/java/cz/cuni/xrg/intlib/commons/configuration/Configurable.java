package cz.cuni.xrg.intlib.commons.configuration;


/**
 * Interface describes object that can be configured by using configuration.
 * 
 * @author Petyr
 *
 */
public interface Configurable <C extends Configuration> {

	/**
	 * Configure object based on passed object. If the object does not
	 * contains valid configuration ConfigurationException should be thrown.
	 * @param c Object with configuration.
	 * @throws ConfigurationException
	 */
	void configure(C c) throws ConfigurationException;

	/**
	 * Return object configuration. 
	 * @return Object configuration.
	 */
	C getConfiguration();
}
