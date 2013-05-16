package cz.cuni.xrg.intlib.commons.app.module.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.launch.FrameworkFactory;
import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;

public class Framework {

    /**
     * Package and class, that will be loaded from bundle into application.
     */
    private final String baseDpuClassName = "module.";
    
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
    private java.util.Map<String, BundleContainer> loadedBundles = new java.util.HashMap<>();
    
    /**
     * Store keys for stored bundles in {@link #loadedBundles}.
     */
    private java.util.Map<Bundle, String> reverseLoadedBundles = new java.util.HashMap<>();
        
    /**
     * Return configuration used to start up OSGi implementation.
     *
     * @param exportedPackages names of additional packages to export started
     * and separated by comma
     * @return
     */
    private java.util.Map<String, String> prepareSettings(String exportedPackages) {
        java.util.Map<String, String> config = new java.util.HashMap<>();
        config.put("osgi.console", "");
        config.put("osgi.clean", "true");
        config.put("osgi.noShutdown", "true");
        // export packages
        config.put(org.osgi.framework.Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
               exportedPackages);
        return config;
    }

    /**
     * Start OSGi framework.
     *
     * @param exportedPackages names of additional packages to export started
     * and separated by comma
     * @throws ModuleException
     */
    public void start(String exportedPackages) throws OSGiException {
        FrameworkFactory frameworkFactory = null; // org.eclipse.osgi.launch.EquinoxFactory

        try {
            frameworkFactory = java.util.ServiceLoader.load(FrameworkFactory.class).iterator().next();
        } catch (Exception ex) {
            // failed to load FrameworkFactory class
            throw new OSGiException("Can't load class FrameworkFactory.", ex);
        }

        framework = frameworkFactory.newFramework(prepareSettings(exportedPackages));
        context = null;
        try {
            // start OSGi container ..
            framework.start();
        } catch (org.osgi.framework.BundleException ex) {
            // failed to start/initiate framework
            throw new OSGiException("Failed to start OSGi framework.", ex);
        }

        try {
            context = framework.getBundleContext();
        } catch (SecurityException ex) {
            // we have to stop framework ..
            stop();
            throw new OSGiException("Failed to get OSGi context.", ex);
        }
    }

    /**
     * Stop OSGi framework. Does not uninstall installed bundles.
     */
    public void stop() {
        try {
            if (framework != null) {
                // stop equinox
                framework.stop();
            }
        } catch (Exception e) {
            // we can't throw here .. 
        }
        framework = null;
        context = null;
    }

    /**
     * Install bundle into framework.
     *
     * @param uri Uri to install bundle from.
     * @return BundleContainer or null.
     * @throws ModuleException
     */
    public BundleContainer installBundle(String uri) throws OSGiException {
        // has bundle been already loaded?
        if (loadedBundles.containsKey(uri)) {
            return loadedBundles.get(uri);
        }
        // load bundle
        BundleContainer bundleContainer = null;
        try {
            Bundle bundle = context.installBundle(uri);
            bundleContainer = new BundleContainer(bundle);
            // add bundle to the loaded bundle list and reverse list
            loadedBundles.put(uri, bundleContainer );
            reverseLoadedBundles.put(bundle, uri);
        } catch (org.osgi.framework.BundleException ex) {
            throw new OSGiException(ex);
        }
        return bundleContainer;
    }

    /**
     * Uninstall bundle from framework.
     *
     * @param bundle Bundle to uninstall.
     * @return False if exception was thrown during uninstalling.
     */
    private boolean uninstallBundle(Bundle bundle) {
        try {
            if (bundle == null) {
                // already cleaned ?
            } else {
                bundle.uninstall();
                // remove record about bundle
                String key = reverseLoadedBundles.get(bundle);
                // remove records related to bundle
                loadedBundles.remove(key);
                reverseLoadedBundles.remove(bundle);
            }
        } catch (org.osgi.framework.BundleException ex) {
            return false;
        }
        return true;
    }

    /**
     * Load BaseDPU class from bundle.
     *
     * @param uri Uri to bundle.
     * @return Loaded BaseDPU class.
     */
    public DPUExecutive loadDPU(String uri) throws OSGiException {
        // load bundle
        BundleContainer bundleContainer = installBundle(uri);
        
        String className = (String)
        		bundleContainer.getBundle().getHeaders().get("Bundle-Name");
        
        if (className == null) {
            throw new OSGiException(
                    "Wrong header, can't get Bundle-Name value.", null);
        }
        // prefix with package
        className = baseDpuClassName + className;
        // load object
        Object loadedObject = bundleContainer.loadClass(className);
        

        // dpu store loaded BaseDPU instance
        DPUExecutive dpu = null;
        try {
            dpu = (DPUExecutive) loadedObject;
        } catch (Exception e) {
            throw new OSGiException(e);
        }

        return dpu;
    }

}
