package cz.cuni.xrg.intlib.commons.app.module;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import cz.cuni.xrg.intlib.commons.app.Application;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;

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
	 * Contains list of common packages to export. If not empty must end by
	 * comma.
	 */
	private static final String PACKAGE_BASE = "";

	/**
	 * Path to the root directory, does not end on file separator.
	 */
	private String rootDirectory;

	/**
	 * List additional package that should be expose from application.
	 */
	private String aditionalPackages;

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
		this.aditionalPackages = conf.getString(Application.FRONTEND
				.equals(app)
				? ConfigProperty.MODULE_FRONT_EXPOSE
				: ConfigProperty.MODULE_BACK_EXPOSE);
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
		return PACKAGE_BASE + aditionalPackages;
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
