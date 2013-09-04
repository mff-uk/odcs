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
 * Class provide functionality to serialise, deserialize and create instance of
 * {@link DPUConfigObject}.
 *
 * @author Petyr
 *
 */
public class ConfigWrap<C extends DPUConfigObject> {

	/**
	 * Configuration's class.
	 */
	private Class<C> configClass;

	/**
	 * Stream for de/serialization.
	 */
	private XStream xstream;

	public ConfigWrap(Class<C> configClass) {
		this.configClass = configClass;
		this.xstream = new XStream();
		// set class loader
		this.xstream.setClassLoader(configClass.getClassLoader());
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
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Deserialize configuration. If the parameter is null or empty then null is
	 * returned.
	 *
	 * @param c Serialized configuration.
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
			throw new ConfigException(
					"Unexpected exception configuration object.", e);
		}
		return config;
	}

	/**
	 * Serialized actual stored configuration. Can return null if configuration
	 * is null.
	 *
	 * @return Serialized configuration, can be null.
	 * @throws ConfigException
	 */
	public byte[] serialize(C config) throws ConfigException {
		if (config == null) {
			return null;
		}
		byte[] result = null;
		// serialise object into byte[]
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			// use XStream for serialisation	
			XStream xStream = new XStream();
			try (ObjectOutputStream objOut = xStream.createObjectOutputStream(
					byteOut)) {
				objOut.writeObject(config);
			}
			result = byteOut.toByteArray();
		} catch (IOException e) {
			throw new ConfigException("Can't serialize configuration.", e);
		}
		return result;
	}
}
