package cz.cuni.xrg.intlib.commons.app.module.osgi;

import java.util.Dictionary;
import java.util.HashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.xrg.intlib.commons.app.module.ModuleException;

/**
 * Represent a single bundle. Enable loading class from bundle. Also
 * contains cache for loaded classes.
 * 
 * <b>Synchronisation notes:</b>
 * {@link #loadedClassCtors}, {@link #uninstall()} and {@link #beginUpdate()}
 * methods are synchronised on {@link #lock}, so only one of this method can be 
 * running.
 * 
 * When {@link #beginUpdate()} is called it set flag that block calling
 * {@link #loadedClassCtors}, {@link #uninstall()}. The block is done after 
 * they acquire the {@link #lock}. So there can be only one method waiting
 * during update.
 * 
 * The update phase is checked by calling {@link #isBeingUpdated} which should
 * be called inside the block synchronised on {@link #lock}. This 
 * function return immediately if no update is done and wait if it is.
 * 
 * It is not possible that the update begin after {@link #isBeingUpdated} return 
 * true as the {@link #isBeingUpdated} is called inside synchronisation of 
 * {@link #lock}. This ensure that the bundle can move into update phase 
 * (ie. {@link #beginUpdate()} return) until function which call  
 * {@link #isBeingUpdated ends.
 * 
 * @author Petyr
 *
 */
class BundleContainer {

	private static final Logger LOG = LoggerFactory.getLogger(BundleContainer.class);
	
	/**
	 * The OSGI bundle it self.
	 */
	private Bundle bundle;	

	/**
	 * Uri from where the bundle has been load.
	 */
	private String uri;
	
	/**
	 * If {@link #isLib} == true, then contains DPU's directory. 
	 */
	private String dpuDirectory;
	
	/**
	 * List of loaded class<?> from this bundle.
	 */
	private java.util.Map<String, Class<?>> loadedClassCtors = new HashMap<>();
	
	/**
	 * Used to lock instance during non concurrent operations.
	 */
	private Object lock = new Object();	
		
	/**
	 * True if bundle is being updated in such case no other method then
	 * {@link #endUpdate()} or {@link #update(BundleContext, String)}
	 * can't be called. 
	 */
	private Boolean isBeingUpdated = false;
	
	/**
	 * True if container encapsulate library.
	 */
	private boolean isLib;
	
	/**
	 * Used only if {@link #isLib} == true. Cache main class name for DPUs.
	 */
	private String mainClassNameCache = null;
	
	/**
	 * Create container for library.
	 * @param uri
	 * @param bundle
	 */
	public BundleContainer(Bundle bundle, String uri) {
		this.bundle = bundle;
		this.uri = uri;
		this.isLib = true;
		this.mainClassNameCache = null;
	}
	
	/**
	 * Create container for DPU.
	 * @param bundle
	 * @param uri
	 * @param uri
	 */
	public BundleContainer(Bundle bundle, String uri, String dpuDirectory) {
		this.bundle = bundle;
		this.uri = uri;
		this.dpuDirectory = dpuDirectory;
		this.isLib = false;
	}	
	
	/**
	 * Load class with given name from the bundle.
	 * @param className Class name prefixed with packages.
	 * @return Loaded class.
	 * @throws ModuleException
	 */
	public Object loadClass(String className) 
			throws ModuleException {
		Class<?> loaderClass = null;
		synchronized(lock) {
			
			checkUpdating();
			
			if (loadedClassCtors.containsKey(className)) {
				// class already been loaded 
				loaderClass = this.loadedClassCtors.get(className);
			} else {
				// try to load class -> throw: ClassNotFoundException
	            try {
					loaderClass = bundle.loadClass(className);
				} catch (ClassNotFoundException e) {
					throw new ModuleException(e);
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
			throw new ModuleException(e);
		} catch (IllegalAccessException e) {
			throw new ModuleException(e);
		} catch (NoClassDefFoundError e) {
			throw new ModuleException(e);
		} catch (NoSuchMethodError e) {
			throw new ModuleException(e);
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
			
			checkUpdating();
			
			// clear list
			loadedClassCtors.clear();
			// 
			bundle.uninstall();
			bundle = null;
			mainClassNameCache = null;
		}
	}
	
	/**
	 * Lock this bundle for all operations but 
	 * {@link #update(BundleContext, String)} and {@link #endUpdate()}. 
	 * Every other operation must wait until {@link #endUpdate()} is called.
	 */
	public void beginUpdate() {
		synchronized(lock) {
			// by this we block all the operations
			isBeingUpdated = true;
		}
	}
	
	/**
	 * Update current bundle. Drop cache of loaded classes. The possible 
	 * exception during bundle uninstallation is silently ignored. 
	 * For loading the {@link BundleException} can be thrown, in such case
	 * the {@link #bundle} is set to null.
	 * The {@link uri} is updated only in case of successful update. 
	 * 
	 * The {@link #beginUpdate()} method must be called before this method.
	 * 
	 * @throws BundleException 
	 */
	public synchronized void update(BundleContext context, String uri) throws BundleException {
		// clear list
		loadedClassCtors.clear();
		// uninstall old bundle
		try {
			bundle.uninstall();
		} catch (BundleException e) {
			LOG.error("Failed to unistall bundle", e);
		} finally {
			bundle = null;
			mainClassNameCache = null;
		}
		// load new bundle
		bundle = context.installBundle(uri);
		// update uri
		this.uri = uri; 
	}
	
	/**
	 * Release bundle for all function call.
	 */
	void endUpdate() {
		// change to false and notify possibly waiting thread 
		isBeingUpdated = false;
		isBeingUpdated.notifyAll();
	}
	
	/**
	 * Blocking function, return immediately if and only if the current bundle is
	 * not updating ie. @{link #isBeingUpdated} == false. Otherwise put 
	 * the current thread into sleep.
	 * 
	 * Should be called inside block which is synchronised on {@link #lock}.
	 */
	private void checkUpdating() {
		while (isBeingUpdated) {
			try {
				isBeingUpdated.wait();
			} catch (InterruptedException e) {
				// check the isBeingUpdated variable
			}
		}
	}	
	
	/**
	 * Return content of manifest.mf file.
	 * @return
	 */
	public Dictionary<String, String> getHeaders() {
		return bundle.getHeaders();
	}
	
	public String dpuDirectory() {
		return dpuDirectory;
	}
	
	public boolean isLibrary() {
		return isLib;
	}
	
	public String getUri() {
		return uri;
	}
	
	/**
	 * Return name of main class. The value is cached.
	 * @return
	 */
	public String getMainClassName() throws ModuleException {
		if (mainClassNameCache == null) {
			final Dictionary<String, String> headers = getHeaders();
			final String packageName = (String) headers.get("DPU-Package");
			if (packageName == null) {
				LOG.error("'DPU-Package' undefined for '{}'", uri);
				throw new ModuleException(
						"Can't find 'DPU-Package' record in bundle's manifest.mf");
			}
			final String className = (String) headers.get("DPU-MainClass");
			if (className == null) {
				LOG.error("'DPU-MainClass' undefined for '{}'", uri);
				throw new ModuleException(
						"Can't find 'DPU-MainClass' record in bundle's manifest.mf");
			}
			mainClassNameCache = packageName + "." + className;
		}
		
		return mainClassNameCache;
	}
		
}
