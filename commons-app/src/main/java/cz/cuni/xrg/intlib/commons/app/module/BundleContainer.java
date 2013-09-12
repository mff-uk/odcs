package cz.cuni.xrg.intlib.commons.app.module;

import java.util.Dictionary;
import java.util.HashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * Represent a single bundle with some additional information.
 * 
 * @author Petyr
 *
 */
class BundleContainer {

	/**
	 * The OSGI bundle it self.
	 */
	private Bundle bundle;
	
	/**
	 * Bundle container's URI. From where
	 * the bundle has been loaded.
	 */
	private String uri;
		
	/**
	 * List of loaded class<?> from this bundle.
	 */
	private java.util.Map<String, Class<?>> loadedClassCtors;

	/**
	 * Used to lock instance during non concurrent operations.
	 */
	private Object lock = new Object();
	
	public BundleContainer(Bundle bundle, String uri) {
		this.bundle = bundle;
		this.uri = uri;
		this.loadedClassCtors = new HashMap<>();
	}
	
	/**
	 * Load class with given name from the bundle.
	 * @param className Class name prefixed with packages.
	 * @return Loaded class.
	 * @throws ClassLoadFailedException
	 */
	public Object loadClass(String className) 
			throws ClassLoadFailedException {
		Class<?> loaderClass = null;
		synchronized(lock) {
			if (loadedClassCtors.containsKey(className)) {
				// class already been loaded 
				loaderClass = this.loadedClassCtors.get(className);
			} else {
				// try to load class -> throw: ClassNotFoundException
	            try {
					loaderClass = bundle.loadClass(className);
				} catch (ClassNotFoundException e) {
					throw new ClassLoadFailedException(e);
				}
	            // store loaded class
	            this.loadedClassCtors.put(className, loaderClass);
			}
		}
		// we have loader, create instance ..		
		Object result = null;
		try {
			result = loaderClass.newInstance();
		} catch (InstantiationException e) {
			throw new ClassLoadFailedException(e);
		} catch (IllegalAccessException e) {
			throw new ClassLoadFailedException(e);
		} catch (NoClassDefFoundError e) {
			throw new ClassLoadFailedException(e);
		} catch (NoSuchMethodError e) {
			throw new ClassLoadFailedException(e);
		}
		// return instance
		return result;
	}

	/**
	 * Uninstall bundle.
	 * @throws BundleException 
	 */
	public void uninstall() throws BundleException {
		synchronized(lock) {
			// clear list
			loadedClassCtors.clear();
			// 
			bundle.uninstall();
			bundle = null;
		}
	}

	/**
	 * Try to reload bundle, also drop all loaded classes.
	 * @throws BundleException 
	 */
	public void update() throws BundleException {
		synchronized(lock) {
			// clear list		
			loadedClassCtors.clear();
			// update
			bundle.update();
		}
	}
	
	/**
	 * Return content of manifest.mf file.
	 * @return
	 */
	public Dictionary<String, String> getHeaders() {
		return bundle.getHeaders();
	}
	
	public Bundle getBundle() {
		return bundle;
	}
	
	public String getUri() {
		return uri;
	}
		
}
