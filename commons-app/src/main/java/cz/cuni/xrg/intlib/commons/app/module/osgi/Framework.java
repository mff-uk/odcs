package cz.cuni.xrg.intlib.commons.app.module.osgi;

import java.util.Collection;
import java.util.Dictionary;

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
    private java.util.Map<String, Bundle> loadedBundles = new java.util.HashMap<>();
    /**
     * Store keys for stored bundles in loadedBundles;
     */
    private java.util.Map<Bundle, String> reverseLoadedBundles = new java.util.HashMap<>();
    /**
     * Store bundle class.
     */
    private java.util.Map<String, Class<?>> loadedClassCtors = new java.util.HashMap<>();

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
                // commons
                "cz.cuni.xrg.intlib.commons,"
                + "cz.cuni.xrg.intlib.commons.configuration,"
                + "cz.cuni.xrg.intlib.commons.event,"
                + "cz.cuni.xrg.intlib.commons.extractor,"
                + "cz.cuni.xrg.intlib.commons.loader,"
                + "cz.cuni.xrg.intlib.commons.transformer,"
                + // commons-web
                "cz.cuni.xrg.intlib.commons.web,"
                + // commons-module
                "cz.cuni.xrg.intlib.commons.repository"
                + exportedPackages);
        return config;
    }
    /*
	
     Export-Package: 
     module;uses:="cz.cuni.xrg.intlib.commons.web,gui,cz.cuni.xrg.intlib.commons,cz.cuni.xrg.intlib.commons.configuration,cz.cuni.xrg.intlib.commons.repository,cz.cuni.xrg.intlib.commons.extractor,com.vaadin.ui";version="0.0.2",
     gui;uses:="com.vaadin.data,module,cz.cuni.xrg.intlib.commons.configuration,com.vaadin.ui";version="0.0.2"
			 
     Import-Package: 
     com.vaadin.data,
     com.vaadin.ui,
     cz.cuni.xrg.intlib.commons
     cz.cuni.xrg.intlib.commons.configuration,
     cz.cuni.xrg.intlib.commons.extractor,
     cz.cuni.xrg.intlib.commons.web,
     cz.cuni.xrg.intlib.commons.repository	
     */

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

        this.framework = frameworkFactory.newFramework(prepareSettings(exportedPackages));
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
            if (this.framework != null) {
                // stop equinox
                this.framework.stop();
            }
        } catch (Exception e) {
            // we can't throw here .. 
        }
        this.framework = null;
        this.context = null;
    }

    /**
     * Install bundle into framework.
     *
     * @param uri Uri to install bundle from.
     * @return Installed bundle or null.
     * @throws ModuleException
     */
    public Bundle installBundle(String uri) throws OSGiException {
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
            throw new OSGiException("Failed to load bundle from uri: " + uri, ex);
        }
        return bundle;
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
     *
     * @return False if exception was thrown during uninstallation of any
     * module.
     */
    public boolean uninstallBundles() {
        boolean result = true;

        Collection<Bundle> toDelete = this.loadedBundles.values();
        for (Bundle item : toDelete) {
            try {
                item.uninstall();
            } catch (org.osgi.framework.BundleException ex) {
                // can't throw ..
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
     *
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
        } else {
            // load class from bundle

            // we need construct class name
            String className = this.baseDpuClassName;
            Dictionary<String, String> header = bundle.getHeaders();
            String bundleName = header.get("Bundle-Name");

            if (bundleName == null) {
                // wring size .. 
                uninstallBundle(bundle);
                throw new OSGiException(
                        "Wrong header, can't get Bundle-Name value.", null);
            } else {
                className += bundleName;
            }

            try {
                // load class from bundle
                loaderClass = bundle.loadClass(className);
                // store loaded class
                this.loadedClassCtors.put(uri, loaderClass);
            } catch (ClassNotFoundException ex) {
                System.out.println("Ex:" + ex.getMessage());

                // uninstall bundle and throw
                uninstallBundle(bundle);
                throw new OSGiException("Failed to load class from bundle.", ex);
            }
        }

        // dpu store loaded BaseDPU instance
        DPUExecutive dpu = null;
        try {
            dpu = (DPUExecutive) loaderClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            // uninstall bundle and throw
            uninstallBundle(bundle);
            throw new OSGiException("Failed to create a instance of class.", ex);
        }

        return dpu;
    }

    @Deprecated
    public void HACK_startBundle(String uri) throws ModuleException {
        // has bundle been already loaded?
        if (this.loadedBundles.containsKey(uri)) {
            try {
                this.loadedBundles.get(uri).start();
            } catch (org.osgi.framework.BundleException e) {
                throw new ModuleException("Can't start bundle: ", e);
            }
        } else {
            throw new RuntimeException("Can't find bundle.");
        }
    }

    @Deprecated
    public org.osgi.framework.launch.Framework HACK_getFramework() {
        return this.framework;
    }

    @Deprecated
    public java.util.Map<String, Bundle> HACK_installed() {
        return this.loadedBundles;
    }
}
