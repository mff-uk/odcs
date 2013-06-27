package cz.cuni.xrg.intlib.commons.app.module;

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
	 * Folder with dpu to load during execution. 
	 * Without file:/// prefix.
	 */
	private String dpuFolder;

	/**
	 * List package that should be expose from application.
	 */
	private String packagesToExpose = "";
	
	/**
	 * Folder with dpu libraries.
	 * Without file:/// prefix.
	 */
	private String dpuLibsFolder = "";

	/**
	 * Module configuration is constructed directly from {@link AppConfig}.
	 *
	 * @param conf
	 */
	public ModuleFacadeConfig(AppConfig conf, Application app) {
		dpuFolder = conf.getString(ConfigProperty.MODULE_PATH);
		dpuLibsFolder = conf.getString(ConfigProperty.MODULE_LIBS);
		packagesToExpose = conf.getString(Application.FRONTEND.equals(app)
			? ConfigProperty.MODULE_FRONT_EXPOSE : ConfigProperty.MODULE_BACK_EXPOSE);
	}

	public String getDpuFolder() {
		return dpuFolder;
	}

	public void setDpuFolder(String dpuFolder) {
		this.dpuFolder = dpuFolder;
	}

	public String getPackagesToExpose() {
		return packagesToExpose;
	}

	public void setPackagesToExpose(String packagesToExpose) {
		this.packagesToExpose = packagesToExpose;
	}

	public String getDpuLibsFolder() {
		return dpuLibsFolder;
	}

	public void setDpuLibsFolder(String dpuLibsFolder) {
		this.dpuLibsFolder = dpuLibsFolder;
	}
}
