package cz.cuni.xrg.intlib.commons.app.module;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import cz.cuni.xrg.intlib.commons.app.Application;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.conf.MissingConfigPropertyException;

/**
 * Contains settings for ModuleFacade;
 * 
 * @author Petyr
 * 
 */
public class ModuleFacadeConfig {

	/**
	 * Name for directory where DPUs are stored.
	 */
	private static final String DPU_DIRECTORY = "dpu";

	/**
	 * Name for directory with libraries.
	 */
	private static final String LIB_DIRECTORY = "lib";

	/**
	 * Name for directory in {@link #LIB_DIRECTORY} where special libs for
	 * backend (Vaadin, .. ) are stored.
	 */
	private static final String LIB_BACKEND_DIRECTORY = "backend";

	/**
	 * Contains list of common packages to export. Must not end with comma.
	 */
	private static final String PACKAGE_BASE = 
			"cz.cuni.xrg.intlib.commons;version=\"0.0.1\"," +
		    "cz.cuni.xrg.intlib.commons.configuration;version=\"0.0.1\"," +
			"cz.cuni.xrg.intlib.commons.data;version=\"0.0.1\"," +
		    "cz.cuni.xrg.intlib.commons.data.rdf;version=\"0.0.1\"," + 
			"cz.cuni.xrg.intlib.commons.event;version=\"0.0.1\"," + 
		    "cz.cuni.xrg.intlib.commons.extractor;version=\"0.0.1\"," + 
			"cz.cuni.xrg.intlib.commons.loader;version=\"0.0.1\"," + 
		    "cz.cuni.xrg.intlib.commons.message;version=\"0.0.1\"," + 
			"cz.cuni.xrg.intlib.commons.transformer;version=\"0.0.1\"," +
			"org.openrdf.rio," + 
			"org.apache.log4j,org.slf4j;version=\"1.7.5\"," +
			// RDF package
			"cz.cuni.xrg.intlib.rdf.enums," + 
			"cz.cuni.xrg.intlib.rdf.exceptions," +
			"cz.cuni.xrg.intlib.rdf.impl," +
			"cz.cuni.xrg.intlib.rdf.interfaces," +
			// lib for serialisation
			"com.thoughtworks.xstream," + 
			// java packages
			"java.lang,javax";
	
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
	 * Module configuration is constructed directly from {@link AppConfig}.
	 * 
	 * @param conf
	 */
	public ModuleFacadeConfig(AppConfig conf, Application app) {
		this.rootDirectory = conf.getString(ConfigProperty.MODULE_PATH);
		try	{
		this.additionalPackages = conf.getString(Application.FRONTEND
				.equals(app)
				? ConfigProperty.MODULE_FRONT_EXPOSE
				: ConfigProperty.MODULE_BACK_EXPOSE);
		} catch (MissingConfigPropertyException e) {
			// missing configuration -> use empty
			this.additionalPackages = "";
		}
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
