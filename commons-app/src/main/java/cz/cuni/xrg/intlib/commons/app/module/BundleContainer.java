package cz.cuni.xrg.intlib.commons.app.module;

import java.util.HashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.wiring.BundleWiring;

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
	 * Bundle container's uri. From where
	 * the bundle has been loaded.
	 */
	private String uri;
		
	/**
	 * List of loaded class<?> from this bundle.
	 */
	private java.util.Map<String, Class<?>> loadedClassCtors;
	
	/**
	 * Bundle class loader. The variable is load on request in {@link #getClassLoader()}
	 */
	private ClassLoader classLoader;
	
	public BundleContainer(Bundle bundle, String uri) {
		this.bundle = bundle;
		this.uri = uri;
		this.loadedClassCtors = new HashMap<>();
		this.classLoader = null;
	}
	
	/**
	 * Load class with given name from the bundle.
	 * @param className Class name prefixed with packages.
	 * @return Loaded class.
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Object loadClass(String className) 
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> loaderClass = null;
		if (loadedClassCtors.containsKey(className)) {
			// class already been loaded 
			loaderClass = this.loadedClassCtors.get(className);
		} else {
			// try to load class -> throw: ClassNotFoundException
            loaderClass = bundle.loadClass(className);
            // store loaded class
            this.loadedClassCtors.put(className, loaderClass);
		}
		
		// we have loader, create instance ..		
		Object result = null;
		try {
			result = loaderClass.newInstance(); // throw: InstantiationException, IllegalAccessException
		} catch (NoClassDefFoundError ex) {
			// we just change the type to ClassNotFoundException
			// so it can properly catch
			throw new ClassNotFoundException(ex.getMessage());
		}
		// return instance
		return result;
	}

	/**
	 * Uninstall bundle.
	 * @throws BundleException 
	 */
	public void uninstall() throws BundleException {
		// clear list
		loadedClassCtors.clear();
		classLoader = null;
		// 
		bundle.uninstall();
		bundle = null;
	}

	public Bundle getBundle() {
		return bundle;
	}
	
	public String getUri() {
		return uri;
	}
	
	public ClassLoader getClassLoader() {
		if (classLoader == null && bundle != null) {
			classLoader = bundle.adapt(BundleWiring.class).getClassLoader();
		}
		return classLoader;
	}
}
