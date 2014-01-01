package cz.cuni.mff.xrg.odcs.commons.module.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;

/**
 * Class provides functionality to serialize, deserialize and create instance of
 * {@link DPUConfigObject}. {@link DPUConfigObject} is serialized as XML, using XStream.
 *
 * @author Petyr
 * @param <C>
 *
 */
public class ConfigWrap<C extends DPUConfigObject> {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigWrap.class);
	
	/**
	 * Configuration's class.
	 */
	private final Class<C> configClass;

	/**
	 * Stream for de/serialization.
	 */
	private final XStream xstream;

	private final XStream xstreamUTF;
	
	public ConfigWrap(Class<C> configClass) {
		this.configClass = configClass;
		// stream for loading, not so strict
		this.xstream = new XStream();
		this.xstream.setClassLoader(configClass.getClassLoader());		
		// save always in utf8
		this.xstreamUTF = new XStream(new DomDriver("UTF-8"));
		this.xstreamUTF.setClassLoader(configClass.getClassLoader());
	}

	/**
	 * Create instance generic ConfigSerializer object. In case of error return
	 * null.
	 *
	 * @return Object instance or null.
	 */
	public C createInstance() {
		try {
			return configClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOG.error("Failed to create configuration instance", e);
			return null;
		}
	}

	/**
	 * Deserialize configuration. If the parameter is null or empty then null is
	 * returned.
	 *
	 * @param c Serialized configuration.
	 * @return 
	 * @throws ConfigException
	 */
	@SuppressWarnings("unchecked")
	public C deserialize(byte[] c) throws ConfigException {
		if (c == null || c.length == 0) {
			return null;
		}
		C config = null;
		// reconstruct object form byte[]
		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(c); ObjectInputStream objIn = xstream
				.createObjectInputStream(byteIn)) {
			Object obj = objIn.readObject();
			config = (C) obj;
		} catch (IOException e) {
			throw new ConfigException("Can't deserialize configuration.", e);
		} catch (ClassNotFoundException e) {
			throw new ConfigException("Can't re-cast configuration object.", e);
		} catch (Exception e) {
			throw new ConfigException(e);
		}
		
		// the config does not have to implement this, so be carefull
		try {		
			config.onDeserialize();
		} catch (AbstractMethodError e) {
			// the method is missing, well ignore this, just log
			LOG.warn("The DPU does not implement abstract method onSerialize() "
					+ "it probably does not inherit from base class. "
					+ "The call was ignored." , e);
		}		
		
		return config;
	}

	/**
	 * Serialized actual stored configuration. Can return null if configuration
	 * is null.
	 *
	 * @param config
	 * @return Serialized configuration, can be null.
	 * @throws ConfigException
	 */
	public byte[] serialize(C config) throws ConfigException {
		if (config == null) {
			return null;
		}
		// the config does not have to implement this, so be carefull
		try {		
			config.onSerialize();
		} catch (AbstractMethodError e) {
			// the method is missing, well ignore this, just log
			LOG.warn("The DPU does not implement abstract method onSerialize() "
					+ "it probably does not inherit from base class. "
					+ "The call was ignored." , e);
		}
		
		byte[] result = null;
		// serialise object into byte[]
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			// use XStream for serialisation
			try (ObjectOutputStream objOut = xstreamUTF.createObjectOutputStream(
					byteOut)) {
				objOut.writeObject(config);
			}
			result = byteOut.toByteArray();
		} catch (IOException e) {
			throw new ConfigException("Can't serialize configuration.", e);
		} 
		LOG.trace("Configuration {} serialized as {}", config.getClass().getName(), result);
		return result;
	}
}
