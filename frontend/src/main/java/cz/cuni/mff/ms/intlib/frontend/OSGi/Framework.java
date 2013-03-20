package cz.cuni.mff.ms.intlib.frontend.OSGi;

import org.osgi.framework.Bundle;
import org.osgi.framework.launch.FrameworkFactory;

public class Framework {

	/**
	 * Package and class name for class, that will be loaded
	 * from bundle into application.
	 */
	private final String baseDpuClassName = ""; 
	
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
				"cz.cuni.mff.ms.intlib.commons,com.vaadin,com.vaadin.ui");
		
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
	 * @throws ExceptionOSGi
	 */
	public void start() throws ExceptionOSGi
	{
		FrameworkFactory frameworkFactory = null; // org.eclipse.osgi.launch.EquinoxFactory
		
		try {
			frameworkFactory = java.util.ServiceLoader.load(FrameworkFactory.class).iterator().next();
		}
		catch(Exception ex) {
			// failed to load FrameworkFactory class
			throw new ExceptionOSGi("Can't load class FrameworkFactory.", ex);
		}
			
		this.framework = frameworkFactory.newFramework( prepareSettings() );		
		this.context = null;
		try {
	    	// start OSGi container ..
			this.framework.start();	    		    	
		} catch (org.osgi.framework.BundleException ex) {
			// failed to start/initiate framework
			throw new ExceptionOSGi("Failed to start OSGi framework.", ex);
		}
		
		try {
			this.context = this.framework.getBundleContext();
		}
		catch (SecurityException ex) {
			// we have to stop framework ..
			stop();
			throw new ExceptionOSGi("Failed to get OSGi context.", ex);
		}
	}		

	/**
	 * Install bundle into framework.
	 * @param uri Uri to install bundle from.
	 * @return INstalled bundle.
	 * @throws ExceptionOSGi
	 */
	private Bundle installBundle(String uri) throws ExceptionOSGi
	{
		Bundle bundle = null;
	    try {
	    	bundle = this.context.installBundle(uri);
		} catch (org.osgi.framework.BundleException ex) {			
			throw new ExceptionOSGi("Failed to load bundle from uri: " + uri + " .", ex);
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
    			bundle.uninstall();
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
		return result;
	}
	
	/**
	 * Load BaseDPU class from bundle.
	 * @param uri Uri to bundle.
	 * @return Loaded BaseDPU class.
	 */
	public cz.cuni.mff.ms.intlib.commons.BaseDPU loadDPU(String uri) throws ExceptionOSGi {
		// start by loading Bundle
		Bundle bundle = installBundle(uri);

		Class<?> loaderClass = null;
		try {
			// load class from bundle
			loaderClass = bundle.loadClass(this.baseDpuClassName);
		} catch (ClassNotFoundException ex) {
			// uninstall bundle and throw
			uninstallBundle(bundle);
			throw new ExceptionOSGi("Failed to load class from bundle.", ex);			
		}

		// dpu store loaded BaseDPU instance
		cz.cuni.mff.ms.intlib.commons.BaseDPU dpu = null;		
		try {
			dpu = (cz.cuni.mff.ms.intlib.commons.BaseDPU)loaderClass.newInstance();
		} catch (InstantiationException ex) {
			// uninstall bundle and throw
			uninstallBundle(bundle);
			throw new ExceptionOSGi("Failed to load class from bundle.", ex);
		} catch (IllegalAccessException ex) {
			// uninstall bundle and throw
			uninstallBundle(bundle);
			throw new ExceptionOSGi("Failed to load class from bundle.", ex);
		}
				
		return dpu;
	}
		
}
