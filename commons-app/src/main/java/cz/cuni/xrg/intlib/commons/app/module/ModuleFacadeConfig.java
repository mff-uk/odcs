package cz.cuni.xrg.intlib.commons.app.module;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.xrg.intlib.commons.app.Application;
import cz.cuni.xrg.intlib.commons.configuration.AppConfig;
import cz.cuni.xrg.intlib.commons.configuration.ConfigProperty;
import cz.cuni.xrg.intlib.commons.configuration.MissingConfigPropertyException;

/**
 * Contains settings for ModuleFacade;
 * 
 * @author Petyr
 * 
 */
public class ModuleFacadeConfig {

	private static final Logger LOG = LoggerFactory.getLogger(ModuleFacadeConfig.class);
	
	/**
	 * Name for directory where DPUs are stored.
	 */
	public static final String DPU_DIRECTORY = "dpu";

	/**
	 * Name for directory with libraries.
	 */
	private static final String LIB_DIRECTORY = "lib";

	/**
	 * Name for directory in {@link #LIB_DIRECTORY} where special libraries for
	 * backend (Vaadin, .. ) are stored.
	 */
	private static final String LIB_BACKEND_DIRECTORY = "backend";

	/**
	 * Contains list of common packages to export. Must not end with comma.
	 */
	private static final String PACKAGE_BASE = "cz.cuni.xrg.intlib.commons;version=\"0.0.1\","
			+ "cz.cuni.xrg.intlib.commons.configuration;version=\"0.0.1\","
			+ "cz.cuni.xrg.intlib.commons.context;version=\"0.0.1\","
			+ "cz.cuni.xrg.intlib.commons.data;version=\"0.0.1\","
			+ "cz.cuni.xrg.intlib.commons.dpu;version=\"0.0.1\","
			+ "cz.cuni.xrg.intlib.commons.dpu.annotation;version=\"0.0.1\","
			+ "cz.cuni.xrg.intlib.commons.message;version=\"0.0.1\","
			+ "org.openrdf.rio,"
			+ "org.apache.log4j,org.slf4j;version=\"1.7.5\","
			// RDF package
			+ "cz.cuni.xrg.intlib.rdf.enums,"
			+ "cz.cuni.xrg.intlib.rdf.exceptions,"
			+ "cz.cuni.xrg.intlib.rdf.impl,"
			+ "cz.cuni.xrg.intlib.rdf.interfaces,"
			// library for configuration serialisation
			+ "com.thoughtworks.xstream,"
			// java packages
			+ "java.lang";

	/**
	 * Contains list of packages exported from frontend. Does not start nor end
	 * on separator.
	 */
	private static final String FRONTEND_BASE = "cz.cuni.xrg.intlib.commons.web;version=\"0.0.1\"";

	/**
	 * Path to the root directory, does not end on file separator.
	 */
	private String rootDirectory;

	/**
	 * List additional package that should be expose from application.
	 */
	private String additionalPackages;

	/**
	 * If true then libraries from {#link {@link #LIB_BACKEND_DIRECTORY} are
	 * also loaded.
	 */
	private boolean useBackendLibs;

	/**
	 * Append new packages to the current one, insert separator if needed.
	 * 
	 * @param packages The current packages, can be empty.
	 * @param toAdd String with list of new packages. Must not start nor end
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
	public ModuleFacadeConfig(AppConfig conf, Application app) {
		this.rootDirectory = conf.getString(ConfigProperty.MODULE_PATH);
		// check ending for trailing
		if (this.rootDirectory.endsWith("\\")
				|| this.rootDirectory.endsWith("/")) {
			// remove last character
			this.rootDirectory = this.rootDirectory.substring(0,
					this.rootDirectory.length() - 1);
		}

		StringBuilder packageList = new StringBuilder();
		try {
			String configPackages = conf.getString(Application.FRONTEND
					.equals(app)
					? ConfigProperty.MODULE_FRONT_EXPOSE
					: ConfigProperty.MODULE_BACK_EXPOSE);
			packageList.append(configPackages);
		} catch (MissingConfigPropertyException e) {
			// missing configuration -> use empty
		}

		if (Application.FRONTEND.equals(app)) {
			// frontend is running -> we need to export Vaadin packages as well
			appendPackages(packageList, com.vaadin.PackageList.PACKAGES);
			appendPackages(packageList, FRONTEND_BASE);
			// print message
			LOG.info("com.vaadin version: {}", com.vaadin.PackageList.VERSION);
		}

		// in every case add org.seasame packages
		appendPackages(packageList, org.openrdf.PackageList.PACKAGES);

		this.additionalPackages = packageList.toString();
		// check if load data from backend's library directory
		this.useBackendLibs = !Application.FRONTEND.equals(app);
	}

	/**
	 * The path does not end on file separator
	 * 
	 * @return
	 */
	public String getDpuFolder() {
		return rootDirectory + File.separator + DPU_DIRECTORY;
	}

	public String getPackagesToExpose() {
		if (additionalPackages.isEmpty()) {
			// no additional packages
			return PACKAGE_BASE;
		} else {
			// add separator
			return PACKAGE_BASE + "," + additionalPackages;
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
		if (useBackendLibs) {
			result.add(rootDirectory + File.separator + LIB_DIRECTORY
					+ File.separator + LIB_BACKEND_DIRECTORY);
		}
		result.add(rootDirectory + File.separator + LIB_DIRECTORY);

		return result;
	}

}
