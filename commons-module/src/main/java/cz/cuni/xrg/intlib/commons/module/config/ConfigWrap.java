package cz.cuni.xrg.intlib.commons.module.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.thoughtworks.xstream.XStream;

import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;

/**
 * Class provide functionality to working with {@link DPUConfigObject} and it's 
 * serialised version.
 * 
 * @author Petyr
 *
 */
public class ConfigWrap<T extends DPUConfigObject> {
	
	/**
	 * Configuration object.
	 */
	private T config;

    /**
     * Stream for de/serialization.
     */
    private XStream xstream;
    
    public ConfigWrap(T config) {
    	this.config = config;
    	this.xstream = new XStream();
    	// set class loader
    	this.xstream.setClassLoader(config.getClass().getClassLoader());
    }    
    
    /**
     * @param c Serialized configuration.
     * @throws ConfigException
     */
    public ConfigWrap(byte[] c) throws ConfigException {
    	this.config = null;
    	this.xstream = new XStream();
    	// try to deserialize .. 
    	configure(c);
    }
        
    /**
     * Set current configuration.
     * @param c
     */
    public void configure(T c) {
    	config = c;
    }
    
    /**
     * Set current configuration. If the parameter is null or empty 
	 * ConfigException is thrown. If throw then the previous configuration 
	 * remain untouched.
     * @param c Serialized configuration.
     * @throws ConfigException
     */
    public void configure(byte[] c) throws ConfigException {
    	if (c == null || c.length == 0) {
			throw new ConfigException("Configuration is null or empty.");
		}
		// reconstruct object form byte[]
		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(c)) {
			// use XStream for serialisation
			ObjectInputStream objIn = xstream.createObjectInputStream(byteIn);						
			Object obj = objIn.readObject();
			config = (T)obj;
			objIn.close();
		} catch (IOException e) {
			throw new ConfigException("Can't deserialize configuration.", e);
		} catch (ClassNotFoundException e) {
			throw new ConfigException("Can't re-cast configuration object.", e);
		} catch (Exception e) {
			throw new ConfigException("Unexpected exception configuration object.", e);
		}
    }
    
    /**
     * Provide access to stored configuration object.
     * @return Configuration, can be null.
     */
    public T getConf() {
    	return config;
    }
    
    /**
     * Serialized actual stored configuration. Can return null 
     * if configuration is null.
     * @return Serialized configuration, can be null.
     * @throws ConfigException 
     */
    public byte[] getConfAsByte() throws ConfigException {
    	if (config == null) {
    		return null;
    	}
    	
    	byte[] result = null;
		// serialise object into byte[]
		try(ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {	
			// use XStream for serialisation	
			XStream xstream = new XStream();
			ObjectOutputStream objOut = xstream.createObjectOutputStream(byteOut);
				objOut.writeObject(config);
			objOut.close();
			result = byteOut.toByteArray();
		} catch (IOException e) {
			throw new ConfigException("Can't serialize configuration.", e);
		}
		return result;
    }
    
    
}
