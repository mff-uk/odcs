package cz.cuni.mff.xrg.odcs.commons.app.module.osgi;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.Application;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.conf.MissingConfigPropertyException;
import cz.cuni.mff.xrg.odcs.commons.app.module.osgi.packages.commons;
import cz.cuni.mff.xrg.odcs.commons.app.module.osgi.packages.commons_module;
import cz.cuni.mff.xrg.odcs.commons.app.module.osgi.packages.commons_web;
import cz.cuni.mff.xrg.odcs.commons.app.module.osgi.packages.dataunit_file;
import cz.cuni.mff.xrg.odcs.commons.app.module.osgi.packages.ontology;
import cz.cuni.mff.xrg.odcs.commons.app.module.osgi.packages.openrdf;
import cz.cuni.mff.xrg.odcs.commons.app.module.osgi.packages.rdf;
import cz.cuni.mff.xrg.odcs.commons.app.module.osgi.packages.vaadin;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * Contains settings for OSGIModuleFacade;
 * 
 * @author Petyr
 */
class OSGIModuleFacadeConfig {
	
    private static final Logger LOG = LoggerFactory.getLogger(
            OSGIModuleFacadeConfig.class);

    /**
     * Name for directory where DPUs are stored.
     */
    public static final String DPU_DIRECTORY = "dpu";

    /**
     * Name for directory with libraries.
     */
    private static final String LIB_DIRECTORY = "lib";

    /**
     * Contains list of common packages to export. Must not end with comma.
     */
    private static final String PACKAGE_BASE =
            "cz.cuni.mff.xrg.odcs.commons;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.commons.configuration;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.commons.context;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.commons.data;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.commons.dpu;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.commons.dpu.annotation;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.commons.message;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.commons.httpconnection.utils;version=\"1.0.0\","
                    // RDF package
                    + "org.openrdf.rio,"
                    + "cz.cuni.mff.xrg.odcs.filelist;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.rdf;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.rdf.data;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.rdf.enums;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.rdf.exceptions;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.rdf.interfaces;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.rdf.impl;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.rdf.repositories;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.rdf.validators;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.rdf.handlers;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.rdf.impl;version=\"1.0.0\","
                    + "cz.cuni.mff.xrg.odcs.rdf.help;version=\"1.0.0\"";

    /**
     * Contains list of packages exported from frontend. Does not start nor end
     * on separator.
     */
    private static final String FRONTEND_BASE =
            "cz.cuni.mff.xrg.odcs.commons.web;version=\"1.0.0\"";

    /**
     * Path to the root directory, does not end on file separator.
     */
    private String rootDirectory;

    /**
     * List additional package that should be expose from application.
     */
    private final String additionalPackages;

    /**
     * If true then libraries from {#link {@link #LIB_BACKEND_DIRECTORY} are
     * also loaded.
     * This value is not used now as there are no libraries only for backend.
     */
    private final boolean useBackendLibs;

    /**
     * Append new packages to the current one, insert separator if needed.
     * 
     * @param packages
     *            The current packages, can be empty.
     * @param toAdd
     *            String with list of new packages. Must not start nor end
     *            with separator.
     */
    private void appendPackages(StringBuilder packages, String toAdd) {
        final int length = packages.length();
        if (length == 0 || packages.charAt(length - 1) == ',') {
            // no separator need
        } else {
            // add separator
            packages.append(',');
        }
        // add packages
        packages.append(toAdd);
    }

    /**
     * Module configuration is constructed directly from {@link AppConfig}.
     * 
     * @param conf
     */
    public OSGIModuleFacadeConfig(AppConfig conf, Application app) {
        this.rootDirectory = conf.getString(ConfigProperty.MODULE_PATH);
        // check ending for trailing
        if (this.rootDirectory.endsWith("\\")
                || this.rootDirectory.endsWith("/")) {
            // remove last character
            this.rootDirectory = this.rootDirectory.substring(0,
                    this.rootDirectory.length() - 1);
        }

        LOG.debug("Instance: {} ", app.toString());

        StringBuilder packageList = new StringBuilder();
        try {
            String configPackages = conf.getString(Application.FRONTEND
                    .equals(app)
                    ? ConfigProperty.MODULE_FRONT_EXPOSE
                    : ConfigProperty.MODULE_BACK_EXPOSE);
            LOG.debug("Packages from config: {}", configPackages);
            packageList.append(configPackages);
        } catch (MissingConfigPropertyException e) {
            // missing configuration -> use empty
        }

        //if (Application.FRONTEND.equals(app)) 
        // the dependencies are now the same .. so backend 
        // and frontend exports the same packages
        {
            // frontend is running -> we need to export Vaadin packages as well
            appendPackages(packageList, vaadin.PACKAGES);
            appendPackages(packageList, FRONTEND_BASE);
            // print message
            LOG.info("com.vaadin version: {}", vaadin.VERSION);
        }

        // in every case add org.seasame packages
        appendPackages(packageList, openrdf.PACKAGES);
        // and system packages
        appendPackages(packageList, OSGISystemPackages.PACKAGES);
        // append packages from commons, commons-module, commons-web
        appendPackages(packageList, commons.PACKAGE_LIST);
        appendPackages(packageList, commons_web.PACKAGE_LIST);
        appendPackages(packageList, commons_module.PACKAGE_LIST);
        appendPackages(packageList, rdf.PACKAGE_LIST);
        appendPackages(packageList, dataunit_file.PACKAGE_LIST);
        appendPackages(packageList, ontology.PACKAGE_LIST);

        this.additionalPackages = packageList.toString();
        // check if load data from backend's library directory
        this.useBackendLibs = !Application.FRONTEND.equals(app);
    }

    /**
     * The path does not end on file separator
     * 
     * @return
     */
    public String getDPUDirectory() {
        return rootDirectory + File.separator + DPU_DIRECTORY;
    }

    public String getPackagesToExpose() {
        Resource resource = new ClassPathResource("/osgiModuleFacadeConfig.properties");
        String list = "";
        String delimiter = ",";
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            Enumeration enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = properties.getProperty(key);
                if(enumeration.hasMoreElements())
                    list += key + "=" + value + delimiter;
                else{
                    list += key + "=" + value;
                }
            }

            LOG.debug("list of package to expose");
        } catch (IOException e) {
            LOG.error("Error", e);
        }


        if (additionalPackages.isEmpty()) {
            // no additional packages
            return list;
        } else {
            // add separator
            return list + "," + additionalPackages;
        }
    }

    /**
     * Return list that contains path to directories with libraries. The path
     * does not end on file separator.
     * 
     * @return
     */
    public List<String> getDpuLibFolder() {
        List<String> result = new LinkedList<>();
        result.add(rootDirectory + File.separator + LIB_DIRECTORY);

        return result;
    }
}
