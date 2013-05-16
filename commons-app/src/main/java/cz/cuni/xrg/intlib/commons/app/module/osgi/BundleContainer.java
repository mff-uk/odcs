package cz.cuni.xrg.intlib.commons.app.module.osgi;

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
	 * List of loaded class<?> from this bundle.
	 */
	private java.util.Map<String, Class<?>> loadedClassCtors;
	
	/**
	 * True if package is resolved, update
	 * only on request.
	 */
	private boolean isResolved;
	
	public BundleContainer(Bundle bundle) {
		this.bundle = bundle;
		this.loadedClassCtors = new HashMap<>();
		
	}
	
	/**
	 * Check the bundle state and return true
	 * if the bundle is in resolve state.
	 * @return
	 */
	public boolean isResolved() {
		isResolved = updateIsResolved();
		return isResolved;
	}
	
	/**
	 * Check resolve state and return true if
	 * the package is resolved but hasn't been up till now.
	 * @return
	 */
	public boolean isNewlyResolved() {
		boolean lastState = isResolved;
		if (lastState == isResolved() ){
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Check the bundle and update {@link #isResolved}.
	 * @return
	 */
	private boolean updateIsResolved() {
		switch(bundle.getState()) {
		case Bundle.RESOLVED:
		case Bundle.ACTIVE:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Load class with given name from the bundle.
	 * @param className Class name prefixed with packages.
	 * @return Loaded class.
	 * @throws OSGiException
	 */
	public Object loadClass(String className) throws OSGiException {
		Class<?> loaderClass = null;
		if (loadedClassCtors.containsKey(className)) {
			// class already been loaded 
			loaderClass = this.loadedClassCtors.get(className);
		} else {
			// try to load class
			 try {
                // load class from bundle
                loaderClass = bundle.loadClass(className);
                // store loaded class
                this.loadedClassCtors.put(className, loaderClass);
            } catch (ClassNotFoundException e) {
                // uninstall bundle and throw
                throw new OSGiException(e);
            }
		}
		// we have loader, create instance ..
		Object resulObject = null;
		
		try {
			resulObject = loaderClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new OSGiException(e);
		}
		
		return resulObject;
	}

	/**
	 * Uninstall bundle.
	 * @throws BundleException 
	 */
	public void uninstall() throws BundleException {
		bundle.uninstall();
		bundle = null;
		// clear list
		loadedClassCtors.clear();
	}

	
	public Bundle getBundle() {
		return bundle;
	}
}
