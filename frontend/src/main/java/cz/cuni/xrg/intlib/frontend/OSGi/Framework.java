package cz.cuni.xrg.intlib.frontend.OSGi;

import org.osgi.framework.Bundle;
import org.osgi.framework.launch.FrameworkFactory;
import cz.cuni.xrg.intlib.commons.DPUExecutive;

public class Framework {

	/**
	 * Package and class name for class, that will be loaded
	 * from bundle into application.
	 */
	private final String baseDpuClassName = "module.Module"; 
	
	/**
	 * OSGi framework class.
	 */
	private org.osgi.framework.launch.Framework framework = null;
	
	/**
	 * OSGi context.
	 */
	private org.osgi.framework.BundleContext context = null;
	
	/**
	 * Store loaded bundles. Bundles are store under their name.
	 */
	private java.util.Map<String, Bundle> loadedBundles = new java.util.HashMap<String, Bundle>();
	
	/**
	 * Store keys for stored bundles in loadedBundles; 
	 */
	private java.util.Map<Bundle, String> reverseLoadedBundles = new java.util.HashMap<Bundle, String>();
	
	/**
	 * Store bundle class.
	 */
	private java.util.Map<String, Class<?> > loadedClassCtors = new java.util.HashMap<String, Class<?> >();
	
	/**
	 * Return configuration used to start up OSGi implementation.
	 * @return
	 */
	private java.util.Map<String, String> prepareSettings() {
		java.util.Map<String, String> config = new java.util.HashMap<String, String>();
		config.put("osgi.console", "");
		config.put("osgi.clean", "true");
		config.put("osgi.noShutdown", "true");
		// export packages
		config.put(org.osgi.framework.Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, 
						// commons
						"cz.cuni.xrg.intlib.commons," +
						"cz.cuni.xrg.intlib.commons.configuration," +
						"cz.cuni.xrg.intlib.commons.event," +
						"cz.cuni.xrg.intlib.commons.extractor," +
						"cz.cuni.xrg.intlib.commons.loader," +
						"cz.cuni.xrg.intlib.commons.transformer," +
						// commons-module
						"cz.cuni.xrg.intlib.commons.module," +
						// vaadin
						"com.vaadin,com.vaadin.ui");
		
		return config;
	}
			
	/**
	 * Stop OSGi framework.
	 */
	public void stop() {
        try {
        	if (this.framework != null ) {
        		this.framework.stop();
        	}
		} catch (Exception e) {
			// we can't throw here .. 
		}
        this.framework = null;
        this.context = null;
	}
	
	/**
	 * Start OSGi framework.
	 * @throws OSGiException
	 */
	public void start() throws OSGiException
	{
		FrameworkFactory frameworkFactory = null; // org.eclipse.osgi.launch.EquinoxFactory
		
		try {
			frameworkFactory = java.util.ServiceLoader.load(FrameworkFactory.class).iterator().next();
		}
		catch(Exception ex) {
			// failed to load FrameworkFactory class
			throw new OSGiException("Can't load class FrameworkFactory.", ex);
		}
			
		this.framework = frameworkFactory.newFramework( prepareSettings() );		
		this.context = null;
		try {
	    	// start OSGi container ..
			this.framework.start();	    		    	
		} catch (org.osgi.framework.BundleException ex) {
			// failed to start/initiate framework
			throw new OSGiException("Failed to start OSGi framework.", ex);
		}
		
		try {
			this.context = this.framework.getBundleContext();
		}
		catch (SecurityException ex) {
			// we have to stop framework ..
			stop();
			throw new OSGiException("Failed to get OSGi context.", ex);
		}
	}		

	/**
	 * Install bundle into framework.
	 * @param uri Uri to install bundle from.
	 * @return INstalled bundle.
	 * @throws OSGiException
	 */
	private Bundle installBundle(String uri) throws OSGiException
	{
		// has bundle been already loaded?
		if (this.loadedBundles.containsKey(uri)) {
			return this.loadedBundles.get(uri);
		}
		// load bundle
		Bundle bundle = null;
	    try {
	    	bundle = this.context.installBundle(uri);
	    	// add bundle to the loaded bundle list
	    	this.loadedBundles.put(uri, bundle);
	    	this.reverseLoadedBundles.put(bundle, uri);
		} catch (org.osgi.framework.BundleException ex) {			
			throw new OSGiException("Failed to load bundle from uri: " + uri + " .", ex);
		}	
	    return bundle;
	}
	
	/**
	 * Uninstall bundle from framework.
	 * @param bundle Bundle to uninstall.
	 * @return False if exception was thrown during uninstalling.
	 */
	private boolean uninstallBundle(Bundle bundle)
	{
    	try {
    		if (bundle == null) {
    			// already cleaned ?
    		} else {
    			bundle.uninstall();
    			// remove record about bundle
    			String key = this.reverseLoadedBundles.get(bundle);
    			// remove records related to bundle
    			this.loadedBundles.remove(key);
    			this.reverseLoadedBundles.remove(bundle);
    			this.loadedClassCtors.remove(key);
    		}
    	} catch (org.osgi.framework.BundleException ex) {			
    		return false;
	    }
    	return true;
	}	

	/**
	 * Uninstall all installed bundles.
	 * @return False if exception was thrown during uninstallation of any module.
	 */
	public boolean uninstallBundles() {
		boolean result = true;
		for (Bundle item : this.loadedBundles.values()) {
			if (!uninstallBundle(item)) {
				result = false;
			}
		}
		// clean storages 
		this.loadedBundles.clear();
		this.reverseLoadedBundles.clear();
		this.loadedClassCtors.clear();
		// ..
		return result;
	}
	
	/**
	 * Load BaseDPU class from bundle.
	 * @param uri Uri to bundle.
	 * @return Loaded BaseDPU class.
	 */
	public DPUExecutive loadDPU(String uri) throws OSGiException {
		// load bundle
		Bundle bundle = installBundle(uri);
		Class<?> loaderClass = null;		
		if (this.loadedClassCtors.containsKey(uri)) {
			// class already loaded
			loaderClass = this.loadedClassCtors.get(uri);
		} else  {
			// load class from bundle
			try {
				// load class from bundle
				loaderClass = bundle.loadClass(this.baseDpuClassName);
				// store loaded class
				this.loadedClassCtors.put(uri, loaderClass);
			} catch (ClassNotFoundException ex) {
				// uninstall bundle and throw
				uninstallBundle(bundle);
				throw new OSGiException("Failed to load class from bundle.", ex);			
			}			
		}
		
		// dpu store loaded BaseDPU instance
		DPUExecutive dpu = null;
		try {
			dpu = (DPUExecutive)loaderClass.newInstance();
		} catch (InstantiationException ex) {
			// uninstall bundle and throw
			uninstallBundle(bundle);
			throw new OSGiException("Failed to create a instance of class.", ex);
		} catch (IllegalAccessException ex) {
			// uninstall bundle and throw
			uninstallBundle(bundle);
			throw new OSGiException("Failed to create a instance of class.", ex);
		}
				
		return dpu;
	}
		
}
